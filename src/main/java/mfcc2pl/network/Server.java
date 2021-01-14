package mfcc2pl.network;

import mfcc2pl.utilities2pl.Lock;
import mfcc2pl.utilities2pl.Transaction;
import mfcc2pl.utilities2pl.WaitForGraphNode;
import mfcc2pl.utilities2pl.operations.Operation;
import oracle.ucp.util.Pair;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private static final int PORT = 8100;
    private ServerSocket serverSocket;
    private boolean running = false;
    private int nrClients = 0;

    protected List<Transaction> transactions;
    protected List<Lock> locks;
    protected List<WaitForGraphNode> waitForGraph;
    protected Integer transactionId; // as sequence counter
    protected Integer lockId; // as sequence counter
    protected Integer commitNr; // for keeping track of the order of commit of the transactions

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.init();
        server.waitForClients();
    }

    public void init() throws IOException {
        setServerSocket(new ServerSocket(PORT));
        running = true;
        transactions = new ArrayList<>();
        locks = new ArrayList<>();
        waitForGraph = new ArrayList<>();
        transactionId = 1;
        lockId = 1;
        commitNr = 1;
    }

    public void waitForClients() throws IOException {
        try {
            new DeadlockThread(this).start();
            while (running) {
                System.out.println("Waiting for a client ...");
                Socket socket = getServerSocket().accept();
                System.out.println("A client has arrived...");
                this.setNrClients(this.getNrClients() + 1);
                new ClientThread(this, this.getServerSocket(), socket).start();
            }
        } catch (IOException e) {
            System.err.println("Ooops... " + e);
        } finally {
            getServerSocket().close();
        }
    }

    public void stop() throws IOException {
        this.running = false;
        getServerSocket().close();
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public int getNrClients() {
        return nrClients;
    }

    public void setNrClients(int nrClients) {
        this.nrClients = nrClients;
    }

    public synchronized Transaction getTransaction(Integer id) {
        // get the transaction with a specified id
        return transactions.stream()
                .filter(transaction -> transaction.getId() == id)
                .findAny()
                .orElse(null);
    }

    public synchronized void setTransaction(Transaction transaction) {
        transactionId++;
        transactions.add(transaction);
    }

    public synchronized Integer getTransactionHoldingIncompatibleLock(Operation operation) {
        // get the transaction id holding an incompatible lock or null if there is no such transaction
        if (operation.getName().equals("select")) {
            // if there is any write lock on the table
            Lock lockToFind = locks.stream()
                    .filter(lock -> lock.getType().equals("write")
                            && lock.getTable().equals(operation.getTableName()))
                    .findAny()
                    .orElse(null);
            if (lockToFind == null) { // no write lock
                return null;
            } else { // the transaction holding the lock
                return lockToFind.getTransactionId();
            }
        } else {
            // if there is any lock on the table
            Lock lockToFind = locks.stream()
                    .filter(lock -> lock.getTable().equals(operation.getTableName()))
                    .findAny()
                    .orElse(null);
            if (lockToFind == null) { // no lock
                return null;
            } else { // the transaction holding the lock
                return lockToFind.getTransactionId();
            }
        }
    }

    public synchronized void block(Integer transactionId, Operation operation) {
        // block a table in read/write mode
        String lockType;
        if (operation.getName().equals("select")) {
            lockType = "read";
        } else {
            lockType = "write";
        }
        locks.add(new Lock(lockId, lockType, operation.getTableName(), transactionId));
        lockId++;
    }

    public synchronized void wait(Integer transactionIdHasLock, Integer transactionIdToWait, Operation operation) {
        // wait for a lock
        String lockType;
        if (operation.getName().equals("select")) {
            lockType = "read";
        } else {
            lockType = "write";
        }
        synchronized (waitForGraph) {
            waitForGraph.add(new WaitForGraphNode(lockType, operation.getTableName(), transactionIdHasLock, transactionIdToWait));
        }
    }

    public synchronized boolean hasLock(String type, Integer transactionId, String tableName) {
        // check if a transaction possesses a lock on a table
        Lock lockToFind = locks.stream()
                .filter(lock -> lock.getType().equals(type)
                        && lock.getTable().equals(tableName)
                        && lock.getTransactionId() == transactionId)
                .findAny()
                .orElse(null);
        return lockToFind != null;
    }

    public synchronized void releaseAndDistributeLocks(Integer transactionId) { // the transaction releasing the locks
        // release the locks of a committed/aborted transaction and give them to the transaction waiting for them for the longest time
        List<Lock> locksToBeRemoved = new ArrayList<>();

        for (Lock lock : locks) {
            if (lock.getTransactionId() == transactionId) {
                locksToBeRemoved.add(lock);
            }
        }

        for (Lock lock : locksToBeRemoved) {
            if (lock.getType().equals("read")) { // if read lock
                // we check to see if it also has the write lock on that table
                Lock writeLock = locksToBeRemoved.stream()
                        .filter(lock2 -> lock2.getType().equals("write")
                                && lock2.getTable().equals(lock.getTable()))
                        .findAny()
                        .orElse(null);

                if (writeLock == null) { // only read lock, so give it to someone waiting for a write lock on that table
                    giveLock(lock, transactionId, "write");
                } else { // also has write lock, so give it to someone waiting for a read/write lock on that table
                    giveLock(lock, transactionId, "any");
                }
            } else {
                giveLock(lock, transactionId, "any");
            }
        }
        locks.removeAll(locksToBeRemoved);
    }

    public void giveLock(Lock lock, Integer transactionId, String lockTypeWanting) {
        WaitForGraphNode waitForGraphNode = null;
        synchronized (waitForGraph) { // get the transaction (the one who has waited the most) waiting for ...
            if (lockTypeWanting.equals("any")) { // a read/write lock on that table
                waitForGraphNode = waitForGraph.stream()
                        .filter(wfgn -> wfgn.getTable().equals(lock.getTable())
                                && wfgn.getTransactionIdHasLock() == transactionId)
                        .findAny()
                        .orElse(null);
            } else if (lockTypeWanting.equals("write")) { // a write lock on that table
                waitForGraphNode = waitForGraph.stream()
                        .filter(wfgn -> wfgn.getLockType().equals("write")
                                && wfgn.getTable().equals(lock.getTable())
                                && wfgn.getTransactionIdHasLock() == transactionId)
                        .findAny()
                        .orElse(null);
            }
            if (waitForGraphNode != null) {
                // give it the lock
                locks.add(new Lock(lockId, waitForGraphNode.getLockType(), lock.getTable(), waitForGraphNode.getTransactionIdWaitsLock()));
                lockId++;

                // remove the element in the wait-for graph
                waitForGraph.remove(waitForGraphNode);

                // announce all that were waiting for the same lock that tha transaction holding it has changed
                for (WaitForGraphNode waitForGraphNode2 : waitForGraph) {
                    if (waitForGraphNode2.getTable().equals(lock.getTable())
                            && waitForGraphNode2.getTransactionIdHasLock() == transactionId) {
                        waitForGraphNode2.setTransactionIdHasLock(waitForGraphNode.getTransactionIdWaitsLock());
                    }
                }
            }
        }
    }

    public synchronized void setStatus(Integer transactionId, String status) {
        // set transaction status (to committed/aborted)
        getTransaction(transactionId).setStatus(status);
        if (status.equals("aborted")) {
            getTransaction(transactionId).setOperations(new ArrayList<>());
        } else {
            getTransaction(transactionId).setCommitNr(commitNr++);
            getTransaction(transactionId).setCommitTs(new Timestamp(System.currentTimeMillis()));
        }
    }

    public synchronized int hasToWait(Integer transactionId, String lockType, String tableName) {
        // returns 0 - the transaction has to wait no longer as it was aborted
        // returns 1 - the transaction has to keep waiting
        // returns 2 - the lock the transaction was waiting for was received
        Lock lockToFind = locks.stream()
                .filter(lock -> lock.getType().equals(lockType)
                        && lock.getTable().equals(tableName)
                        && lock.getTransactionId() == transactionId)
                .findAny()
                .orElse(null);
        String status = getTransaction(transactionId).getStatus();
        if (status.equals("aborted")) {
            return 0; // aborted
        }
        if (lockToFind == null) {
            return 1; // keep waiting
        } else {
            return 2; // lock received
        }
    }

    public Pair<Integer, Integer> hasSimpleDeadlock() {
        WaitForGraphNode waitForGraphNode;
        // check if there is an element for which there exists a reversed element (hasLock <-> waitsLock)
        synchronized (waitForGraph) {
            waitForGraphNode = waitForGraph.stream()
                    .filter(wfgn -> transactionIdInConflict(wfgn) != null)
                    .findAny()
                    .orElse(null);
        }

        if (waitForGraphNode == null) { // no deadlock
            return null;
        } else {
            // returns the pair - the id of the transaction that has waited less, the id of the transaction that has waited more
            return new Pair(waitForGraphNode.getTransactionIdWaitsLock(), waitForGraphNode.getTransactionIdHasLock());
        }
    }

    private Integer transactionIdInConflict(WaitForGraphNode wfgn) {
        synchronized (waitForGraph) {
            int wfgnIndex = waitForGraph.indexOf(wfgn);
            if (wfgnIndex != -1) { // not a null element
                for (int i = wfgnIndex + 1; i < waitForGraph.size(); i++) {
                    // reversed
                    if (wfgn.getTransactionIdHasLock() == waitForGraph.get(i).getTransactionIdWaitsLock()
                            && wfgn.getTransactionIdWaitsLock() == waitForGraph.get(i).getTransactionIdHasLock()) {
                        return waitForGraph.get(i).getTransactionIdHasLock(); // we return the one with which it is in conflict
                    }
                }
            }
        }

        return null;
    }

    public void resolveDeadlock(Connection conn1, Connection conn2, Pair<Integer, Integer> transactionsInConflict) {
        // rollback to the transaction which has waited the least of the two
        getTransaction(transactionsInConflict.get1st()).rollback(conn1, conn2, getTransaction(transactionsInConflict.get1st()).getOperations().size() - 1);
        // release and distribute its locks
        releaseAndDistributeLocks(transactionsInConflict.get1st());
        // set its status to aborted
        setStatus(transactionsInConflict.get1st(), "aborted");

        // clean the wait-for graph of the element for the transaction continuing executing
        synchronized (waitForGraph) {
            waitForGraph.removeIf(wfgn -> wfgn.getTransactionIdHasLock() == transactionsInConflict.get2nd()
                    && wfgn.getTransactionIdWaitsLock() == transactionsInConflict.get1st());
        }
    }

    public synchronized void displayManagementEntities(Integer transactionId) {
        System.out.println(" ");
        System.out.println("************************* " + transactionId + " *************************");
        displayTransactions();
        displayLocks();
        displayWaitForGraph();
        System.out.println(" ");
    }

    public void displayTransactions() {
        System.out.println("Transactions: ");
        for (Transaction transaction : this.transactions) {
            if (transaction.getStatus().equals("committed")) {
                System.out.println(transaction.getId() + " *** " + transaction.getTs() + " *** " + transaction.getStatus() + " *** #" + transaction.getCommitNr() + " *** " + transaction.getCommitTs());
            } else {
                System.out.println(transaction.getId() + " *** " + transaction.getTs() + " *** " + transaction.getStatus());
            }
            for (Operation operation : transaction.getOperations()) {
                System.out.println("\t" + operation);
            }
        }
    }

    public void displayLocks() {
        System.out.println("Locks: ");
        for (Lock lock : this.locks) {
            System.out.println(lock);
        }
    }

    public void displayWaitForGraph() {
        System.out.println("Wait-For Graph: ");
        for (WaitForGraphNode waitForGraphNode : this.waitForGraph) {
            System.out.println(waitForGraphNode);
        }
    }
}

package mfcc2pl.network;

import mfcc2pl.Utilities;
import mfcc2pl.utilities2pl.Lock;
import mfcc2pl.utilities2pl.Transaction;
import mfcc2pl.utilities2pl.WaitForGraphNode;
import mfcc2pl.utilities2pl.operations.Operation;
import oracle.ucp.util.Pair;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
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
    protected Integer transactionId;
    protected Integer lockId;

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
        transactionId = 0;
        lockId = 0;
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
            Utilities.store(0, "..\\2PL\\src\\main\\resources\\current_transaction.txt");
        } finally {
            getServerSocket().close();
            Utilities.store(0, "..\\2PL\\src\\main\\resources\\current_transaction.txt");
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
        return transactions.stream()
                .filter(transaction -> transaction.getId() == id)
                .findAny()
                .orElse(null);
    }

    public synchronized Integer getTransactionHoldingIncompatibleLock(Operation operation) {
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
            if (lockToFind == null) { // no write lock
                return null;
            } else { // the transaction holding the lock
                return lockToFind.getTransactionId();
            }
        }
    }

    public synchronized void block(Integer transactionId, Operation operation) {
        String lockType = "";
        if (operation.getName().equals("select")) {
            lockType = "read";
        } else {
            lockType = "write";
        }
        locks.add(new Lock(lockId, lockType, operation.getTableName(), transactionId));
    }

    public synchronized void wait(Integer transactionIdHasLock, Integer transactionIdToWait, Operation operation) {
        String lockType = "";
        if (operation.getName().equals("select")) {
            lockType = "read";
        } else {
            lockType = "write";
        }
        waitForGraph.add(new WaitForGraphNode(lockType, operation.getTableName(), transactionIdHasLock, transactionIdToWait));
    }

    public synchronized boolean hasLock(String type, Integer transactionId, String tableName) {
        Lock lockToFind = locks.stream()
                .filter(lock -> lock.getType().equals(type)
                        && lock.getTable().equals(tableName)
                        && lock.getTransactionId() == transactionId)
                .findAny()
                .orElse(null);
        if (lockToFind == null) {
            return false;
        } else {
            return true;
        }
    }

    public synchronized void releaseAndDistributeLocks(Integer transactionId) {
        List<Lock> locksToBeRemoved = new ArrayList<Lock>();

        for (Lock lock : locks) {
            if (lock.getTransactionId() == transactionId) {
                locksToBeRemoved.add(lock);
            }
        }

        for (Lock lock : locksToBeRemoved) {
            if (lock.getType().equals("read")) {
                WaitForGraphNode waitForGraphNode = waitForGraph.stream()
                        .filter(wfgn -> wfgn.getLockType().equals(lock.getType())
                                && wfgn.getTable().equals(lock.getTable()))
                        .findAny()
                        .orElse(null);
                if (waitForGraphNode != null) {
                    locks.add(new Lock(lockId, lock.getType(), lock.getTable(), waitForGraphNode.getTransactionIdWaitsLock()));
                    lockId++;
                    waitForGraph.remove(waitForGraphNode);
                }
            } else {
                WaitForGraphNode waitForGraphNode = waitForGraph.stream()
                        .filter(wfgn -> wfgn.getTable().equals(lock.getTable()))
                        .findAny()
                        .orElse(null);
                if (waitForGraphNode != null) {
                    locks.add(new Lock(lockId, waitForGraphNode.getLockType(), lock.getTable(), waitForGraphNode.getTransactionIdWaitsLock()));
                    lockId++;
                    waitForGraph.remove(waitForGraphNode);
                }
            }
        }
        locks.removeAll(locksToBeRemoved);
    }

    public synchronized void setStatus(Integer transactionId, String status) {
        getTransaction(transactionId).setStatus(status);
    }

    public synchronized int isWaiting(Integer transactionId, String lockType, String tableName) {
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
        WaitForGraphNode waitForGraphNode = waitForGraph.stream()
                .filter(wfgn -> transactionIdInConflict(wfgn) != null)
                .findAny()
                .orElse(null);

        if (waitForGraphNode == null) {
            return null;
        } else {
            return new Pair(waitForGraphNode.getTransactionIdWaitsLock(), waitForGraphNode.getTransactionIdHasLock());
        }
    }

    private Integer transactionIdInConflict(WaitForGraphNode wfgn) {
        for (WaitForGraphNode wfgn2 : waitForGraph) {
            if (wfgn2 != wfgn) {
                if (wfgn.getTransactionIdHasLock() == wfgn2.getTransactionIdWaitsLock()
                        && wfgn.getTransactionIdWaitsLock() == wfgn2.getTransactionIdHasLock()) {
                    return wfgn2.getTransactionIdHasLock();
                }
            }
        }
        return null;
    }

    public void resolveDeadlock(Connection conn1, Connection conn2, Pair<Integer, Integer> transactionsInConflict) {
        getTransaction(transactionsInConflict.get1st()).rollback(conn1, conn2, getTransaction(transactionsInConflict.get1st()).getOperations().size() - 1);
        releaseAndDistributeLocks(transactionsInConflict.get1st());
        setStatus(transactionsInConflict.get1st(), "aborted");

        waitForGraph.removeIf(wfgn -> wfgn.getTransactionIdHasLock() == transactionsInConflict.get2nd()
                && wfgn.getTransactionIdWaitsLock() == transactionsInConflict.get1st());
    }

    public void displayTransactions() {
        System.out.println("Transactions: ");
        for (Transaction transaction : this.transactions) {
            System.out.println(transaction.getId() + " *** " + transaction.getTs() + " *** " + transaction.getStatus());
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

    public void displayManagementEntities(Integer transactionId) {
        System.out.println("");
        System.out.println("************************* " + transactionId + " *************************");
        displayTransactions();
        displayLocks();
        displayWaitForGraph();
        System.out.println("");
    }
}

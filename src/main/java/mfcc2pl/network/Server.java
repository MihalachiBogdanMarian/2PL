package mfcc2pl.network;

import mfcc2pl.Utilities;
import mfcc2pl.utilities2pl.Lock;
import mfcc2pl.utilities2pl.Transaction;
import mfcc2pl.utilities2pl.WaitForGraphNode;
import mfcc2pl.utilities2pl.operations.Operation;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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

    public void displayManagementEntities() {
        System.out.println("");
        displayTransactions();
        displayLocks();
        displayWaitForGraph();
        System.out.println("");
    }
}

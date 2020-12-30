package mfcc2pl.network;

import mfcc2pl.sqlutilities.dbconnection.Database;
import mfcc2pl.utilities2pl.Transaction;
import mfcc2pl.utilities2pl.operations.Operation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientThread extends Thread {

    private Socket socket = null;
    private final Server server;
    private final Connection conn1;
    private final Connection conn2;
    private Integer transactionId;

    public ClientThread(Server server, ServerSocket serverSocket, Socket clientSocket) {
        this.server = server;
        this.server.setServerSocket(serverSocket);
        this.socket = clientSocket;
        conn1 = Database.getConnection(1);
        conn2 = Database.getConnection(2);
        transactionId = -1;
    }

    @Override
    public void run() {
        ObjectInputStream objectInputStream = null;

        Transaction transaction;
        this.transactionId = server.transactionId;
        transaction = new Transaction(server.transactionId, new Timestamp(System.currentTimeMillis()), "active");
        synchronized (server.transactionId) {
            server.transactionId++;
        }
        synchronized (server.transactions) {
            server.transactions.add(transaction);
        }

        try {
            while (true) {
                objectInputStream = new ObjectInputStream(socket.getInputStream());
                PrintWriter out = new PrintWriter(socket.getOutputStream());

                Operation operation = (Operation) objectInputStream.readObject();

                if (operation.getName().equals("commit")) {
                    server.releaseAndDistributeLocks(this.transactionId);

                    server.setStatus(this.transactionId, "committed");
                    server.getTransaction(this.transactionId).addOperation(operation);

                    out.println("Commited");
                    out.flush();

                    break;
                }

                Integer transactionHoldingIncompatibleLock = server.getTransactionHoldingIncompatibleLock(operation);
                if (transactionHoldingIncompatibleLock == null) { // no incompatible lock
                    // acquire the lock
                    server.block(this.transactionId, operation);

                    synchronized (server.lockId) {
                        server.lockId++;
                    }

                    operation.execute(conn1, conn2);
                    server.getTransaction(this.transactionId).addOperation(operation);

                    out.println("Executed " + operation);
                    out.flush();

                    server.displayManagementEntities(this.transactionId);
                } else { // there is an incompatible lock
                    if (transactionHoldingIncompatibleLock == this.transactionId) { // if it is my write lock
                        // if select and I own only the write lock, I will acquire also the read lock on that table
                        if (operation.getName().equals("select") && !server.hasLock("read", this.transactionId, operation.getTableName())) {

                            server.block(this.transactionId, operation);

                            synchronized (server.lockId) {
                                server.lockId++;
                            }
                        }

                        operation.execute(conn1, conn2);
                        server.getTransaction(this.transactionId).addOperation(operation);

                        out.println("Executed " + operation);
                        out.flush();

                        server.displayManagementEntities(this.transactionId);
                    } else { // someone else has the incompatible lock, so I have to wait

                        server.wait(transactionHoldingIncompatibleLock, this.transactionId, operation);

                        String lockType = "";
                        if (operation.getName().equals("select")) {
                            lockType = "read";
                        } else {
                            lockType = "write";
                        }

                        out.println("Wait");
                        out.flush();

                        server.displayManagementEntities(this.transactionId);

                        int waitingStatus;
                        while (true) {
                            waitingStatus = server.isWaiting(transactionId, lockType, operation.getTableName());
                            if (waitingStatus == 0 || waitingStatus == 2) {
                                break;
                            }

                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        if (waitingStatus == 2) { // continue execution
                            operation.execute(conn1, conn2);
                            server.getTransaction(this.transactionId).addOperation(operation);

                            server.displayManagementEntities(this.transactionId);

                            out.println("Executed " + operation);
                            out.flush();
                        } else {
                            out.println("Aborted at " + operation);
                            out.flush();

                            break;
                        }
                    }

                }
            }

            server.displayManagementEntities(this.transactionId);

            if (server.getNrClients() > 0) {
                server.setNrClients(server.getNrClients() - 1);
//                int transactionNumber = Utilities.retrieve("..\\2PL\\src\\main\\resources\\current_transaction.txt");
//                Utilities.store(transactionNumber - 1, "..\\2PL\\src\\main\\resources\\current_transaction.txt");
                socket.close();
            }
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                objectInputStream.close();
            } catch (IOException ex) {
                Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}


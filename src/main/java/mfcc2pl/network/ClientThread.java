package mfcc2pl.network;

import mfcc2pl.Utilities;
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
        synchronized (server.transactionId) {
            this.transactionId = server.transactionId;
            transaction = new Transaction(server.transactionId, new Timestamp(System.currentTimeMillis()), "active");
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
                    synchronized (server.locks) {
                        synchronized (server.lockId) {
                            synchronized (server.waitForGraph) {
                                Utilities.releaseAndDistributeLocks(server.locks, server.lockId, server.waitForGraph, this.transactionId);
                            }
                        }
                    }
                    synchronized (server.transactions) {
                        Utilities.setStatus(server.transactions, this.transactionId, "committed");
                    }

                    out.println("Commited");
                    out.flush();

                    break;
                }

                Integer transactionHoldingIncompatibleLock = Utilities.getTransactionHoldingIncompatibleLock(server.locks, this.transactionId, operation);
                if (transactionHoldingIncompatibleLock == null) { // no incompatible lock
                    synchronized (server.locks) { // acquire the lock
                        Utilities.block(server.locks, server.lockId, this.transactionId, operation);
                    }
                    synchronized (server.lockId) {
                        server.lockId++;
                    }

                    operation.execute(conn1, conn2);
                    Utilities.getTransaction(server.transactions, this.transactionId).addOperation(operation);

                    out.println("Executed " + operation);
                    out.flush();
                } else { // the is an incompatible lock
                    if (transactionHoldingIncompatibleLock == this.transactionId) { // if it is my write lock
                        // if select and I own only the write lock, I will acquire also the read lock on that table
                        if (operation.getName().equals("select") && !Utilities.hasLock(server.locks, "read", this.transactionId, operation.getTableName())) {
                            synchronized (server.locks) {
                                Utilities.block(server.locks, server.lockId, this.transactionId, operation);
                            }
                            synchronized (server.lockId) {
                                server.lockId++;
                            }
                        }

                        operation.execute(conn1, conn2);
                        Utilities.getTransaction(server.transactions, this.transactionId).addOperation(operation);

                        out.println("Executed " + operation);
                        out.flush();
                    } else { // someone else has the incompatible lock, so I have to wait
                        synchronized (server.waitForGraph) {
                            Utilities.wait(server.waitForGraph, transactionHoldingIncompatibleLock, this.transactionId, operation);
                        }
                        String lockType = "";
                        if (operation.getName().equals("select")) {
                            lockType = "read";
                        } else {
                            lockType = "write";
                        }

                        out.println("Wait");
                        out.flush();

                        server.displayManagementEntities();

                        while (true) {
                            synchronized (server.locks) {
                                if (!Utilities.isWaiting(server.locks, transactionId, lockType, operation.getTableName())) {
                                    break;
                                }
                            }
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        operation.execute(conn1, conn2);
                        Utilities.getTransaction(server.transactions, this.transactionId).addOperation(operation);

                        server.displayManagementEntities();

                        out.println("Executed " + operation);
                        out.flush();
                    }

                }

                server.displayManagementEntities();
            }

            server.displayManagementEntities();

            if (server.getNrClients() > 0) {
                server.setNrClients(server.getNrClients() - 1);
                int transactionNumber = Utilities.retrieve("..\\2PL\\src\\main\\resources\\current_transaction.txt");
                Utilities.store(transactionNumber - 1, "..\\2PL\\src\\main\\resources\\current_transaction.txt");
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


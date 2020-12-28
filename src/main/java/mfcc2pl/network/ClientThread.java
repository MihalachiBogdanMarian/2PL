package mfcc2pl.network;

import mfcc2pl.Utilities;
import mfcc2pl.sqlutilities.dbconnection.Database;
import mfcc2pl.utilities2pl.operations.Operation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientThread extends Thread {

    private Socket socket = null;
    private final Server server;
    private final Connection conn1;
    private final Connection conn2;

    public ClientThread(Server server, ServerSocket serverSocket, Socket clientSocket) {
        this.server = server;
        this.server.setServerSocket(serverSocket);
        this.socket = clientSocket;
        conn1 = Database.getConnection(1);
        conn2 = Database.getConnection(2);
    }

    @Override
    public void run() {
        ObjectInputStream objectInputStream = null;
        try {
            while (true) {
                objectInputStream = new ObjectInputStream(socket.getInputStream());
                PrintWriter out = new PrintWriter(socket.getOutputStream());

                Operation operation = (Operation) objectInputStream.readObject();

                operation.execute(conn1, conn2);
                out.println("Executed " + operation);
                out.flush();

                if (operation.getName().equals("commit")) {
                    break;
                }
            }
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


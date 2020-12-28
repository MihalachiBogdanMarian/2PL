package mfcc2pl.network;

import mfcc2pl.Utilities;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static final int PORT = 8100;
    private ServerSocket serverSocket;
    private boolean running = false;
    private int nrClients = 0;

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.init();
        server.waitForClients();
    }

    public void init() throws IOException {
        setServerSocket(new ServerSocket(PORT));
        running = true;
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
}

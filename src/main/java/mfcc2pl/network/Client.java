package mfcc2pl.network;

import mfcc2pl.utilities2pl.operations.Operation;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Scanner;

public class Client {

    private final static String SERVER_ADDRESS = "127.0.0.1";
    private final static int PORT = 8100;
    private static boolean restart = true;
    private static boolean firstRun = true;

    public static void main(String[] args) throws IOException {
        Client client = new Client();

        Socket socket = new Socket(SERVER_ADDRESS, PORT);

        // choose the transaction this client will execute
        System.out.print("\nTransaction number: \n");
        int transactionNumber = Integer.parseInt(client.readFromKeyboard());

        List<Operation> transaction = Transactions.getTransactions().get(transactionNumber);


        while (restart) {
            restart = false;

            System.out.println("Press any key to start the transaction - start sending the operations to the server: ");

            if (firstRun) { // trigger the operations only the first time; at restart, take them automatically
                // SEND ALL OPERATIONS INSIDE A TRANSACTION AT ONCE
//                client.readFromKeyboard();
            }

            int i = 0;

            while (i < transaction.size()) {
                // SEND ONE OPERATION INSIDE A TRANSACTION AT A TIME
                System.out.print("\nPress any key to send the next operation: \n");
                client.readFromKeyboard();

                // send operation to the Server and wait for the response
                String response = client.sendOperationToServer(transaction.get(i), socket);
                System.out.println(response);

                if (response.equals("Wait")) {
                    System.out.println("Blocked waiting for a lock! I have to wait now!");
                    System.out.println();

                    // wait for the next message from the Server
                    String unblockOrAbortResponse = client.waitResponseFromServer(socket);
                    System.out.println(unblockOrAbortResponse);

                    if (unblockOrAbortResponse.startsWith("Aborted")) {
                        System.out.println("Trying to send the operations again!");
                        restart = true; // automatically restart the transaction
                        firstRun = false;
                        break;
                    } else { // received the lock, can continue to send operations
                        System.out.println("I can send operations again!");
                    }
                }

                // next operation
                i++;
            }
        }
    }

    public String sendOperationToServer(Operation operation, Socket socket) throws IOException {
        try {
            OutputStream outputStream = socket.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // write the operation object to the Server
            objectOutputStream.writeObject(operation);

            // wait for the response from the Server
            String response = in.readLine();
            return response.replaceAll("\\\\&n", "\n").replaceAll("\\\\&t", "\t");
        } catch (UnknownHostException e) {
            System.err.println("No server listening... " + e);
            return null;
        }
    }

    public String waitResponseFromServer(Socket socket) throws IOException {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String response = in.readLine();
            return response.replaceAll("\\\\&n", "\n").replaceAll("\\\\&t", "\t");
        } catch (UnknownHostException e) {
            System.err.println("No server listening... " + e);
            return null;
        }
    }

    private String readFromKeyboard() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }
}
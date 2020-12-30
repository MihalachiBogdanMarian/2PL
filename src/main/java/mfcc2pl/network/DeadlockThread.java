package mfcc2pl.network;

import mfcc2pl.sqlutilities.dbconnection.Database;
import mfcc2pl.utilities2pl.operations.Operation;
import oracle.ucp.util.Pair;

import java.sql.Connection;

public class DeadlockThread extends Thread {

    private final Server server;
    private final Connection conn1;
    private final Connection conn2;

    public DeadlockThread(Server server) {
        this.server = server;
        conn1 = Database.getConnection(1);
        conn2 = Database.getConnection(2);
    }

    @Override
    public void run() {
        while (true) {
            Pair<Integer, Integer> transactionInDeadlockId = server.hasSimpleDeadlock();
            if (transactionInDeadlockId != null) {

                System.out.println();
                System.out.println("Deadlock: aborting transaction " + transactionInDeadlockId.get1st());
                for (Operation operation : server.getTransaction(transactionInDeadlockId.get1st()).getOperations()) {
                    System.out.println("\t" + operation);
                }
                System.out.println();

                server.resolveDeadlock(conn1, conn2, transactionInDeadlockId);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}


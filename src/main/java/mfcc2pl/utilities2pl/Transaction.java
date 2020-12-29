package mfcc2pl.utilities2pl;

import mfcc2pl.utilities2pl.operations.Operation;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Transaction {

    private int id;
    private Timestamp ts;
    private String status;
    List<Operation> operations;

    public Transaction(int id, Timestamp ts, String status) {
        this.id = id;
        this.ts = ts;
        this.status = status;
        operations = new ArrayList<>();
    }

    public void rollback(Connection conn1, Connection conn2, int fromOperationIndex) {
        for (int i = fromOperationIndex; i >= 0; i--) {
            if (operations.get(i).getName().equals("select") || operations.get(i).getName().equals("commit")) {
            } else if (operations.get(i).getName().equals("insert") || operations.get(i).getName().equals("update")) {
                operations.get(i).compensationOperation().execute(conn1, conn2);
            } else if (operations.get(i).getName().equals("delete")) {
                for (Operation operation : operations.get(i).compensationOperations()) {
                    operation.execute(conn1, conn2);
                }
            }
        }
    }

    public void addOperation(Operation operation) {
        operations.add(operation);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getTs() {
        return ts;
    }

    public void setTs(Timestamp ts) {
        this.ts = ts;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", ts=" + ts +
                ", status='" + status + '\'' +
                ", operations=" + operations +
                '}';
    }
}

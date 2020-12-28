package mfcc2pl.utilities2pl;

import mfcc2pl.utilities2pl.operations.Operation;

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
}

package mfcc2pl.utilities2pl;

public class Lock {

    private int id;
    private String type;
    private String table;
    private String transaction;

    public Lock(int id, String type, String table, String transactionId) {
        this.id = id;
        this.type = type;
        this.table = table;
        this.transaction = transactionId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }
}

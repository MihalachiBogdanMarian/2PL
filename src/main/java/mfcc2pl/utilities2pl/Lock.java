package mfcc2pl.utilities2pl;

public class Lock {

    private int id;
    private String type;
    private String table;
    private int transactionId;

    public Lock(int id, String type, String table, int transactionId) {
        this.id = id;
        this.type = type;
        this.table = table;
        this.transactionId = transactionId;
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

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public String toString() {
        return "Lock{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", table='" + table + '\'' +
                ", transactionId=" + transactionId  +
                '}';
    }
}

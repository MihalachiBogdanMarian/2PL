package mfcc2pl.utilities2pl;

public class WaitForGraphNode {

    private String lockType;
    private String table;
    private int transactionIdHasLock;
    private int transactionIdWaitsLock;

    public WaitForGraphNode(String lockType, String table, int transactionIdHasLock, int transactionIdWaitsLock) {
        this.lockType = lockType;
        this.table = table;
        this.transactionIdHasLock = transactionIdHasLock;
        this.transactionIdWaitsLock = transactionIdWaitsLock;
    }

    public String getLockType() {
        return lockType;
    }

    public void setLockType(String lockType) {
        this.lockType = lockType;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public int getTransactionIdHasLock() {
        return transactionIdHasLock;
    }

    public void setTransactionIdHasLock(int transactionIdHasLock) {
        this.transactionIdHasLock = transactionIdHasLock;
    }

    public int getTransactionIdWaitsLock() {
        return transactionIdWaitsLock;
    }

    public void setTransactionIdWaitsLock(int transactionIdWaitsLock) {
        this.transactionIdWaitsLock = transactionIdWaitsLock;
    }

    @Override
    public String toString() {
        return "WaitForGraphNode{" +
                "lockType='" + lockType + '\'' +
                ", table='" + table + '\'' +
                ", transactionIdHasLock=" + transactionIdHasLock +
                ", transactionIdWaitsLock=" + transactionIdWaitsLock +
                '}';
    }
}

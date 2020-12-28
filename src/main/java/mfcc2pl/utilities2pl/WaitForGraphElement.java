package mfcc2pl.utilities2pl;

public class WaitForGraphElement {

    private String lockType;
    private String table;
    private int transactionHasLock;
    private int transactionWaitsLock;

    public WaitForGraphElement(String lockType, String table, int transactionHasLock, int transactionWaitsLock) {
        this.lockType = lockType;
        this.table = table;
        this.transactionHasLock = transactionHasLock;
        this.transactionWaitsLock = transactionWaitsLock;
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

    public int getTransactionHasLock() {
        return transactionHasLock;
    }

    public void setTransactionHasLock(int transactionHasLock) {
        this.transactionHasLock = transactionHasLock;
    }

    public int getTransactionWaitsLock() {
        return transactionWaitsLock;
    }

    public void setTransactionWaitsLock(int transactionWaitsLock) {
        this.transactionWaitsLock = transactionWaitsLock;
    }
}

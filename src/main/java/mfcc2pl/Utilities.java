package mfcc2pl;

import mfcc2pl.sqlutilities.model.SearchCondition;
import mfcc2pl.utilities2pl.Lock;
import mfcc2pl.utilities2pl.Transaction;
import mfcc2pl.utilities2pl.WaitForGraphNode;
import mfcc2pl.utilities2pl.operations.Operation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Utilities {

    public static String formSelectStatement(List<String> fields, String tableName, List<SearchCondition> searchConditions) {
        String selectStatement = "select ";
        if (fields.size() == 1 && fields.get(0).equals("*")) {
            selectStatement += fields.get(0);
        } else {
            for (int i = 0; i < fields.size(); i++) {
                if (i == fields.size() - 1) {
                    selectStatement += fields.get(i);
                } else {
                    selectStatement += fields.get(i) + ", ";
                }
            }
        }
        selectStatement += " from " + tableName;
        if (searchConditions.isEmpty()) {
        } else {
            selectStatement += " where ";
            for (int i = 0; i < searchConditions.size(); i++) {
                if (i == searchConditions.size() - 1) {
                    selectStatement += searchConditions.get(i).getFieldName() + " " + searchConditions.get(i).getOperator() + " ?";
                } else {
                    selectStatement += searchConditions.get(i).getFieldName() + " " + searchConditions.get(i).getOperator() + " ? and ";
                }
            }
        }
        return selectStatement;
    }

    public static String formUpdateStatement(String tableName, String fieldName, List<SearchCondition> searchConditions) {
        String updateStatement = "update " + tableName + " set " + fieldName + " = ?";
        if (searchConditions.isEmpty()) {
        } else {
            updateStatement += " where ";
            for (int i = 0; i < searchConditions.size(); i++) {
                if (i == searchConditions.size() - 1) {
                    updateStatement += searchConditions.get(i).getFieldName() + " " + searchConditions.get(i).getOperator() + " ?";
                } else {
                    updateStatement += searchConditions.get(i).getFieldName() + " " + searchConditions.get(i).getOperator() + " ? and ";
                }
            }
        }
        return updateStatement;
    }

    public static String formUpdateStatementIncrement(String tableName, String fieldName, List<SearchCondition> searchConditions) {
        String updateStatement = "update " + tableName + " set " + fieldName + " = " + fieldName + " + 1";
        if (searchConditions.isEmpty()) {
        } else {
            updateStatement += " where ";
            for (int i = 0; i < searchConditions.size(); i++) {
                if (i == searchConditions.size() - 1) {
                    updateStatement += searchConditions.get(i).getFieldName() + " " + searchConditions.get(i).getOperator() + " ?";
                } else {
                    updateStatement += searchConditions.get(i).getFieldName() + " " + searchConditions.get(i).getOperator() + " ? and ";
                }
            }
        }
        return updateStatement;
    }

    public static String formUpdateStatementDecrement(String tableName, String fieldName, List<SearchCondition> searchConditions) {
        String updateStatement = "update " + tableName + " set " + fieldName + " = " + fieldName + " - 1";
        if (searchConditions.isEmpty()) {
        } else {
            updateStatement += " where ";
            for (int i = 0; i < searchConditions.size(); i++) {
                if (i == searchConditions.size() - 1) {
                    updateStatement += searchConditions.get(i).getFieldName() + " " + searchConditions.get(i).getOperator() + " ?";
                } else {
                    updateStatement += searchConditions.get(i).getFieldName() + " " + searchConditions.get(i).getOperator() + " ? and ";
                }
            }
        }
        return updateStatement;
    }

    public static String formDeleteStatement(String tableName, List<SearchCondition> searchConditions) {
        String updateStatement = "delete from " + tableName;
        if (searchConditions.isEmpty()) {
        } else {
            updateStatement += " where ";
            for (int i = 0; i < searchConditions.size(); i++) {
                if (i == searchConditions.size() - 1) {
                    updateStatement += searchConditions.get(i).getFieldName() + " " + searchConditions.get(i).getOperator() + " ?";
                } else {
                    updateStatement += searchConditions.get(i).getFieldName() + " " + searchConditions.get(i).getOperator() + " ? and ";
                }
            }
        }
        return updateStatement;
    }

    public static void store(int value, String filename) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(filename);
            fileWriter.write(String.valueOf(value));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileWriter != null) {
                    fileWriter.flush();
                    fileWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static int retrieve(String filename) {
        int value = -1;
        try {
            FileReader reader = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(reader);

            value = Integer.parseInt(bufferedReader.readLine());

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static Transaction getTransaction(List<Transaction> transactions, Integer id) {
        return transactions.stream()
                .filter(transaction -> transaction.getId() == id)
                .findAny()
                .orElse(null);
    }

    public static Integer getTransactionHoldingIncompatibleLock(List<Lock> locks, Integer transactionId, Operation operation) {
        if (operation.getName().equals("select")) {
            Lock lockToFind = locks.stream()
                    .filter(lock -> lock.getType().equals("write")
                            && lock.getTable().equals(operation.getTableName())
                            && lock.getTransactionId() != transactionId)
                    .findAny()
                    .orElse(null);
            if (lockToFind == null) {
                return null;
            } else {
                return lockToFind.getTransactionId();
            }
        } else {
            Lock lockToFind = locks.stream()
                    .filter(lock -> lock.getTable().equals(operation.getTableName())
                            && lock.getTransactionId() != transactionId)
                    .findAny()
                    .orElse(null);
            if (lockToFind == null) {
                return null;
            } else {
                return lockToFind.getTransactionId();
            }
        }
    }

    public static void block(List<Lock> locks, Integer lockId, Integer transactionId, Operation operation) {
        String lockType = "";
        if (operation.getName().equals("select")) {
            lockType = "read";
        } else {
            lockType = "write";
        }
        locks.add(new Lock(lockId, lockType, operation.getTableName(), transactionId));
    }

    public static void wait(List<WaitForGraphNode> waitForGraphNodes, Integer transactionIdHasLock, Integer transactionIdToWait, Operation operation) {
        String lockType = "";
        if (operation.getName().equals("select")) {
            lockType = "read";
        } else {
            lockType = "write";
        }
        waitForGraphNodes.add(new WaitForGraphNode(lockType, operation.getTableName(), transactionIdHasLock, transactionIdToWait));
    }
}

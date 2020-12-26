package mfcc2pl;

import mfcc2pl.utilities2pl.operations.SearchCondition;

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
}

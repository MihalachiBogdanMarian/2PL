package mfcc2pl.sqlutilities.controllers;

import mfcc2pl.sqlutilities.dbconnection.Database;
import mfcc2pl.sqlutilities.model.SearchCondition;

import java.sql.*;
import java.util.List;

public class ControllerUtilities {

    public static void preparedSelectOrDeleteStatementSetParameters(PreparedStatement pstmt, List<SearchCondition> searchConditions) {
        try {
            for (int i = 0; i < searchConditions.size(); i++) {
                if (searchConditions.get(i).getValue() instanceof Integer) {
                    pstmt.setInt(i + 1, (Integer) searchConditions.get(i).getValue());
                } else if (searchConditions.get(i).getValue() instanceof Date) {
                    pstmt.setDate(i + 1, (Date) searchConditions.get(i).getValue());
                } else if (searchConditions.get(i).getValue() instanceof String) {
                    pstmt.setString(i + 1, searchConditions.get(i).getValue().toString());
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void preparedUpdateStatementSetParameters(PreparedStatement pstmt, String updateType, Object fieldValue, List<SearchCondition> searchConditions) {
        try {
            if (fieldValue instanceof Integer) {
                pstmt.setInt(1, (Integer) fieldValue);
            } else if (fieldValue instanceof Date) {
                pstmt.setDate(1, (Date) fieldValue);
            } else if (fieldValue instanceof String) {
                pstmt.setString(1, fieldValue.toString());
            }

            for (int i = 0; i < searchConditions.size(); i++) {
                if (searchConditions.get(i).getValue() instanceof Integer) {
                    if (updateType.equals("i") || updateType.equals("d")) {
                        pstmt.setInt(i + 1, (Integer) searchConditions.get(i).getValue());
                    } else {
                        pstmt.setInt(i + 2, (Integer) searchConditions.get(i).getValue());
                    }
                } else if (searchConditions.get(i).getValue() instanceof Date) {
                    if (updateType.equals("i") || updateType.equals("d")) {
                        pstmt.setDate(i + 1, (Date) searchConditions.get(i).getValue());
                    } else {
                        pstmt.setDate(i + 2, (Date) searchConditions.get(i).getValue());
                    }
                } else if (searchConditions.get(i).getValue() instanceof String) {
                    if (updateType.equals("i") || updateType.equals("d")) {
                        pstmt.setString(i + 1, searchConditions.get(i).getValue().toString());
                    } else {
                        pstmt.setString(i + 2, searchConditions.get(i).getValue().toString());
                    }
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void refreshMaterializedView(String viewName) {
        try {
            final Connection viewConn = Database.getConnection(viewName);
            String userIdsViewRefresh = "begin dbms_snapshot.refresh('" + viewName + "', 'f'); end;";

            CallableStatement cs = null;
            cs = viewConn.prepareCall(userIdsViewRefresh);
            cs.execute();

            cs.close();
            viewConn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}

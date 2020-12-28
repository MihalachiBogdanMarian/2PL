package mfcc2pl.sqlutilities.controllers;

import mfcc2pl.Utilities;
import mfcc2pl.sqlutilities.model.SearchCondition;
import mfcc2pl.sqlutilities.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserController {

    public Connection conn;

    public UserController(Connection conn) {
        this.conn = conn;
    }

    public void insertUser(User user) {
        String insertStatement = "insert into users " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement pstmt = conn.prepareStatement(insertStatement);
            pstmt.setInt(1, user.getId());
            pstmt.setString(2, user.getFirstName());
            pstmt.setString(3, user.getLastName());
            pstmt.setDate(4, user.getBirthday());
            pstmt.setString(5, user.getAddress());
            pstmt.setString(6, user.getPhoneNumber());
            pstmt.setString(7, user.getEmail());
            pstmt.setString(8, user.getPassword());
            pstmt.setString(9, user.getType());
            pstmt.setInt(10, user.getLogged());
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<Map<String, Object>> selectUsers(List<String> fields, List<SearchCondition> searchConditions) {
        List<Map<String, Object>> users = new ArrayList<>();

        String selectStatement = Utilities.formSelectStatement(fields, "users", searchConditions);

        try {
            PreparedStatement pstmt = conn.prepareStatement(selectStatement);

            for (int i = 0; i < searchConditions.size(); i++) {
                if (searchConditions.get(i).getValue() instanceof Integer) {
                    pstmt.setInt(i + 1, (Integer) searchConditions.get(i).getValue());
                } else if (searchConditions.get(i).getValue() instanceof Date) {
                    pstmt.setDate(i + 1, (Date) searchConditions.get(i).getValue());
                } else if (searchConditions.get(i).getValue() instanceof String) {
                    pstmt.setString(i + 1, searchConditions.get(i).getValue().toString());
                }
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                if (fields.size() == 1 && fields.get(0).equals("*")) {
                    Map<String, Object> user = new HashMap<>();
                    user.put("id", rs.getInt("id"));
                    user.put("first_name", rs.getString("first_name"));
                    user.put("last_name", rs.getString("last_name"));
                    user.put("birthday", rs.getDate("birthday"));
                    user.put("address", rs.getString("address"));
                    user.put("phone_number", rs.getString("phone_number"));
                    user.put("email", rs.getString("email"));
                    user.put("password", rs.getString("password"));
                    user.put("type", rs.getString("type"));
                    user.put("logged", rs.getInt("logged"));
                    users.add(user);
                } else {
                    Map<String, Object> user = new HashMap<>();
                    for (String field : fields) {
                        if (field.equals("id") || field.equals("logged")) {
                            user.put(field, rs.getInt(field));
                        } else if (field.equals("birthday")) {
                            user.put(field, rs.getDate(field));
                        } else {
                            user.put(field, rs.getString(field));
                        }
                    }
                    users.add(user);
                }
            }
            pstmt.execute();
            pstmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return users;
    }

    public void updateUsers(String updateType, String fieldName, Object fieldValue, List<SearchCondition> searchConditions) {

        String updateStatement;
        if (updateType.equals("u")) {
            updateStatement = Utilities.formUpdateStatement("users", fieldName, searchConditions);
        } else if (updateType.equals("i")) {
            updateStatement = Utilities.formUpdateStatementIncrement("users", fieldName, searchConditions);
        } else if (updateType.equals("d")) {
            updateStatement = Utilities.formUpdateStatementDecrement("users", fieldName, searchConditions);
        } else {
            updateStatement = Utilities.formUpdateStatement("users", fieldName, searchConditions);
        }

        try {
            PreparedStatement pstmt = conn.prepareStatement(updateStatement);
            if (fieldValue instanceof Integer) {
                pstmt.setInt(1, (Integer) fieldValue);
            } else if (fieldValue instanceof Date) {
                pstmt.setDate(1, (Date) fieldValue);
            } else if (fieldValue instanceof String) {
                pstmt.setString(1, fieldValue.toString());
            }

            for (int i = 0; i < searchConditions.size(); i++) {
                if (searchConditions.get(i).getValue() instanceof Integer) {
                    pstmt.setInt(i + 2, (Integer) searchConditions.get(i).getValue());
                } else if (searchConditions.get(i).getValue() instanceof Date) {
                    pstmt.setDate(i + 2, (Date) searchConditions.get(i).getValue());
                } else if (searchConditions.get(i).getValue() instanceof String) {
                    pstmt.setString(i + 2, searchConditions.get(i).getValue().toString());
                }
            }

            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void deleteUsers(List<SearchCondition> searchConditions) {

        String deleteStatement = Utilities.formDeleteStatement("users", searchConditions);

        try {
            PreparedStatement pstmt = conn.prepareStatement(deleteStatement);

            for (int i = 0; i < searchConditions.size(); i++) {
                if (searchConditions.get(i).getValue() instanceof Integer) {
                    pstmt.setInt(i + 1, (Integer) searchConditions.get(i).getValue());
                } else if (searchConditions.get(i).getValue() instanceof Date) {
                    pstmt.setDate(i + 1, (Date) searchConditions.get(i).getValue());
                } else if (searchConditions.get(i).getValue() instanceof String) {
                    pstmt.setString(i + 1, searchConditions.get(i).getValue().toString());
                }
            }

            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

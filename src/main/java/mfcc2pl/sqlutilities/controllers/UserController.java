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

    public List<Map<String, Object>> selectUsers(List<String> fields, List<SearchCondition> searchConditions) {
        List<Map<String, Object>> users = new ArrayList<>();

        String selectStatement = Utilities.formSelectStatement(fields, "users", searchConditions);

        try {
            PreparedStatement pstmt = conn.prepareStatement(selectStatement);

            ControllerUtilities.preparedSelectOrDeleteStatementSetParameters(pstmt, searchConditions);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> user = new HashMap<>();
                if (fields.size() == 1 && fields.get(0).equals("*")) {
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
                } else {
                    for (String field : fields) {
                        if (field.equals("id") || field.equals("logged")) {
                            user.put(field, rs.getInt(field));
                        } else if (field.equals("birthday")) {
                            user.put(field, rs.getDate(field));
                        } else {
                            user.put(field, rs.getString(field));
                        }
                    }
                }
                users.add(user);
            }
            pstmt.execute();
            pstmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return users;
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

            // make a refresh of the materialized view referred in the other database
            ControllerUtilities.refreshMaterializedView("user_ids");
        } catch (SQLException ex) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateUsers(String updateType, String fieldName, Object fieldValue, List<SearchCondition> searchConditions) {
        String updateStatement;
        switch (updateType) {
            case "i":
                updateStatement = Utilities.formUpdateStatementIncrement("users", fieldName, searchConditions);
                break;
            case "d":
                updateStatement = Utilities.formUpdateStatementDecrement("users", fieldName, searchConditions);
                break;
            default:
                updateStatement = Utilities.formUpdateStatement("users", fieldName, searchConditions);
                break;
        }

        try {
            PreparedStatement pstmt = conn.prepareStatement(updateStatement);

            ControllerUtilities.preparedUpdateStatementSetParameters(pstmt, updateType, fieldValue, searchConditions);

            pstmt.executeUpdate();
            pstmt.close();

            if (fieldName.equals("id")) {
                // make a refresh of the materialized view referred in the other database
                ControllerUtilities.refreshMaterializedView("user_ids");
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void deleteUsers(List<SearchCondition> searchConditions) {
        String deleteStatement = Utilities.formDeleteStatement("users", searchConditions);

        try {
            PreparedStatement pstmt = conn.prepareStatement(deleteStatement);

            ControllerUtilities.preparedSelectOrDeleteStatementSetParameters(pstmt, searchConditions);

            pstmt.executeUpdate();
            pstmt.close();

            // make a refresh of the materialized view referred in the other database
            ControllerUtilities.refreshMaterializedView("user_ids");
        } catch (SQLException ex) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

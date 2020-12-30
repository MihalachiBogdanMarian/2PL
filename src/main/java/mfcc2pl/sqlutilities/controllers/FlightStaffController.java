package mfcc2pl.sqlutilities.controllers;

import mfcc2pl.Utilities;
import mfcc2pl.sqlutilities.model.FlightStaff;
import mfcc2pl.sqlutilities.model.SearchCondition;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FlightStaffController {

    public Connection conn;

    public FlightStaffController(Connection conn) {
        this.conn = conn;
    }

    public List<Map<String, Object>> selectFlightStaff(List<String> fields, List<SearchCondition> searchConditions) {
        List<Map<String, Object>> flightStaff = new ArrayList<>();

        String selectStatement = Utilities.formSelectStatement(fields, "flights_staff", searchConditions);

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
                    Map<String, Object> flightWorker = new HashMap<>();
                    flightWorker.put("flight_id", rs.getInt("flight_id"));
                    flightWorker.put("user_id", rs.getInt("user_id"));
                    flightWorker.put("user_type", rs.getString("user_type"));
                    flightStaff.add(flightWorker);
                } else {
                    Map<String, Object> flightWorker = new HashMap<>();
                    for (String field : fields) {
                        if (field.equals("user_type")) {
                            flightWorker.put(field, rs.getString(field));
                        } else {
                            flightWorker.put(field, rs.getInt(field));
                        }
                    }
                    flightStaff.add(flightWorker);
                }
            }
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(FlightStaffController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return flightStaff;
    }

    public void insertFlightStaff(FlightStaff flightStaff) {
        try {
            String insertStatement = "insert into flights_staff(flight_id, user_id, user_type) " +
                    "values (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertStatement);
            pstmt.setInt(1, flightStaff.getFlightId());
            pstmt.setInt(2, flightStaff.getUserId());
            pstmt.setString(3, flightStaff.getUserType());
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(FlightStaffController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void deleteFlightStaff(List<SearchCondition> searchConditions) {
        String deleteStatement = Utilities.formDeleteStatement("flights_staff", searchConditions);

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
            Logger.getLogger(FlightStaffController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

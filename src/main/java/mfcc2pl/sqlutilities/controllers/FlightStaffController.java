package mfcc2pl.sqlutilities.controllers;

import mfcc2pl.sqlutilities.model.FlightStaff;
import mfcc2pl.sqlutilities.model.SearchCondition;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

        String selectStatement = ControllerUtilities.formSelectStatement(fields, "flights_staff", searchConditions);

        try {
            PreparedStatement pstmt = conn.prepareStatement(selectStatement);

            ControllerUtilities.preparedSelectOrDeleteStatementSetParameters(pstmt, searchConditions);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> flightWorker = new HashMap<>();
                if (fields.size() == 1 && fields.get(0).equals("*")) {
                    flightWorker.put("flight_id", rs.getInt("flight_id"));
                    flightWorker.put("user_id", rs.getInt("user_id"));
                    flightWorker.put("user_type", rs.getString("user_type"));
                } else {
                    for (String field : fields) {
                        if (field.equals("user_type")) {
                            flightWorker.put(field, rs.getString(field));
                        } else {
                            flightWorker.put(field, rs.getInt(field));
                        }
                    }
                }
                flightStaff.add(flightWorker);
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
        String deleteStatement = ControllerUtilities.formDeleteStatement("flights_staff", searchConditions);

        try {
            PreparedStatement pstmt = conn.prepareStatement(deleteStatement);

            ControllerUtilities.preparedSelectOrDeleteStatementSetParameters(pstmt, searchConditions);

            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(FlightStaffController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

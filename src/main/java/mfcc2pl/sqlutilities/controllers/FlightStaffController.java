package mfcc2pl.sqlutilities.controllers;

import mfcc2pl.Utilities;
import mfcc2pl.sqlutilities.model.FlightStaff;
import mfcc2pl.sqlutilities.model.SearchCondition;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FlightStaffController {

    public Connection conn;

    public FlightStaffController(Connection conn) {
        this.conn = conn;
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

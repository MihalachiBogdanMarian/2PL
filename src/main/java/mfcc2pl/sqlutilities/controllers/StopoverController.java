package mfcc2pl.sqlutilities.controllers;

import mfcc2pl.Utilities;
import mfcc2pl.sqlutilities.model.SearchCondition;
import mfcc2pl.sqlutilities.model.Stopover;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StopoverController {

    public Connection conn;

    public StopoverController(Connection conn) {
        this.conn = conn;
    }

    public List<Map<String, Object>> selectStopovers(List<String> fields, List<SearchCondition> searchConditions) {
        List<Map<String, Object>> stopovers = new ArrayList<>();

        String selectStatement = Utilities.formSelectStatement(fields, "stopovers", searchConditions);

        try {
            PreparedStatement pstmt = conn.prepareStatement(selectStatement);

            ControllerUtilities.preparedSelectOrDeleteStatementSetParameters(pstmt, searchConditions);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> stopover = new HashMap<>();
                if (fields.size() == 1 && fields.get(0).equals("*")) {
                    stopover.put("stop_number", rs.getInt("stop_number"));
                    stopover.put("flight_id", rs.getInt("flight_id"));
                    stopover.put("airport_name", rs.getString("airport_name"));
                    stopover.put("time", rs.getInt("time"));
                    stopover.put("price_first_class", rs.getInt("price_first_class"));
                    stopover.put("price_second_class", rs.getInt("price_second_class"));
                    stopover.put("departure_date", rs.getDate("departure_date"));
                } else {
                    FlightController.addSomeFlightAttributesForSelect(rs, stopover, fields);
                }
                stopovers.add(stopover);
            }
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(StopoverController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return stopovers;
    }

    public void insertStopover(Stopover stopover) {
        try {
            String insertStatement = "insert into stopovers(stop_number, flight_id, airport_name, time, price_first_class, price_second_class, departure_date) " +
                    "values (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertStatement);
            pstmt.setInt(1, stopover.getStopNumber());
            pstmt.setInt(2, stopover.getFlightId());
            pstmt.setString(3, stopover.getAirportName());
            pstmt.setInt(4, stopover.getTime());
            pstmt.setInt(5, stopover.getPriceFirstClass());
            pstmt.setInt(6, stopover.getPriceSecondClass());
            pstmt.setDate(7, stopover.getDepartureDate());
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(StopoverController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void deleteStopovers(List<SearchCondition> searchConditions) {
        String deleteStatement = Utilities.formDeleteStatement("stopovers", searchConditions);

        try {
            PreparedStatement pstmt = conn.prepareStatement(deleteStatement);

            ControllerUtilities.preparedSelectOrDeleteStatementSetParameters(pstmt, searchConditions);

            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(StopoverController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

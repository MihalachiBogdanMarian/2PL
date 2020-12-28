package mfcc2pl.sqlutilities.controllers;

import mfcc2pl.sqlutilities.model.FlightInCache;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FlightInCacheController {

    public Connection conn;

    public FlightInCacheController(Connection conn) {
        this.conn = conn;
    }

    public void insertFlightInCache(FlightInCache flightInCache) {
        try {
            String insertStatement = "insert into flights_cache(flight_id, departure_date, duration, delay, distance, stopovers, airport_name, airplane_id, first_class_seats, second_class_seats, first_class_price, second_class_price, reason, rescheduled) " +
                    "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertStatement);
            pstmt.setInt(1, flightInCache.getId());
            pstmt.setDate(2, flightInCache.getDepartureDate());
            pstmt.setInt(3, flightInCache.getDuration());
            pstmt.setInt(4, flightInCache.getDelay());
            pstmt.setInt(5, flightInCache.getDistance());
            pstmt.setInt(6, flightInCache.getStopovers());
            pstmt.setString(7, flightInCache.getAirportName());
            pstmt.setInt(8, flightInCache.getAirplaneId());
            pstmt.setInt(9, flightInCache.getFirstClassSeats());
            pstmt.setInt(10, flightInCache.getSecondClassSeats());
            pstmt.setInt(11, flightInCache.getFirstClassPrice());
            pstmt.setInt(12, flightInCache.getSecondClassPrice());
            pstmt.setString(13, flightInCache.getReason());
            if (flightInCache.getRescheduled() == null) {
                pstmt.setNull(14, Types.NULL);
            } else {
                pstmt.setDate(14, flightInCache.getRescheduled());
            }
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(FlightInCacheController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

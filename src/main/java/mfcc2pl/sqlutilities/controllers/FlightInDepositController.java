package mfcc2pl.sqlutilities.controllers;

import mfcc2pl.sqlutilities.model.FlightInDeposit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FlightInDepositController {

    public Connection conn;

    public FlightInDepositController(Connection conn) {
        this.conn = conn;
    }

    public void insertFlightInCache(FlightInDeposit flightInDeposit) {
        try {
            String insertStatement = "insert into flights_cache(flight_id, departure_date, duration, delay, distance, stopovers, airport_name, airplane_id, first_class_seats, second_class_seats, first_class_price, second_class_price, reason, rescheduled) " +
                    "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertStatement);
            pstmt.setInt(1, flightInDeposit.getId());
            pstmt.setDate(2, flightInDeposit.getDepartureDate());
            pstmt.setInt(3, flightInDeposit.getDuration());
            pstmt.setInt(4, flightInDeposit.getDelay());
            pstmt.setInt(5, flightInDeposit.getDistance());
            pstmt.setInt(6, flightInDeposit.getStopovers());
            pstmt.setString(7, flightInDeposit.getAirportName());
            pstmt.setInt(8, flightInDeposit.getAirplaneId());
            pstmt.setInt(9, flightInDeposit.getFirstClassSeats());
            pstmt.setInt(10, flightInDeposit.getSecondClassSeats());
            pstmt.setInt(11, flightInDeposit.getFirstClassPrice());
            pstmt.setInt(12, flightInDeposit.getSecondClassPrice());
            pstmt.setString(13, flightInDeposit.getReason());
            if (flightInDeposit.getRescheduled() == null) {
                pstmt.setNull(14, Types.NULL);
            } else {
                pstmt.setDate(14, flightInDeposit.getRescheduled());
            }
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(FlightInDepositController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

package mfcc2pl.sqlutilities.controllers;

import mfcc2pl.Utilities;
import mfcc2pl.sqlutilities.model.FlightInDeposit;
import mfcc2pl.sqlutilities.model.SearchCondition;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FlightInDepositController {

    public Connection conn;

    public FlightInDepositController(Connection conn) {
        this.conn = conn;
    }

    public List<Map<String, Object>> selectFlightsInDeposit(List<String> fields, List<SearchCondition> searchConditions) {
        List<Map<String, Object>> flightsInDeposit = new ArrayList<>();

        String selectStatement = Utilities.formSelectStatement(fields, "flights_deposit", searchConditions);

        try {
            PreparedStatement pstmt = conn.prepareStatement(selectStatement);

            ControllerUtilities.preparedSelectOrDeleteStatementSetParameters(pstmt, searchConditions);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> flightInDeposit = new HashMap<>();
                if (fields.size() == 1 && fields.get(0).equals("*")) {
                    FlightController.addAllFlightAttributesForSelect(rs, flightInDeposit);
                    flightInDeposit.put("reason", rs.getString("reason"));
                    flightInDeposit.put("rescheduled", rs.getDate("rescheduled"));
                } else {
                    for (String field : fields) {
                        if (field.equals("departure_date") || field.equals("rescheduled")) {
                            flightInDeposit.put(field, rs.getDate(field));
                        } else if (field.equals("airport_name") || field.equals("reason")) {
                            flightInDeposit.put(field, rs.getString(field));
                        } else {
                            flightInDeposit.put(field, rs.getInt(field));
                        }
                    }
                }
                flightsInDeposit.add(flightInDeposit);
            }
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(FlightInDepositController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return flightsInDeposit;
    }

    public void insertFlightInDeposit(FlightInDeposit flightInDeposit) {
        try {
            String insertStatement = "insert into flights_deposit(flight_id, departure_date, duration, delay, distance, stopovers, airport_name, airplane_id, first_class_seats, second_class_seats, first_class_price, second_class_price, reason, rescheduled) " +
                    "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertStatement);

            FlightController.setAllFlightParametersForInsert(pstmt, flightInDeposit.getId(), flightInDeposit.getDepartureDate(), flightInDeposit.getDuration(), flightInDeposit.getDelay(), flightInDeposit.getDistance(), flightInDeposit.getStopovers(), flightInDeposit.getAirportName(), flightInDeposit.getAirplaneId(), flightInDeposit.getFirstClassSeats(), flightInDeposit.getSecondClassSeats(), flightInDeposit.getFirstClassPrice(), flightInDeposit.getSecondClassPrice());

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

    public void deleteFlightInDeposit(List<SearchCondition> searchConditions) {
        String deleteStatement = Utilities.formDeleteStatement("flights_deposit", searchConditions);

        try {
            PreparedStatement pstmt = conn.prepareStatement(deleteStatement);

            ControllerUtilities.preparedSelectOrDeleteStatementSetParameters(pstmt, searchConditions);

            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(FlightInDepositController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

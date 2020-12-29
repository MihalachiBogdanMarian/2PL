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

    public void insertFlightInDeposit(FlightInDeposit flightInDeposit) {
        try {
            String insertStatement = "insert into flights_deposit(flight_id, departure_date, duration, delay, distance, stopovers, airport_name, airplane_id, first_class_seats, second_class_seats, first_class_price, second_class_price, reason, rescheduled) " +
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

    public List<Map<String, Object>> selectFlightsInDeposit(List<String> fields, List<SearchCondition> searchConditions) {
        List<Map<String, Object>> flightsInDeposit = new ArrayList<>();

        String selectStatement = Utilities.formSelectStatement(fields, "flights_deposit", searchConditions);

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
                    Map<String, Object> flightInDeposit = new HashMap<>();
                    flightInDeposit.put("id", rs.getInt("id"));
                    flightInDeposit.put("departure_date", rs.getDate("departure_date"));
                    flightInDeposit.put("duration", rs.getInt("duration"));
                    flightInDeposit.put("delay", rs.getInt("delay"));
                    flightInDeposit.put("distance", rs.getInt("distance"));
                    flightInDeposit.put("stopovers", rs.getInt("stopovers"));
                    flightInDeposit.put("airport_name", rs.getString("airport_name"));
                    flightInDeposit.put("airplane_id", rs.getInt("airplane_id"));
                    flightInDeposit.put("first_class_seats", rs.getInt("first_class_seats"));
                    flightInDeposit.put("second_class_seats", rs.getInt("second_class_seats"));
                    flightInDeposit.put("first_class_price", rs.getInt("first_class_price"));
                    flightInDeposit.put("second_class_price", rs.getInt("second_class_price"));
                    flightInDeposit.put("reason", rs.getString("reason"));
                    flightInDeposit.put("rescheduled", rs.getDate("rescheduled"));
                    flightsInDeposit.add(flightInDeposit);
                } else {
                    Map<String, Object> flightInDeposit = new HashMap<>();
                    for (String field : fields) {
                        if (field.equals("departure_date") || field.equals("rescheduled")) {
                            flightInDeposit.put(field, rs.getDate(field));
                        } else if (field.equals("airport_name") || field.equals("reason")) {
                            flightInDeposit.put(field, rs.getString(field));
                        } else {
                            flightInDeposit.put(field, rs.getInt(field));
                        }
                    }
                    flightsInDeposit.add(flightInDeposit);
                }
            }
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(FlightController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return flightsInDeposit;
    }

    public void deleteFlightInDeposit(List<SearchCondition> searchConditions) {

        String deleteStatement = Utilities.formDeleteStatement("flights_deposit", searchConditions);

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

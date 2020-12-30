package mfcc2pl.sqlutilities.controllers;

import mfcc2pl.Utilities;
import mfcc2pl.sqlutilities.model.Flight;
import mfcc2pl.sqlutilities.model.SearchCondition;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FlightController {

    public Connection conn;

    public FlightController(Connection conn) {
        this.conn = conn;
    }

    public List<Map<String, Object>> selectFlights(List<String> fields, List<SearchCondition> searchConditions) {
        List<Map<String, Object>> flights = new ArrayList<>();

        String selectStatement = Utilities.formSelectStatement(fields, "flights", searchConditions);

        try {
            PreparedStatement pstmt = conn.prepareStatement(selectStatement);

            ControllerUtilities.preparedSelectOrDeleteStatementSetParameters(pstmt, searchConditions);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> flight = new HashMap<>();
                if (fields.size() == 1 && fields.get(0).equals("*")) {
                    addAllFlightAttributesForSelect(rs, flight);
                } else {
                    addSomeFlightAttributesForSelect(rs, flight, fields);
                }
                flights.add(flight);
            }
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(FlightController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return flights;
    }

    public void insertFlight(Flight flight) {
        try {
            String insertStatement = "insert into flights(id, departure_date, duration, delay, distance, stopovers, airport_name, airplane_id, first_class_seats, second_class_seats, first_class_price, second_class_price) " +
                    "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertStatement);

            setAllFlightParametersForInsert(pstmt, flight.getId(), flight.getDepartureDate(), flight.getDuration(), flight.getDelay(), flight.getDistance(), flight.getStopovers(), flight.getAirportName(), flight.getAirplaneId(), flight.getFirstClassSeats(), flight.getSecondClassSeats(), flight.getFirstClassPrice(), flight.getSecondClassPrice());

            pstmt.executeUpdate();
            pstmt.close();

            // make a refresh of the materialized view referred in the other database
            ControllerUtilities.refreshMaterializedView("flight_details");
        } catch (SQLException ex) {
            Logger.getLogger(FlightController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateFlights(String updateType, String fieldName, Object fieldValue, List<SearchCondition> searchConditions) {
        String updateStatement;
        switch (updateType) {
            case "i":
                updateStatement = Utilities.formUpdateStatementIncrement("flights", fieldName, searchConditions);
                break;
            case "d":
                updateStatement = Utilities.formUpdateStatementDecrement("flights", fieldName, searchConditions);
                break;
            default:
                updateStatement = Utilities.formUpdateStatement("flights", fieldName, searchConditions);
                break;
        }

        try {
            PreparedStatement pstmt = conn.prepareStatement(updateStatement);

            ControllerUtilities.preparedUpdateStatementSetParameters(pstmt, updateType, fieldValue, searchConditions);

            pstmt.executeUpdate();
            pstmt.close();

            if (fieldName.equals("id") || fieldName.equals("first_class_price") || fieldName.equals("second_class_price") || fieldName.equals("stopovers")) {
                // make a refresh of the materialized view referred in the other database
                ControllerUtilities.refreshMaterializedView("flight_details");
            }
        } catch (SQLException ex) {
            Logger.getLogger(FlightController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void deleteFlights(List<SearchCondition> searchConditions) {
        String deleteStatement = Utilities.formDeleteStatement("flights", searchConditions);

        try {
            PreparedStatement pstmt = conn.prepareStatement(deleteStatement);

            ControllerUtilities.preparedSelectOrDeleteStatementSetParameters(pstmt, searchConditions);

            pstmt.executeUpdate();
            pstmt.close();

            // make a refresh of the materialized view referred in the other database
            ControllerUtilities.refreshMaterializedView("flight_details");
        } catch (SQLException ex) {
            Logger.getLogger(FlightController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected static void addAllFlightAttributesForSelect(ResultSet rs, Map<String, Object> flight) throws SQLException {
        flight.put("id", rs.getInt("id"));
        flight.put("departure_date", rs.getDate("departure_date"));
        flight.put("duration", rs.getInt("duration"));
        flight.put("delay", rs.getInt("delay"));
        flight.put("distance", rs.getInt("distance"));
        flight.put("stopovers", rs.getInt("stopovers"));
        flight.put("airport_name", rs.getString("airport_name"));
        flight.put("airplane_id", rs.getInt("airplane_id"));
        flight.put("first_class_seats", rs.getInt("first_class_seats"));
        flight.put("second_class_seats", rs.getInt("second_class_seats"));
        flight.put("first_class_price", rs.getInt("first_class_price"));
        flight.put("second_class_price", rs.getInt("second_class_price"));
    }

    protected static void addSomeFlightAttributesForSelect(ResultSet rs, Map<String, Object> flight, List<String> fields) throws SQLException {
        for (String field : fields) {
            if (field.equals("departure_date")) {
                flight.put(field, rs.getDate(field));
            } else if (field.equals("airport_name")) {
                flight.put(field, rs.getString(field));
            } else {
                flight.put(field, rs.getInt(field));
            }
        }
    }

    protected static void setAllFlightParametersForInsert(PreparedStatement pstmt, int id, Date departureDate, int duration, int delay, int distance, int stopovers, String airportName, int airplaneId, int firstClassSeats, int secondClassSeats, int firstClassPrice, int secondClassPrice) throws SQLException {
        pstmt.setInt(1, id);
        pstmt.setDate(2, departureDate);
        pstmt.setInt(3, duration);
        pstmt.setInt(4, delay);
        pstmt.setInt(5, distance);
        pstmt.setInt(6, stopovers);
        pstmt.setString(7, airportName);
        pstmt.setInt(8, airplaneId);
        pstmt.setInt(9, firstClassSeats);
        pstmt.setInt(10, secondClassSeats);
        pstmt.setInt(11, firstClassPrice);
        pstmt.setInt(12, secondClassPrice);
    }
}

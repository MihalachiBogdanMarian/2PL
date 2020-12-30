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
                    Map<String, Object> flight = new HashMap<>();
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
                    flights.add(flight);
                } else {
                    Map<String, Object> flight = new HashMap<>();
                    for (String field : fields) {
                        if (field.equals("departure_date")) {
                            flight.put(field, rs.getDate(field));
                        } else if (field.equals("airport_name")) {
                            flight.put(field, rs.getString(field));
                        } else {
                            flight.put(field, rs.getInt(field));
                        }
                    }
                    flights.add(flight);
                }
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
            pstmt.setInt(1, flight.getId());
            pstmt.setDate(2, flight.getDepartureDate());
            pstmt.setInt(3, flight.getDuration());
            pstmt.setInt(4, flight.getDelay());
            pstmt.setInt(5, flight.getDistance());
            pstmt.setInt(6, flight.getStopovers());
            pstmt.setString(7, flight.getAirportName());
            pstmt.setInt(8, flight.getAirplaneId());
            pstmt.setInt(9, flight.getFirstClassSeats());
            pstmt.setInt(10, flight.getSecondClassSeats());
            pstmt.setInt(11, flight.getFirstClassPrice());
            pstmt.setInt(12, flight.getSecondClassPrice());
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(FlightController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateFlights(String updateType, String fieldName, Object fieldValue, List<SearchCondition> searchConditions) {
        String updateStatement;
        if (updateType.equals("u")) {
            updateStatement = Utilities.formUpdateStatement("flights", fieldName, searchConditions);
        } else if (updateType.equals("i")) {
            updateStatement = Utilities.formUpdateStatementIncrement("flights", fieldName, searchConditions);
        } else if (updateType.equals("d")) {
            updateStatement = Utilities.formUpdateStatementDecrement("flights", fieldName, searchConditions);
        } else {
            updateStatement = Utilities.formUpdateStatement("flights", fieldName, searchConditions);
        }

        try {
            PreparedStatement pstmt = conn.prepareStatement(updateStatement);
            if (fieldValue instanceof Integer) {
                pstmt.setInt(1, (Integer) fieldValue);
            } else if (fieldValue instanceof Date) {
                pstmt.setDate(1, (Date) fieldValue);
            } else if (fieldValue instanceof String) {
                pstmt.setString(1, fieldValue.toString());
            }

            for (int i = 0; i < searchConditions.size(); i++) {
                if (searchConditions.get(i).getValue() instanceof Integer) {
                    if (updateType.equals("i") || updateType.equals("d")) {
                        pstmt.setInt(i + 1, (Integer) searchConditions.get(i).getValue());
                    } else {
                        pstmt.setInt(i + 2, (Integer) searchConditions.get(i).getValue());
                    }
                } else if (searchConditions.get(i).getValue() instanceof Date) {
                    if (updateType.equals("i") || updateType.equals("d")) {
                        pstmt.setDate(i + 1, (Date) searchConditions.get(i).getValue());
                    } else {
                        pstmt.setDate(i + 2, (Date) searchConditions.get(i).getValue());
                    }
                } else if (searchConditions.get(i).getValue() instanceof String) {
                    if (updateType.equals("i") || updateType.equals("d")) {
                        pstmt.setString(i + 1, searchConditions.get(i).getValue().toString());
                    } else {
                        pstmt.setString(i + 2, searchConditions.get(i).getValue().toString());
                    }
                }
            }

            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(FlightController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void deleteFlights(List<SearchCondition> searchConditions) {
        String deleteStatement = Utilities.formDeleteStatement("flights", searchConditions);

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
            Logger.getLogger(FlightController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

package mfcc2pl.sqlutilities.controllers;

import mfcc2pl.Utilities;
import mfcc2pl.sqlutilities.model.SearchCondition;
import mfcc2pl.sqlutilities.model.Ticket;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TicketController {

    public Connection conn;

    public TicketController(Connection conn) {
        this.conn = conn;
    }

    public void insertTicket(Ticket ticket) {
        try {
            String insertStatement = "insert into tickets(code, price, class, passenger_id, flight_id, stopover) " +
                    "values (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertStatement);
            pstmt.setInt(1, ticket.getCode());
            pstmt.setInt(2, ticket.getPrice());
            pstmt.setInt(3, ticket.getSeatClass());
            pstmt.setInt(4, ticket.getPassengerId());
            pstmt.setInt(5, ticket.getFlightId());
            pstmt.setInt(6, ticket.getStopover());
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(TicketController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<Map<String, Object>> selectTickets(List<String> fields, List<SearchCondition> searchConditions) {
        List<Map<String, Object>> tickets = new ArrayList<>();

        String selectStatement = Utilities.formSelectStatement(fields, "tickets", searchConditions);

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
                    Map<String, Object> ticket = new HashMap<>();
                    ticket.put("code", rs.getInt("code"));
                    ticket.put("price", rs.getInt("price"));
                    ticket.put("class", rs.getInt("class"));
                    ticket.put("passenger_id", rs.getInt("passenger_id"));
                    ticket.put("flight_id", rs.getInt("flight_id"));
                    ticket.put("stopover", rs.getInt("stopover"));
                    tickets.add(ticket);
                } else {
                    Map<String, Object> ticket = new HashMap<>();
                    for (String field : fields) {
                        ticket.put(field, rs.getInt(field));
                    }
                    tickets.add(ticket);
                }
            }
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(FlightController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return tickets;
    }

    public void deleteTickets(List<SearchCondition> searchConditions) {

        String deleteStatement = Utilities.formDeleteStatement("tickets", searchConditions);

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
            Logger.getLogger(TicketController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

package mfcc2pl.sqlutilities.controllers;

import mfcc2pl.sqlutilities.model.SearchCondition;
import mfcc2pl.sqlutilities.model.Ticket;

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

public class TicketController {

    public Connection conn;

    public TicketController(Connection conn) {
        this.conn = conn;
    }

    public List<Map<String, Object>> selectTickets(List<String> fields, List<SearchCondition> searchConditions) {
        List<Map<String, Object>> tickets = new ArrayList<>();

        String selectStatement = ControllerUtilities.formSelectStatement(fields, "tickets", searchConditions);

        try {
            PreparedStatement pstmt = conn.prepareStatement(selectStatement);

            ControllerUtilities.preparedSelectOrDeleteStatementSetParameters(pstmt, searchConditions);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> ticket = new HashMap<>();
                if (fields.size() == 1 && fields.get(0).equals("*")) {
                    ticket.put("code", rs.getInt("code"));
                    ticket.put("price", rs.getInt("price"));
                    ticket.put("class", rs.getInt("class"));
                    ticket.put("passenger_id", rs.getInt("passenger_id"));
                    ticket.put("flight_id", rs.getInt("flight_id"));
                    ticket.put("stopover", rs.getInt("stopover"));
                } else {
                    for (String field : fields) {
                        ticket.put(field, rs.getInt(field));
                    }
                }
                tickets.add(ticket);
            }
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(TicketController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return tickets;
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

    public void updateTickets(String updateType, String fieldName, Object fieldValue, List<SearchCondition> searchConditions) {
        String updateStatement;
        switch (updateType) {
            case "i":
                updateStatement = ControllerUtilities.formUpdateStatementIncrement("tickets", fieldName, searchConditions);
                break;
            case "d":
                updateStatement = ControllerUtilities.formUpdateStatementDecrement("tickets", fieldName, searchConditions);
                break;
            default:
                updateStatement = ControllerUtilities.formUpdateStatement("tickets", fieldName, searchConditions);
                break;
        }

        try {
            PreparedStatement pstmt = conn.prepareStatement(updateStatement);

            ControllerUtilities.preparedUpdateStatementSetParameters(pstmt, updateType, fieldValue, searchConditions);

            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(TicketController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void deleteTickets(List<SearchCondition> searchConditions) {
        String deleteStatement = ControllerUtilities.formDeleteStatement("tickets", searchConditions);

        try {
            PreparedStatement pstmt = conn.prepareStatement(deleteStatement);

            ControllerUtilities.preparedSelectOrDeleteStatementSetParameters(pstmt, searchConditions);

            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(TicketController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

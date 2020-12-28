package mfcc2pl.utilities2pl.operations;

import mfcc2pl.sqlutilities.controllers.*;
import mfcc2pl.sqlutilities.model.*;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class Insert extends AbstractOperation {

    private Object objectToInsert;

    public Insert(String tableName, Object objectToInsert) {
        this.setName("insert");
        this.setTableName(tableName);
        this.objectToInsert = objectToInsert;
    }

    @Override
    public List<Map<String, Object>> execute(Connection conn1, Connection conn2) {
        switch (this.tableName) {
            case "airplanes":
                break;
            case "companies":
                break;
            case "feedback":
                new FeedbackController(conn2).insertFeedback((Feedback) objectToInsert);
                break;
            case "flights":
                new FlightController(conn2).insertFlight((Flight) objectToInsert);
                break;
            case "flights_cache":
                new FlightInCacheController(conn1).insertFlightInCache((FlightInCache) objectToInsert);
                break;
            case "flights_staff":
                new FlightStaffController(conn1).insertFlightStaff((FlightStaff) objectToInsert);
                break;
            case "stopovers":
                new StopoverController(conn2).insertStopover((Stopover) objectToInsert);
                break;
            case "tickets":
                new TicketController(conn1).insertTicket((Ticket) objectToInsert);
                break;
            case "users":
                new UserController(conn1).insertUser((User) objectToInsert);
                break;
            default:
                break;
        }
        return null;
    }

    @Override
    public String toString() {
        return "Insert{" +
                "name='" + name + '\'' +
                ", tableName='" + tableName + '\'' +
                ", objectToInsert=" + objectToInsert +
                '}';
    }
}

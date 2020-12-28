package mfcc2pl.utilities2pl.operations;

import mfcc2pl.sqlutilities.controllers.*;
import mfcc2pl.sqlutilities.model.*;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Insert extends AbstractOperation {

    private final Object objectToInsert;

    public Insert(String tableName, Object objectToInsert) {
        this.setName("insert");
        this.setTableName(tableName);
        this.objectToInsert = objectToInsert;
    }

    @Override
    public List<Map<String, Object>> execute(Connection conn1, Connection conn2) {
        switch (this.tableName) {
            case "feedback":
                new FeedbackController(conn2).insertFeedback((Feedback) objectToInsert);
                break;
            case "flights":
                new FlightController(conn2).insertFlight((Flight) objectToInsert);
                break;
            case "flights_cache":
                new FlightInDepositController(conn1).insertFlightInCache((FlightInDeposit) objectToInsert);
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
    public Operation compensationOperation() {
        switch (this.tableName) {
            case "users":
                return new Delete(this.tableName, Arrays.asList(new SearchCondition("id", "=", ((User) this.objectToInsert).getId())));
            case "flights":
                return new Delete(this.tableName, Arrays.asList(new SearchCondition("id", "=", ((Flight) this.objectToInsert).getId())));
            case "flights_deposit":
                return new Delete(this.tableName, Arrays.asList(new SearchCondition("id", "=", ((FlightInDeposit) this.objectToInsert).getId())));
            case "tickets":
                return new Delete(this.tableName, Arrays.asList(new SearchCondition("id", "=", ((Ticket) this.objectToInsert).getCode())));
            case "feedback":
                return new Delete(this.tableName, Arrays.asList(new SearchCondition("user_id", "=", ((Feedback) this.objectToInsert).getUserId()),
                        new SearchCondition("company_id", "=", ((Feedback) this.objectToInsert).getCompanyId()),
                        new SearchCondition("message", "=", ((Feedback) this.objectToInsert).getMessage())));
            case "stopovers":
                return new Delete(this.tableName, Arrays.asList(new SearchCondition("stop_number", "=", ((Stopover) this.objectToInsert).getStopNumber()),
                        new SearchCondition("flight_id", "=", ((Stopover) this.objectToInsert).getStopNumber())));
            case "flights_staff":
                return new Delete(this.tableName, Arrays.asList(new SearchCondition("flight_id", "=", ((FlightStaff) this.objectToInsert).getFlightId()),
                        new SearchCondition("user_id", "=", ((FlightStaff) this.objectToInsert).getUserId())));
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

package mfcc2pl.utilities2pl.operations;

import mfcc2pl.sqlutilities.controllers.*;
import mfcc2pl.sqlutilities.model.SearchCondition;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class Delete extends AbstractOperation {

    private List<SearchCondition> searchConditions;

    public Delete(String tableName, List<SearchCondition> searchConditions) {
        this.setName("delete");
        this.setTableName(tableName);
        this.searchConditions = searchConditions;
    }

    @Override
    public List<Map<String, Object>> execute(Connection conn1, Connection conn2) {
        switch (this.tableName) {
            case "airplanes":
                break;
            case "companies":
                break;
            case "feedback":
                break;
            case "flights":
                new FlightController(conn2).deleteFlights(searchConditions);
                break;
            case "flights_cache":
                break;
            case "flights_staff":
                new FlightStaffController(conn1).deleteFlightStaff(searchConditions);
                break;
            case "stopovers":
                new StopoverController(conn2).deleteStopovers(searchConditions);
                break;
            case "tickets":
                new TicketController(conn1).deleteTickets(searchConditions);
                break;
            case "users":
                new UserController(conn1).deleteUsers(searchConditions);
                break;
            default:
                break;
        }
        return null;
    }

    @Override
    public String toString() {
        return "Delete{" +
                "name='" + name + '\'' +
                ", tableName='" + tableName + '\'' +
                ", searchConditions=" + searchConditions +
                '}';
    }
}

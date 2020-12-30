package mfcc2pl.utilities2pl.operations;

import mfcc2pl.sqlutilities.controllers.*;
import mfcc2pl.sqlutilities.model.*;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Delete extends AbstractOperation {

    private final List<SearchCondition> searchConditions;
    private final List<Object> objectsBeforeDeletion;

    public Delete(String tableName, List<SearchCondition> searchConditions) {
        this.setName("delete");
        this.setTableName(tableName);
        this.searchConditions = searchConditions;
        this.objectsBeforeDeletion = new ArrayList<>();
    }

    @Override
    public List<Map<String, Object>> execute(Connection conn1, Connection conn2) {
        List<Map<String, Object>> objectsBeforeDeletion;
        switch (this.tableName) {
            case "flights":
                FlightController flightController = new FlightController(conn2);
                objectsBeforeDeletion = flightController.selectFlights(Arrays.asList("*"), searchConditions);
                convertMapToObjects(this.tableName, objectsBeforeDeletion);
                flightController.deleteFlights(searchConditions);
                break;
            case "flights_deposit":
                FlightInDepositController flightInDepositController = new FlightInDepositController(conn1);
                objectsBeforeDeletion = flightInDepositController.selectFlightsInDeposit(Arrays.asList("*"), searchConditions);
                convertMapToObjects(this.tableName, objectsBeforeDeletion);
                flightInDepositController.deleteFlightInDeposit(searchConditions);
                break;
            case "flights_staff":
                FlightStaffController flightStaffController = new FlightStaffController(conn1);
                objectsBeforeDeletion = flightStaffController.selectFlightStaff(Arrays.asList("*"), searchConditions);
                convertMapToObjects(this.tableName, objectsBeforeDeletion);
                flightStaffController.deleteFlightStaff(searchConditions);
                break;
            case "stopovers":
                StopoverController stopoverController = new StopoverController(conn2);
                objectsBeforeDeletion = stopoverController.selectStopovers(Arrays.asList("*"), searchConditions);
                convertMapToObjects(this.tableName, objectsBeforeDeletion);
                stopoverController.deleteStopovers(searchConditions);
                break;
            case "tickets":
                TicketController ticketController = new TicketController(conn1);
                objectsBeforeDeletion = ticketController.selectTickets(Arrays.asList("*"), searchConditions);
                convertMapToObjects(this.tableName, objectsBeforeDeletion);
                ticketController.deleteTickets(searchConditions);
                break;
            case "users":
                UserController userController = new UserController(conn1);
                objectsBeforeDeletion = userController.selectUsers(Arrays.asList("*"), searchConditions);
                convertMapToObjects(this.tableName, objectsBeforeDeletion);
                userController.deleteUsers(searchConditions);
                break;
            default:
                break;
        }
        return null;
    }

    @Override
    public List<Operation> compensationOperations() {
        List<Operation> compensationOperations = new ArrayList<>();
        for (Object objectBeforeDeletion : this.objectsBeforeDeletion) {
            compensationOperations.add(new Insert(this.tableName, objectBeforeDeletion));
        }
        return compensationOperations;
    }

    @Override
    public String toString() {
        return "Delete{" +
                "name='" + name + '\'' +
                ", tableName='" + tableName + '\'' +
                ", searchConditions=" + searchConditions +
                '}';
    }

    private void convertMapToObjects(String tableName, List<Map<String, Object>> objectsBeforeDeletion) {
        switch (tableName) {
            case "flights":
                for (Map<String, Object> objectBeforeDeletion : objectsBeforeDeletion) {
                    this.objectsBeforeDeletion.add(new Flight((Integer) objectBeforeDeletion.get("id"),
                            (Date) objectBeforeDeletion.get("departure_date"),
                            (Integer) objectBeforeDeletion.get("duration"),
                            (Integer) objectBeforeDeletion.get("delay"),
                            (Integer) objectBeforeDeletion.get("distance"),
                            (Integer) objectBeforeDeletion.get("stopovers"),
                            objectBeforeDeletion.get("airport_name").toString(),
                            (Integer) objectBeforeDeletion.get("airplane_id"),
                            (Integer) objectBeforeDeletion.get("first_class_seats"),
                            (Integer) objectBeforeDeletion.get("second_class_seats"),
                            (Integer) objectBeforeDeletion.get("first_class_price"),
                            (Integer) objectBeforeDeletion.get("second_class_price")
                    ));
                }
                break;
            case "flights_staff":
                for (Map<String, Object> objectBeforeDeletion : objectsBeforeDeletion) {
                    this.objectsBeforeDeletion.add(new FlightStaff((Integer) objectBeforeDeletion.get("flight_id"),
                            (Integer) objectBeforeDeletion.get("user_id"),
                            objectBeforeDeletion.get("user_type").toString()
                    ));
                }
                break;
            case "stopovers":
                for (Map<String, Object> objectBeforeDeletion : objectsBeforeDeletion) {
                    this.objectsBeforeDeletion.add(new Stopover((Integer) objectBeforeDeletion.get("stop_number"),
                            (Integer) objectBeforeDeletion.get("flight_id"),
                            objectBeforeDeletion.get("airport_name").toString(),
                            (Integer) objectBeforeDeletion.get("time"),
                            (Integer) objectBeforeDeletion.get("price_first_class"),
                            (Integer) objectBeforeDeletion.get("price_second_class"),
                            (Date) objectBeforeDeletion.get("departure_date")
                    ));
                }
                break;
            case "tickets":
                for (Map<String, Object> objectBeforeDeletion : objectsBeforeDeletion) {
                    this.objectsBeforeDeletion.add(new Ticket((Integer) objectBeforeDeletion.get("code"),
                            (Integer) objectBeforeDeletion.get("price"),
                            (Integer) objectBeforeDeletion.get("class"),
                            (Integer) objectBeforeDeletion.get("passenger_id"),
                            (Integer) objectBeforeDeletion.get("flight_id"),
                            (Integer) objectBeforeDeletion.get("stopover")
                    ));
                }
                break;
            case "users":
                for (Map<String, Object> objectBeforeDeletion : objectsBeforeDeletion) {
                    this.objectsBeforeDeletion.add(new User((Integer) objectBeforeDeletion.get("id"),
                            objectBeforeDeletion.get("first_name").toString(),
                            objectBeforeDeletion.get("last_name").toString(),
                            (Date) objectBeforeDeletion.get("birthday"),
                            objectBeforeDeletion.get("address").toString(),
                            objectBeforeDeletion.get("phone_number").toString(),
                            objectBeforeDeletion.get("email").toString(),
                            objectBeforeDeletion.get("password").toString(),
                            objectBeforeDeletion.get("type").toString()
                    ));
                }
                break;
            default:
                break;
        }
    }
}

package mfcc2pl.utilities2pl.operations;

import mfcc2pl.sqlutilities.controllers.FlightController;
import mfcc2pl.sqlutilities.controllers.TicketController;
import mfcc2pl.sqlutilities.controllers.UserController;
import mfcc2pl.sqlutilities.model.SearchCondition;

import java.sql.Connection;
import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Update extends AbstractOperation {

    private final String updateType;
    private final String fieldName;
    private final Object fieldValue;
    private final List<SearchCondition> searchConditions;
    private Object fieldValueBeforeUpdate;

    public Update(String tableName, String updateType, String fieldName, Object fieldValue, List<SearchCondition> searchConditions) {
        this.setName("update");
        this.setTableName(tableName);
        this.updateType = updateType;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.searchConditions = searchConditions;
        this.fieldValueBeforeUpdate = null;
    }

    @Override
    public List<Map<String, Object>> execute(Connection conn1, Connection conn2) {
        switch (this.tableName) {
            case "flights":
                FlightController flightController = new FlightController(conn2);
                if (fieldName.equals("departure_date")) {
                    fieldValueBeforeUpdate = (Date) flightController.selectFlights(Arrays.asList(fieldName), searchConditions).get(0).get(fieldName);
                } else if (fieldName.equals("airport_name")) {
                    fieldValueBeforeUpdate = flightController.selectFlights(Arrays.asList(fieldName), searchConditions).get(0).get(fieldName).toString();
                } else {
                    fieldValueBeforeUpdate = (Integer) flightController.selectFlights(Arrays.asList(fieldName), searchConditions).get(0).get(fieldName);
                }
                flightController.updateFlights(updateType, fieldName, fieldValue, searchConditions);
                break;
            case "tickets":
                TicketController ticketController = new TicketController(conn1);
                fieldValueBeforeUpdate = (Integer) ticketController.selectTickets(Arrays.asList(fieldName), searchConditions).get(0).get(fieldName);
                ticketController.updateTickets(updateType, fieldName, fieldValue, searchConditions);
                break;
            case "users":
                UserController userController = new UserController(conn1);
                if (fieldName.equals("id") || fieldName.equals("logged")) {
                    fieldValueBeforeUpdate = (Integer) userController.selectUsers(Arrays.asList(fieldName), searchConditions).get(0).get(fieldName);
                } else if (fieldName.equals("birthday")) {
                    fieldValueBeforeUpdate = (Date) userController.selectUsers(Arrays.asList(fieldName), searchConditions).get(0).get(fieldName);
                } else {
                    fieldValueBeforeUpdate = userController.selectUsers(Arrays.asList(fieldName), searchConditions).get(0).get(fieldName).toString();
                }
                userController.updateUsers(updateType, fieldName, fieldValue, searchConditions);
                break;
            default:
                break;
        }
        return null;
    }

    @Override
    public Operation compensationOperation() {
        return new Update(this.tableName, "u", this.fieldName, this.fieldValueBeforeUpdate, this.searchConditions);
    }

    @Override
    public String toString() {
        return "Update{" +
                "name='" + name + '\'' +
                ", tableName='" + tableName + '\'' +
                ", updateType='" + updateType + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", fieldValue=" + fieldValue +
                ", searchConditions=" + searchConditions +
                '}';
    }
}

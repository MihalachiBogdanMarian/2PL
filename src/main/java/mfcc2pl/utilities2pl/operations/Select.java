package mfcc2pl.utilities2pl.operations;

import mfcc2pl.sqlutilities.controllers.*;
import mfcc2pl.sqlutilities.model.SearchCondition;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Select extends AbstractOperation {

    private final List<String> fields;
    private final List<SearchCondition> searchConditions;

    public Select(String tableName, List<String> fields, List<SearchCondition> searchConditions) {
        this.setName("select");
        this.setTableName(tableName);
        this.fields = fields;
        this.searchConditions = searchConditions;
    }

    @Override
    public List<Map<String, Object>> execute(Connection conn1, Connection conn2) {
        List<Map<String, Object>> result = new ArrayList<>();
        switch (this.tableName) {
            case "airplanes":
                break;
            case "companies":
                result = new CompanyController(conn2).selectCompanies(this.fields, this.searchConditions);
                break;
            case "feedback":
                result = new FeedbackController(conn2).selectFeedback(this.fields, this.searchConditions);
                break;
            case "flights":
                result = new FlightController(conn2).selectFlights(this.fields, this.searchConditions);
                break;
            case "flights_cache":
                break;
            case "flights_staff":
                break;
            case "stopovers":
                result = new StopoverController(conn2).selectStopovers(this.fields, this.searchConditions);
                break;
            case "tickets":
                break;
            case "users":
                result = new UserController(conn1).selectUsers(this.fields, this.searchConditions);
                break;
            default:
                break;
        }
        return result;
    }

    @Override
    public String toString() {
        return "Select{" +
                "name='" + name + '\'' +
                ", tableName='" + tableName + '\'' +
                ", fields=" + fields +
                ", searchConditions=" + searchConditions +
                '}';
    }
}

package mfcc2pl.utilities2pl.operations;

import java.util.List;
import java.util.Map;

public class Select extends AbstractOperation {

    private List<String> fields;
    private List<SearchCondition> searchConditions;

    public Select(String tableName, List<String> fields, List<SearchCondition> searchConditions) {
        this.setName("select");
        this.setTableName(tableName);
        this.fields = fields;
        this.searchConditions = searchConditions;
    }

    @Override
    public List<Map<String, Object>> execute() {
        switch (this.tableName) {
            case "airplanes":
                break;
            case "companies":
                break;
            case "feedback":
                break;
            case "flights":
                break;
            case "flights_cache":
                break;
            case "flights_staff":
                break;
            case "stopovers":
                break;
            case "tickets":
                break;
            case "users":
                break;
            default:
                break;
        }
        return null;
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

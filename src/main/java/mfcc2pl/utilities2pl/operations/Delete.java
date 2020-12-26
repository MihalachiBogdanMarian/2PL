package mfcc2pl.utilities2pl.operations;

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
        return "Delete{" +
                "name='" + name + '\'' +
                ", tableName='" + tableName + '\'' +
                ", searchConditions=" + searchConditions +
                '}';
    }
}

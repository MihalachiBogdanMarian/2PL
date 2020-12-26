package mfcc2pl.utilities2pl.operations;

import java.util.List;
import java.util.Map;

public class Update extends AbstractOperation {

    private String updateType;
    private String fieldName;
    private Object fieldValue;
    private List<SearchCondition> searchConditions;

    public Update(String tableName, String updateType, String fieldName, Object fieldValue, List<SearchCondition> searchConditions) {
        this.setName("update");
        this.setTableName(tableName);
        this.updateType = updateType;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
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

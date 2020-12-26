package mfcc2pl.utilities2pl.operations;

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
        return "Insert{" +
                "name='" + name + '\'' +
                ", tableName='" + tableName + '\'' +
                ", objectToInsert=" + objectToInsert +
                '}';
    }
}

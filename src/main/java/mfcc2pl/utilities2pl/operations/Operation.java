package mfcc2pl.utilities2pl.operations;

import java.util.List;
import java.util.Map;

public interface Operation {

    String getName();

    void setName(String name);

    String getTableName();

    void setTableName(String name);

    default Operation reverseOperation() {
        return null;
    };

    default List<Map<String, Object>> execute() {
        System.out.println(this.getClass().toString() + " executing...");
        return null;
    }

}

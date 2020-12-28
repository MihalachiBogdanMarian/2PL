package mfcc2pl.utilities2pl.operations;

import java.io.Serializable;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

public interface Operation extends Serializable {

    String getName();

    void setName(String name);

    String getTableName();

    void setTableName(String name);

    default List<Map<String, Object>> execute(Connection conn1, Connection conn2) {
        System.out.println(this.getClass().toString() + " executing...");
        return null;
    }

    default Operation compensationOperation() {
        return null;
    }

    default List<Operation> compensationOperations() {
        return null;
    }
}

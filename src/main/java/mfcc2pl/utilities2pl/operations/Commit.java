package mfcc2pl.utilities2pl.operations;

import mfcc2pl.sqlutilities.dbconnection.Database;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class Commit extends AbstractOperation {

    public Commit() {
        this.setName("commit");
    }

    @Override
    public List<Map<String, Object>> execute(Connection conn1, Connection conn2) {
        Database.commit(conn1);
        Database.commit(conn2);
        return null;
    }

    @Override
    public String toString() {
        return "Commit{" +
                "name='" + name + '\'' +
                '}';
    }
}

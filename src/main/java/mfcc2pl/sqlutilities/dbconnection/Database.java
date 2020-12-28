package mfcc2pl.sqlutilities.dbconnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Map.entry;

public class Database {

    private static final String URL = "jdbc:oracle:thin:@localhost:1521:XE";
    private static final String USER1 = "c##airport1";
    private static final String PASSWORD1 = "c##airport1";
    private static final String USER2 = "c##airport2";
    private static final String PASSWORD2 = "c##airport2";

    private static final Map<String, Integer> tablesMappingToConnections = Map.ofEntries(
            entry("users", 1),
            entry("tickets", 1),
            entry("flights_cache", 1),
            entry("flights_staff", 1),
            entry("companies", 2),
            entry("feedback", 2),
            entry("airplanes", 2),
            entry("flights", 2),
            entry("stopovers", 2),
            entry("flight_details", 1),
            entry("user_ids", 2)
    );

    private Database() {
    }

    public static Connection getConnection(int nr) {
        if (nr == 1) {
            return createConnection(1);
        } else {
            return createConnection(2);
        }
    }

    public static Connection getConnection(String tableName) {
        int nr = tablesMappingToConnections.get(tableName);
        return getConnection(nr);
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void commit(Connection conn) {
        if (conn != null) {
            try {
                conn.commit();
            } catch (SQLException ex) {
                Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void rollback(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static Connection createConnection(int nr) {
        Connection conn = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("The driver couldn't be registered!");
            System.exit(0);
        }
        try {
            if (nr == 1) {
                conn = DriverManager.getConnection(URL, USER1, PASSWORD1);
//                conn.setAutoCommit(false);
            } else {
                conn = DriverManager.getConnection(URL, USER2, PASSWORD2);
//                conn.setAutoCommit(false);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conn;
    }
}

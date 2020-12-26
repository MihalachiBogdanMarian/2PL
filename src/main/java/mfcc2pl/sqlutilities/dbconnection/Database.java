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
    private static Connection connection1 = null;
    private static Connection connection2 = null;

    private static final Map<String, Integer> tablesMappingToConnections = Map.ofEntries(
            entry("users", 1),
            entry("tickets", 1),
            entry("flights_cache", 1),
            entry("flights_staff", 1),
            entry("companies", 2),
            entry("feedback", 2),
            entry("airplanes", 2),
            entry("flights", 2),
            entry("stopovers", 2)
    );

    private Database() {
    }

    public static Connection getConnection(int nr) {
        if (nr == 1) {
            if (connection1 == null) {
                createConnection(1);
            }
            return connection1;
        } else {
            if (connection2 == null) {
                createConnection(2);
            }
            return connection2;
        }
    }

    public static Connection getConnection(String tableName) {
        int nr = tablesMappingToConnections.get(tableName);
        return getConnection(nr);
    }

    public static void closeConnection(int nr) {
        if (nr == 1) {
            if (connection1 != null) {
                try {
                    connection1.close();
                } catch (SQLException ex) {
                    Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            if (connection2 != null) {
                try {
                    connection2.close();
                } catch (SQLException ex) {
                    Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static void closeConnection(String tableName) {
        int nr = tablesMappingToConnections.get(tableName);
        closeConnection(nr);
    }

    public static void commit(int nr) {
        if (nr == 1) {
            if (connection1 != null) {
                try {
                    connection1.commit();
                } catch (SQLException ex) {
                    Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            if (connection2 != null) {
                try {
                    connection2.commit();
                } catch (SQLException ex) {
                    Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static void commit(String tableName) {
        int nr = tablesMappingToConnections.get(tableName);
        commit(nr);
    }

    public static void rollback(int nr) {
        if (nr == 1) {
            if (connection1 != null) {
                try {
                    connection1.rollback();
                } catch (SQLException ex) {
                    Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            if (connection2 != null) {
                try {
                    connection2.rollback();
                } catch (SQLException ex) {
                    Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static void rollback(String tableName) {
        int nr = tablesMappingToConnections.get(tableName);
        rollback(nr);
    }

    private static void createConnection(int nr) {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("The driver couldn't be registered!");
            System.exit(0);
        }
        try {
            if (nr == 1) {
                connection1 = DriverManager.getConnection(URL, USER1, PASSWORD1);
                connection1.setAutoCommit(false);
            } else {
                connection2 = DriverManager.getConnection(URL, USER2, PASSWORD2);
                connection2.setAutoCommit(false);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

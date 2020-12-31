package mfcc2pl.network;

import mfcc2pl.sqlutilities.model.*;
import mfcc2pl.utilities2pl.operations.*;

import java.sql.Date;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Transactions {

    protected final static Map<Integer, List<Operation>> transactions = new HashMap<>();

    static {
        // multiple only read locks
        transactions.put(-10, Arrays.asList(
                new Select("users", Arrays.asList("*"), Arrays.asList()),
                new Commit()
        ));
        transactions.put(-9, Arrays.asList(
                new Select("users", Arrays.asList("*"), Arrays.asList()),
                new Commit()
        ));
        transactions.put(-8, Arrays.asList(
                new Select("users", Arrays.asList("*"), Arrays.asList()),
                new Commit()
        ));


        // deadlock test
        transactions.put(-7, Arrays.asList(
                new Select("users", Arrays.asList("*"), Arrays.asList()),
                new Update("flights", "u", "duration", 1000, Arrays.asList(new SearchCondition("id", "=", "15"))),
                new Commit()
        ));
        transactions.put(-6, Arrays.asList(
                new Update("flights", "u", "duration", 2000, Arrays.asList(new SearchCondition("id", "=", "15"))),
                new Update("users", "u", "address", "Cluj", Arrays.asList(new SearchCondition("id", "=", "15"))),
                new Commit()
        ));

        // course test
        transactions.put(-5, Arrays.asList(
                new Update("users", "u", "address", "Cluj", Arrays.asList(new SearchCondition("id", "=", "10"))),
                new Update("flights", "u", "duration", 1, Arrays.asList(new SearchCondition("id", "=", "10"))),
                new Update("tickets", "u", "price", 1, Arrays.asList(new SearchCondition("code", "=", "10"))),
                new Commit()
        ));
        transactions.put(-4, Arrays.asList(
                new Select("users", Arrays.asList("*"), Arrays.asList()),
                new Update("flights", "i", "duration", "duration", Arrays.asList(new SearchCondition("id", "=", "10"))),
                new Commit()
        ));
        transactions.put(-3, Arrays.asList(
                new Select("tickets", Arrays.asList("*"), Arrays.asList()),
                new Update("flights", "u", "duration", 3, Arrays.asList(new SearchCondition("id", "=", "10"))),
                new Update("tickets", "u", "price", 3, Arrays.asList(new SearchCondition("code", "=", "10"))),
                new Commit()
        ));

        // initial test
        transactions.put(-2, Arrays.asList(
                new Insert("users", new User(100, "John", "White", Date.valueOf("1990-03-16"), "New York", "0789438629", "john100@user.com", "8DHW6LC20O", "user")),
                new Select("users", Arrays.asList("id"), Arrays.asList(new SearchCondition("email", "=", "john1@user.com"), new SearchCondition("password", "=", "8DHW6LC20O"))),
                new Update("users", "u", "logged", 1, Arrays.asList(new SearchCondition("id", "=", 100))),
                new Commit()
        ));
        transactions.put(-1, Arrays.asList(
                new Commit()
        ));

        /* USE CASES */
        /* register a new user and log him in */
        transactions.put(0, Arrays.asList(
                new Insert("users", new User(100, "John", "White", Date.valueOf("1990-03-16"), "New York", "0789438629", "john100@user.com", "8DHW6LC20O", "user")),
                new Select("users", Arrays.asList("id"), Arrays.asList(new SearchCondition("email", "=", "john1@user.com"), new SearchCondition("password", "=", "8DHW6LC20O"))),
                new Update("users", "u", "logged", 1, Arrays.asList(new SearchCondition("id", "=", 100))),
                new Commit()
        ));
        /* log out user */
        transactions.put(1, Arrays.asList(
                new Update("users", "u", "logged", 0, Arrays.asList(new SearchCondition("id", "=", 100))),
                new Commit()
        ));
        /* post feedback to a company following a flight */
        transactions.put(2, Arrays.asList(
                new Select("users", Arrays.asList("id"), Arrays.asList(new SearchCondition("email", "=", "john1@user.com"), new SearchCondition("password", "=", "8DHW6LC20O"))),
                new Update("users", "u", "logged", 1, Arrays.asList(new SearchCondition("id", "=", 100))),
                new Select("companies", Arrays.asList("id"), Arrays.asList(new SearchCondition("name", "=", "SWISS"))),
                new Insert("feedback", new Feedback(50, 19, "Horrible flight! Dirty seats and uneducated flight attendants!")),
                new Update("users", "u", "logged", 0, Arrays.asList(new SearchCondition("id", "=", 100))),
                new Commit()
        ));
        /* analyze feedback by company */
        transactions.put(3, Arrays.asList(
                new Select("companies", Arrays.asList("id"), Arrays.asList(new SearchCondition("name", "=", "SWISS"))),
                new Select("feedback", Arrays.asList("*"), Arrays.asList(new SearchCondition("company_id", "=", "19"))),
                new Commit()
        ));
        /* register a new flight into the database - done by an admin */
        transactions.put(4, Arrays.asList(
                new Select("users", Arrays.asList("id"), Arrays.asList(new SearchCondition("email", "=", "dave101@user.com"), new SearchCondition("password", "=", "8GMLWPO90S"), new SearchCondition("type", "=", "admin"))),
                new Update("users", "u", "logged", 1, Arrays.asList(new SearchCondition("id", "=", 101))),
                new Insert("flights", new Flight(200, Date.valueOf("2021-01-14"), 180, 0, 2500, 1, "Dallas/Fort Worth", 15, 75, 5, 450, 800)),
                new Insert("stopovers", new Stopover(1, 200, "Copenhagen Airport", 90, 225, 400, Date.valueOf("2021-01-14"))),
                new Insert("flights_staff", new FlightStaff(200, 1, "pilot")),
                new Insert("flights_staff", new FlightStaff(200, 4, "fa")),
                new Insert("flights_staff", new FlightStaff(200, 5, "fa")),
                new Insert("flights_staff", new FlightStaff(200, 6, "fa")),
                new Update("users", "u", "logged", 0, Arrays.asList(new SearchCondition("id", "=", 101))),
                new Commit()
        ));
        /* remove a successfully completed flight - done by an admin */
        transactions.put(5, Arrays.asList(
                new Select("users", Arrays.asList("id"), Arrays.asList(new SearchCondition("email", "=", "dave101@user.com"), new SearchCondition("password", "=", "8GMLWPO90S"), new SearchCondition("type", "=", "admin"))),
                new Update("users", "u", "logged", 1, Arrays.asList(new SearchCondition("id", "=", 101))),
                new Select("flights", Arrays.asList("*"), Arrays.asList(new SearchCondition("id", "=", "200"))),
                new Delete("stopovers", Arrays.asList(new SearchCondition("flight_id", "=", "200"))),
                new Delete("flights_staff", Arrays.asList(new SearchCondition("flight_id", "=", "200"))),
                new Insert("flights_deposit", new FlightInDeposit(200, Date.valueOf("2021-01-14"), 180, 0, 2500, 1, "Dallas/Fort Worth", 15, 75, 5, 450, 800, "success", null)),
                new Delete("flights", Arrays.asList(new SearchCondition("id", "=", "200"))),
                new Update("users", "u", "logged", 0, Arrays.asList(new SearchCondition("id", "=", 101))),
                new Commit()
        ));
        /* reschedule a flight - done by an admin */
        transactions.put(6, Arrays.asList(
                new Select("users", Arrays.asList("id"), Arrays.asList(new SearchCondition("email", "=", "dave101@user.com"), new SearchCondition("password", "=", "8GMLWPO90S"), new SearchCondition("type", "=", "admin"))),
                new Update("users", "u", "logged", 1, Arrays.asList(new SearchCondition("id", "=", 101))),
                new Select("flights", Arrays.asList("*"), Arrays.asList(new SearchCondition("id", "=", "101"))),
                new Insert("flights_deposit", new FlightInDeposit(101, Date.valueOf("2021-01-18"), 180, 0, 2500, 3, "Beijing Capital International Airport", 1, 150, 80, 450, 800, "bad weather", Date.valueOf("2021-01-28"))),
                new Update("flights", "u", "departure_date", Date.valueOf("2021-01-28"), Arrays.asList(new SearchCondition("id", "=", 101))),
                new Update("users", "u", "logged", 0, Arrays.asList(new SearchCondition("id", "=", 101))),
                new Commit()
        ));
        /* buy a ticket */
        transactions.put(7, Arrays.asList(
                new Select("users", Arrays.asList("id"), Arrays.asList(new SearchCondition("email", "=", "john1@user.com"), new SearchCondition("password", "=", "8DHW6LC20O"))),
                new Update("users", "u", "logged", 1, Arrays.asList(new SearchCondition("id", "=", 100))),
                new Select("flights", Arrays.asList("*"), Arrays.asList(new SearchCondition("id", "=", "102"))),
                new Insert("tickets", new Ticket(500, 300, 1, 100, 102, 0)),
                new Update("users", "u", "type", "passenger", Arrays.asList(new SearchCondition("id", "=", 100))),
                new Update("flights", "d", "first_class_seats", "first_class_seats", Arrays.asList(new SearchCondition("id", "=", 102))),
                new Update("users", "u", "logged", 0, Arrays.asList(new SearchCondition("id", "=", 100))),
                new Commit()
        ));
        /* retract a ticket */
        transactions.put(8, Arrays.asList(
                new Select("users", Arrays.asList("id"), Arrays.asList(new SearchCondition("email", "=", "john1@user.com"), new SearchCondition("password", "=", "8DHW6LC20O"))),
                new Update("users", "u", "logged", 1, Arrays.asList(new SearchCondition("id", "=", 100))),
                new Delete("tickets", Arrays.asList(new SearchCondition("passenger_id", "=", "100"), new SearchCondition("flight_id", "=", "102"))),
                new Update("users", "u", "type", "user", Arrays.asList(new SearchCondition("id", "=", 100))),
                new Update("flights", "i", "first_class_seats", "first_class_seats", Arrays.asList(new SearchCondition("id", "=", 102))),
                new Update("users", "u", "logged", 0, Arrays.asList(new SearchCondition("id", "=", 100))),
                new Commit()
        ));
        /* check the total price per flight - done by a manager */
        transactions.put(9, Arrays.asList(
                new Select("users", Arrays.asList("id"), Arrays.asList(new SearchCondition("email", "=", "dave101@user.com"), new SearchCondition("password", "=", "8GMLWPO90S"), new SearchCondition("type", "=", "admin"))),
                new Update("users", "u", "logged", 1, Arrays.asList(new SearchCondition("id", "=", 101))),
                new Select("flights", Arrays.asList("*"), Arrays.asList()),
                new Select("stopovers", Arrays.asList("*"), Arrays.asList()),
                new Update("users", "u", "logged", 0, Arrays.asList(new SearchCondition("id", "=", 101))),
                new Commit()
        ));
    }

    public static Map<Integer, List<Operation>> getTransactions() {
        return transactions;
    }
}

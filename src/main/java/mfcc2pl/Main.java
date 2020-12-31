package mfcc2pl;

import mfcc2pl.sqlutilities.controllers.UserController;
import mfcc2pl.sqlutilities.dbconnection.Database;
import mfcc2pl.sqlutilities.model.SearchCondition;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        System.out.println(Utilities.formSelectStatement(Arrays.asList("id"),
                "users",
                Arrays.asList(new SearchCondition("email", "=", "dave101@user.com"),
                        new SearchCondition("password", "=", "8GMLWPO90S"))));

        List<Map<String, Object>> users = new UserController(Database.getConnection("users")).selectUsers(Arrays.asList("*"),
                Arrays.asList(new SearchCondition("email", "=", "dave101@user.com"),
                        new SearchCondition("password", "=", "8GMLWPO90S")));
        for (Map<String, Object> user : users) {
            System.out.println(user.toString());
        }

        users = new UserController(Database.getConnection("users")).selectUsers(Arrays.asList("id"),
                Arrays.asList(new SearchCondition("email", "=", "dave101@user.com"),
                        new SearchCondition("password", "=", "8GMLWPO90S")));
        for (Map<String, Object> user : users) {
            System.out.println(user.toString());
        }

//        new UserController(Database.getConnection("users")).insertUser(new User(300, "Laurel", "Thomas", Date.valueOf("1995-07-08"),
//                "Tokyo", "0723999845", "laurel300@user.com", "4DU97GH3W8", "user"));
//        Database.commit("users");

        System.out.println(Utilities.formUpdateStatement("users",
                "logged",
                Arrays.asList(new SearchCondition("id", "=", "11"))));
        System.out.println(Utilities.formUpdateStatementIncrement("flights",
                "first_class_seats",
                Arrays.asList(new SearchCondition("id", "=", "60"))));
        System.out.println(Utilities.formDeleteStatement("flights",
                Arrays.asList(new SearchCondition("passenger_id", "=", "100"), new SearchCondition("flight_id", "=", "60"))));

//        new UserController(Database.getConnection("users")).updateUsers("u", "first_name", "David", Arrays.asList(new SearchCondition("email", "=", "dave101@user.com"),
//                new SearchCondition("password", "=", "8GMLWPO90S")));
//        Database.commit("users");

//        new UserController(Database.getConnection("users")).deleteUsers(Arrays.asList(new SearchCondition("id", "=", "300")));
//        Database.commit("users");

//        new FlightController(Database.getConnection("flights")).updateFlights("u", "departure_date", Date.valueOf("2021-10-25"), Arrays.asList(new SearchCondition("id", "=", "11")));
//        Database.commit("flights");

//        new UserController(Database.getConnection("users")).insertUser(new User(1000, "Nathaniel", "Zabawa", Date.valueOf("1995-11-22"), "Los Angeles", "0701909808", "nathaniel10@user.com", "00PAN23YWE", "passenger"));
//        Database.commit("users");
    }
}

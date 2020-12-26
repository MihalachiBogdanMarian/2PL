package mfcc2pl;

import mfcc2pl.sqlutilities.controllers.FlightController;
import mfcc2pl.sqlutilities.controllers.UserController;
import mfcc2pl.utilities2pl.operations.SearchCondition;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        System.out.println(Utilities.formSelectStatement(Arrays.asList("id"),
                "users",
                Arrays.asList(new SearchCondition("email", "=", "christina1@pilot.com"),
                        new SearchCondition("password", "=", "CKL8XK70MR"))));

        List<Map<String, Object>> users = new UserController().selectUsers(Arrays.asList("*"),
                Arrays.asList(new SearchCondition("email", "=", "christina1@pilot.com"),
                        new SearchCondition("password", "=", "CKL8XK70MR")));
        for (Map<String, Object> user : users) {
            System.out.println(user.toString());
        }

        users = new UserController().selectUsers(Arrays.asList("id"),
                Arrays.asList(new SearchCondition("email", "=", "christina1@pilot.com"),
                        new SearchCondition("password", "=", "CKL8XK70MR")));
        for (Map<String, Object> user : users) {
            System.out.println(user.toString());
        }

//        new UserController().insertUser(new User(300, "Laurel", "Thomas", Date.valueOf("1995-07-08"),
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

        new UserController().updateUsers("u", "first_name", "Christina", Arrays.asList(new SearchCondition("email", "=", "christina1@pilot.com"),
                new SearchCondition("password", "=", "CKL8XK70MR")));

//        Database.commit("users");

        new UserController().deleteUsers(Arrays.asList(new SearchCondition("id", "=", "300")));

//        Database.commit("users");

        new FlightController().updateFlights("u", "departure_date", Date.valueOf("2021-10-25"), Arrays.asList(new SearchCondition("id", "=", "11")));

//        Database.commit("flights");


    }
}

package mfcc2pl.sqlutilities.model;

import java.io.Serializable;
import java.sql.Date;

public class User implements Serializable {

    private int id;
    private String firstName;
    private String lastName;
    private Date birthday;
    private String address;
    private String phoneNumber;
    private String email;
    private String password;
    private String type;
    private int logged;

    public User() {
        this.id = -1;
        this.firstName = "";
        this.lastName = "";
        this.birthday = null;
        this.address = "";
        this.phoneNumber = "";
        this.email = "";
        this.password = "";
        this.type = "user";
        this.logged = 0;
    }

    public User(int id, String firstName, String lastName, Date birthday, String address, String phoneNumber, String email, String password, String type) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.type = type;
        this.logged = 0;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthday=" + birthday +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", type='" + type + '\'' +
                ", logged=" + logged +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLogged() {
        return logged;
    }

    public void setLogged(int logged) {
        this.logged = logged;
    }
}

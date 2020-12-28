package mfcc2pl.sqlutilities.model;

import java.io.Serializable;
import java.sql.Date;

public class Flight implements Serializable {

    private int id;
    private Date departureDate;
    private int duration;
    private int delay;
    private int distance;
    private int stopovers;
    private String airportName;
    private int airplaneId;
    private int firstClassSeats;
    private int secondClassSeats;
    private int firstClassPrice;
    private int secondClassPrice;

    public Flight() {
        this.id = -1;
        this.departureDate = null;
        this.duration = 0;
        this.delay = 0;
        this.distance = 0;
        this.stopovers = 0;
        this.airportName = "";
        this.airplaneId = -1;
        this.firstClassSeats = 0;
        this.secondClassSeats = 0;
        this.firstClassPrice = 0;
        this.secondClassPrice = 0;
    }

    public Flight(int id, Date departureDate, int duration, int delay, int distance, int stopovers, String airportName, int airplaneId, int firstClassSeats, int secondClassSeats, int firstClassPrice, int secondClassPrice) {
        this.id = id;
        this.departureDate = departureDate;
        this.duration = duration;
        this.delay = delay;
        this.distance = distance;
        this.stopovers = stopovers;
        this.airportName = airportName;
        this.airplaneId = airplaneId;
        this.firstClassSeats = firstClassSeats;
        this.secondClassSeats = secondClassSeats;
        this.firstClassPrice = firstClassPrice;
        this.secondClassPrice = secondClassPrice;
    }

    @Override
    public String toString() {
        return "Flight{" +
                "id=" + id +
                ", departureDate=" + departureDate +
                ", duration=" + duration +
                ", delay=" + delay +
                ", distance=" + distance +
                ", stopovers=" + stopovers +
                ", airportName='" + airportName + '\'' +
                ", airplaneId=" + airplaneId +
                ", firstClassSeats=" + firstClassSeats +
                ", secondClassSeats=" + secondClassSeats +
                ", firstClassPrice=" + firstClassPrice +
                ", secondClassSPrice=" + secondClassPrice +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getStopovers() {
        return stopovers;
    }

    public void setStopovers(int stopovers) {
        this.stopovers = stopovers;
    }

    public String getAirportName() {
        return airportName;
    }

    public void setAirportName(String airportName) {
        this.airportName = airportName;
    }

    public int getAirplaneId() {
        return airplaneId;
    }

    public void setAirplaneId(int airplaneId) {
        this.airplaneId = airplaneId;
    }

    public int getFirstClassSeats() {
        return firstClassSeats;
    }

    public void setFirstClassSeats(int firstClassSeats) {
        this.firstClassSeats = firstClassSeats;
    }

    public int getSecondClassSeats() {
        return secondClassSeats;
    }

    public void setSecondClassSeats(int secondClassSeats) {
        this.secondClassSeats = secondClassSeats;
    }

    public int getFirstClassPrice() {
        return firstClassPrice;
    }

    public void setFirstClassPrice(int firstClassPrice) {
        this.firstClassPrice = firstClassPrice;
    }

    public int getSecondClassPrice() {
        return secondClassPrice;
    }

    public void setSecondClassPrice(int secondClassPrice) {
        this.secondClassPrice = secondClassPrice;
    }
}

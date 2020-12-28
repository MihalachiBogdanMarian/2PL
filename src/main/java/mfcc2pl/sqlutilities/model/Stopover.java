package mfcc2pl.sqlutilities.model;

import java.io.Serializable;
import java.sql.Date;

public class Stopover implements Serializable {

    private int stopNumber;
    private int flightId;
    private String airportName;
    private int time;
    private int priceFirstClass;
    private int priceSecondClass;
    private Date departureDate;

    public Stopover() {
        this.stopNumber = 0;
        this.flightId = -1;
        this.airportName = "";
        this.time = 0;
        this.priceFirstClass = 0;
        this.priceSecondClass = 0;
        this.departureDate = null;
    }

    public Stopover(int stopNumber, int flightId, String airportName, int time, int priceFirstClass, int priceSecondClass, Date departureDate) {
        this.stopNumber = stopNumber;
        this.flightId = flightId;
        this.airportName = airportName;
        this.time = time;
        this.priceFirstClass = priceFirstClass;
        this.priceSecondClass = priceSecondClass;
        this.departureDate = departureDate;
    }

    @Override
    public String toString() {
        return "Stopover{" +
                "stopNumber=" + stopNumber +
                ", flightId=" + flightId +
                ", airportName='" + airportName + '\'' +
                ", time=" + time +
                ", priceFirstClass=" + priceFirstClass +
                ", priceSecondClass=" + priceSecondClass +
                ", departureDate=" + departureDate +
                '}';
    }

    public int getStopNumber() {
        return stopNumber;
    }

    public void setStopNumber(int stopNumber) {
        this.stopNumber = stopNumber;
    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public String getAirportName() {
        return airportName;
    }

    public void setAirportName(String airportName) {
        this.airportName = airportName;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getPriceFirstClass() {
        return priceFirstClass;
    }

    public void setPriceFirstClass(int priceFirstClass) {
        this.priceFirstClass = priceFirstClass;
    }

    public int getPriceSecondClass() {
        return priceSecondClass;
    }

    public void setPriceSecondClass(int priceSecondClass) {
        this.priceSecondClass = priceSecondClass;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }
}

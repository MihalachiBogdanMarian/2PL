package mfcc2pl.sqlutilities.model;

public class Ticket {

    private int code;
    private int price;
    private int seatClass;
    private int passengerId;
    private int flightId;
    private int stopover;

    public Ticket() {
        this.code = -1;
        this.price = 0;
        this.seatClass = 1;
        this.passengerId = -1;
        this.flightId = -1;
        this.stopover = 0;
    }

    public Ticket(int code, int price, int seatClass, int passengerId, int flightId, int stopover) {
        this.code = code;
        this.price = price;
        this.seatClass = seatClass;
        this.passengerId = passengerId;
        this.flightId = flightId;
        this.stopover = stopover;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "code=" + code +
                ", price=" + price +
                ", seatClass=" + seatClass +
                ", passengerId=" + passengerId +
                ", flightId=" + flightId +
                ", stopover=" + stopover +
                '}';
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getSeatClass() {
        return seatClass;
    }

    public void setSeatClass(int seatClass) {
        this.seatClass = seatClass;
    }

    public int getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(int passengerId) {
        this.passengerId = passengerId;
    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public int getStopover() {
        return stopover;
    }

    public void setStopover(int stopover) {
        this.stopover = stopover;
    }
}

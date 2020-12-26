package mfcc2pl.sqlutilities.model;

public class FlightStaff {

    private int flightId;
    private int userId;
    private String userType;

    public FlightStaff() {
        this.flightId = -1;
        this.userId = -1;
        this.userType = "pilot";
    }

    public FlightStaff(int flightId, int userId, String userType) {
        this.flightId = flightId;
        this.userId = userId;
        this.userType = userType;
    }

    @Override
    public String toString() {
        return "FlightStaff{" +
                "flightId=" + flightId +
                ", userId=" + userId +
                ", userType='" + userType + '\'' +
                '}';
    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}

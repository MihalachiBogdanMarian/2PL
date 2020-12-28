package mfcc2pl.sqlutilities.model;

import java.io.Serializable;

public class Airplane implements Serializable {

    private int id;
    private int firstClassSeats;
    private int secondClassSeats;
    private int available;
    private int companyId;

    public Airplane() {
        this.id = -1;
        this.firstClassSeats = 0;
        this.secondClassSeats = 0;
        this.available = 1;
        this.companyId = -1;
    }

    public Airplane(int id, int firstClassSeats, int secondClassSeats, int available, int companyId) {
        this.id = id;
        this.firstClassSeats = firstClassSeats;
        this.secondClassSeats = secondClassSeats;
        this.available = available;
        this.companyId = companyId;
    }

    @Override
    public String toString() {
        return "Airplane{" +
                "id=" + id +
                ", firstClassSeats=" + firstClassSeats +
                ", secondClassSeats=" + secondClassSeats +
                ", available=" + available +
                ", companyId=" + companyId +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }
}

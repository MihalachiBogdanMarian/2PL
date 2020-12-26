package mfcc2pl.sqlutilities.model;

public class Feedback {

    private int userId;
    private int companyId;
    private String message;

    public Feedback() {
        this.userId = -1;
        this.companyId = -1;
        this.message = "";
    }

    public Feedback(int userId, int companyId, String meesage) {
        this.userId = userId;
        this.companyId = companyId;
        this.message = meesage;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "userId=" + userId +
                ", companyId=" + companyId +
                ", meesage='" + message + '\'' +
                '}';
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

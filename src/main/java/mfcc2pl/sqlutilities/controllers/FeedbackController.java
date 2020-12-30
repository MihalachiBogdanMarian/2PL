package mfcc2pl.sqlutilities.controllers;

import mfcc2pl.Utilities;
import mfcc2pl.sqlutilities.model.Feedback;
import mfcc2pl.sqlutilities.model.SearchCondition;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FeedbackController {

    public Connection conn;

    public FeedbackController(Connection conn) {
        this.conn = conn;
    }

    public List<Map<String, Object>> selectFeedback(List<String> fields, List<SearchCondition> searchConditions) {
        List<Map<String, Object>> feedbackList = new ArrayList<>();

        String selectStatement = Utilities.formSelectStatement(fields, "feedback", searchConditions);

        try {
            PreparedStatement pstmt = conn.prepareStatement(selectStatement);

            ControllerUtilities.preparedSelectOrDeleteStatementSetParameters(pstmt, searchConditions);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> feedback = new HashMap<>();
                if (fields.size() == 1 && fields.get(0).equals("*")) {
                    feedback.put("user_id", rs.getInt("user_id"));
                    feedback.put("company_id", rs.getInt("company_id"));
                    feedback.put("message", rs.getString("message"));
                } else {
                    for (String field : fields) {
                        if (field.equals("message")) {
                            feedback.put(field, rs.getString(field));
                        } else {
                            feedback.put(field, rs.getInt(field));
                        }
                    }
                }
                feedbackList.add(feedback);
            }
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(FeedbackController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return feedbackList;
    }

    public void insertFeedback(Feedback feedback) {
        try {
            String insertStatement = "insert into feedback(user_id, company_id, message) " +
                    "values (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertStatement);
            pstmt.setInt(1, feedback.getUserId());
            pstmt.setInt(2, feedback.getCompanyId());
            pstmt.setString(3, feedback.getMessage());
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(FeedbackController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

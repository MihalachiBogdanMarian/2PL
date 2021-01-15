package mfcc2pl.sqlutilities.controllers;

import mfcc2pl.sqlutilities.model.SearchCondition;

import javax.swing.text.Utilities;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CompanyController {

    public Connection conn;

    public CompanyController(Connection conn) {
        this.conn = conn;
    }

    public List<Map<String, Object>> selectCompanies(List<String> fields, List<SearchCondition> searchConditions) {
        List<Map<String, Object>> companies = new ArrayList<>();

        String selectStatement = ControllerUtilities.formSelectStatement(fields, "companies", searchConditions);

        try {
            PreparedStatement pstmt = conn.prepareStatement(selectStatement);

            ControllerUtilities.preparedSelectOrDeleteStatementSetParameters(pstmt, searchConditions);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> company = new HashMap<>();
                if (fields.size() == 1 && fields.get(0).equals("*")) {
                    company.put("id", rs.getInt("id"));
                    company.put("name", rs.getString("name"));
                    company.put("address", rs.getString("address"));
                    company.put("city", rs.getString("city"));
                    company.put("phone_number", rs.getString("phone_number"));
                    company.put("email", rs.getString("email"));
                } else {
                    for (String field : fields) {
                        if (field.equals("id")) {
                            company.put(field, rs.getInt(field));
                        } else {
                            company.put(field, rs.getString(field));
                        }
                    }
                }
                companies.add(company);
            }
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(CompanyController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return companies;
    }
}

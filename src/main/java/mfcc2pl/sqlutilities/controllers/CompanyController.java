package mfcc2pl.sqlutilities.controllers;

import mfcc2pl.Utilities;
import mfcc2pl.sqlutilities.dbconnection.Database;
import mfcc2pl.utilities2pl.operations.SearchCondition;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CompanyController {

    public static List<Map<String, Object>> selectCompanies(List<String> fields, List<SearchCondition> searchConditions) {
        List<Map<String, Object>> companies = new ArrayList<>();

        Connection con = Database.getConnection("companies");

        String selectStatement = Utilities.formSelectStatement(fields, "companies", searchConditions);

        try {
            PreparedStatement pstmt = con.prepareStatement(selectStatement);

            for (int i = 0; i < searchConditions.size(); i++) {
                if (searchConditions.get(i).getValue() instanceof Integer) {
                    pstmt.setInt(i + 1, (Integer) searchConditions.get(i).getValue());
                } else if (searchConditions.get(i).getValue() instanceof Date) {
                    pstmt.setDate(i + 1, (Date) searchConditions.get(i).getValue());
                } else if (searchConditions.get(i).getValue() instanceof String) {
                    pstmt.setString(i + 1, searchConditions.get(i).getValue().toString());
                }
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                if (fields.size() == 1 && fields.get(0).equals("*")) {
                    Map<String, Object> company = new HashMap<>();
                    company.put("id", rs.getInt("id"));
                    company.put("name", rs.getString("name"));
                    company.put("address", rs.getString("address"));
                    company.put("city", rs.getString("city"));
                    company.put("phone_number", rs.getString("phone_number"));
                    company.put("email", rs.getString("email"));
                    companies.add(company);
                } else {
                    Map<String, Object> company = new HashMap<>();
                    for (String field : fields) {
                        if (field.equals("id")) {
                            company.put(field, rs.getInt(field));
                        } else {
                            company.put(field, rs.getString(field));
                        }
                    }
                    companies.add(company);
                }
            }
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(CompanyController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return companies;
    }
}

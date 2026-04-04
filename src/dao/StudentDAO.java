package dao;

import java.sql.*;
import util.DBConnection;

public class StudentDAO {

    public void addStudent(String id, String name, String course, String email, String phone) {

        try (Connection con = DBConnection.getConnection()) {

            String sql = "INSERT INTO students VALUES (?,?,?,?,?)";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, id);
            ps.setString(2, name);
            ps.setString(3, course);
            ps.setString(4, email);
            ps.setString(5, phone);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
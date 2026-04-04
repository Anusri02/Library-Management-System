package dao;

import java.sql.*;
import util.DBConnection;

public class BookDAO {

    public void addBook(String id, String title, String author, String publisher, String availability, int quantity) {

        try (Connection con = DBConnection.getConnection()) {

            String sql = "INSERT INTO books (id, title, author, publisher, availability, quantity) VALUES (?,?,?,?,?,?)";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, id);
            ps.setString(2, title);
            ps.setString(3, author);
            ps.setString(4, publisher);
            ps.setString(5, availability);
            ps.setInt(6, quantity);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
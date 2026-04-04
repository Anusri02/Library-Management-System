package util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() {
        Connection con = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/library?useSSL=false&serverTimezone=UTC",
                "root",
                "Anusri@02"
            );

            System.out.println("Connection successful!");

        } catch (Exception e) {
            System.out.println("Connection failed: " + e);
        }

        return con;
    }
    public static void main(String[] args) {
    Connection con = getConnection();
    System.out.println("Connected: " + (con != null));
}
}

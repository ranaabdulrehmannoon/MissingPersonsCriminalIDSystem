package application;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Explicitly load the MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = "jdbc:mysql://localhost:3306/oop_project?useSSL=false";
            String user = "root"; // replace if your username is different
            String password = "Your password"; // replace with your actual password

            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connection successful!");
        } catch (Exception e) {
            System.out.println("Connection failed! Details:");
            System.out.println("URL: jdbc:mysql://localhost:3306/oop_project?useSSL=false");
            e.printStackTrace();
        }
        return conn;
    }
}

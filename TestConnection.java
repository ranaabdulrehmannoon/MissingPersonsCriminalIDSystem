package application;

import java.sql.Connection;
import java.sql.DriverManager;

public class TestConnection {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/oop_project";
        String user = "root";
        String password = "your actual password"; // Replace with your MySQL password
        
        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Database connected successfully!");
            conn.close();
        } catch (Exception e) {
            System.err.println("❌ Connection failed: " + e.getMessage());
        }
    }

}

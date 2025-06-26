package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AdminDashboardController {
    @FXML private StackPane contentPane;
    @FXML private Button btnManageUsers;
    @FXML private Button btnGenerateReports;
    @FXML private Button btnLogout;
    @FXML private Button btnDashboard;
    
    @FXML private BarChart<String, Number> missingPersonsChart;
    @FXML private BarChart<String, Number> casesByStatusChart;
    @FXML private Label totalUsersLabel;
    @FXML private Label activeCasesLabel;
    @FXML private Label solvedCasesLabel;

    @FXML
    public void initialize() {
        try {
            // Initialize charts with actual data
            initializeMissingPersonsChart();
            initializeCasesByStatusChart();
            
            // Load statistics from database
            loadStatistics();
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load dashboard data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void initializeMissingPersonsChart() throws SQLException {
        missingPersonsChart.setTitle("Missing Persons Statistics");
        missingPersonsChart.setLegendVisible(false);
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        
        try (Connection conn = DatabaseConnection.getConnection()) {
           
            String sql24h = "SELECT COUNT(*) AS count FROM registermissingpersons " +
                          "WHERE date_missing >= NOW() - INTERVAL 1 DAY";
            try (PreparedStatement stmt = conn.prepareStatement(sql24h);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    series.getData().add(new XYChart.Data<>("Last 24h", rs.getInt("count")));
                }
            }
            
            // Last week
            String sqlWeek = "SELECT COUNT(*) AS count FROM registermissingpersons " +
                           "WHERE date_missing >= NOW() - INTERVAL 7 DAY";
            try (PreparedStatement stmt = conn.prepareStatement(sqlWeek);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    series.getData().add(new XYChart.Data<>("Last Week", rs.getInt("count")));
                }
            }
            
            // Last month
            String sqlMonth = "SELECT COUNT(*) AS count FROM registermissingpersons " +
                             "WHERE date_missing >= NOW() - INTERVAL 30 DAY";
            try (PreparedStatement stmt = conn.prepareStatement(sqlMonth);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    series.getData().add(new XYChart.Data<>("Last Month", rs.getInt("count")));
                }
            }
            
            // Last year
            String sqlYear = "SELECT COUNT(*) AS count FROM registermissingpersons " +
                           "WHERE date_missing >= NOW() - INTERVAL 365 DAY";
            try (PreparedStatement stmt = conn.prepareStatement(sqlYear);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    series.getData().add(new XYChart.Data<>("Last Year", rs.getInt("count")));
                }
            }
        }
        
        missingPersonsChart.getData().add(series);
    }
    
    private void initializeCasesByStatusChart() throws SQLException {
        casesByStatusChart.setTitle("Case Status Distribution");
        casesByStatusChart.setLegendVisible(false);
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT status, COUNT(*) AS count FROM registermissingpersons GROUP BY status";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    series.getData().add(new XYChart.Data<>(
                        rs.getString("status"), 
                        rs.getInt("count")
                    ));
                }
            }
        }
        
        casesByStatusChart.getData().add(series);
    }
    
    private void loadStatistics() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Total users
            String usersSql = "SELECT COUNT(*) AS count FROM users";
            try (PreparedStatement stmt = conn.prepareStatement(usersSql);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    totalUsersLabel.setText(String.valueOf(rs.getInt("count")));
                }
            }
            
            // Active cases
            String activeSql = "SELECT COUNT(*) AS count FROM registermissingpersons WHERE status = 'Active'";
            try (PreparedStatement stmt = conn.prepareStatement(activeSql);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    activeCasesLabel.setText(String.valueOf(rs.getInt("count")));
                }
            }
            
            // Solved cases
            String solvedSql = "SELECT COUNT(*) AS count FROM registermissingpersons WHERE status = 'Solved'";
            try (PreparedStatement stmt = conn.prepareStatement(solvedSql);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    solvedCasesLabel.setText(String.valueOf(rs.getInt("count")));
                }
            }
        }
    }
    
    @FXML
    private void handleDashboard() {
        try {
            Parent dashboardContent = FXMLLoader.load(getClass().getResource("/fxml/AdminDashboard.fxml"));
            Stage stage = (Stage) btnDashboard.getScene().getWindow();
            stage.setScene(new Scene(dashboardContent));
            stage.setFullScreen(true);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to reload dashboard: " + e.getMessage());
        }
    }

    @FXML
    private void handleManageUsers() {
        loadContent("/fxml/UserManagement.fxml");
    }

    @FXML
    private void handleGenerateReports() {
        loadContent("/fxml/ReportGenerator.fxml");
    }

    @FXML
    private void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setFullScreen(true);
        } catch (IOException e) {
            showAlert("Error", "Cannot logout: " + e.getMessage());
        }
    }

    private void loadContent(String fxmlPath) {
        try {
            Parent content = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentPane.getChildren().clear();
            contentPane.getChildren().add(content);
        } catch (IOException e) {
            showAlert("Error", "Cannot load content: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
package application;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class DashboardController implements Initializable {
    @FXML private StackPane contentPane;
    @FXML private Label totalCasesLabel;
    @FXML private Label solvedCasesLabel;
    @FXML private Label activeCasesLabel;
    @FXML private ListView<String> recentActivityList;
    @FXML private Text welcomeText;

    @Override
    public void initialize(URL location, java.util.ResourceBundle resources) {
        // Set welcome message with username
        welcomeText.setText("Welcome, " + Controller.currentUsername);
        
        loadDashboardData();
    }

    private void loadDashboardData() {
        try {
            // Load statistics
            loadStatistics();
            
            // Load recent activity
            loadRecentActivity();
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load dashboard data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDashboard() {
        try {
            // Reload the dashboard by loading the FXML again
            Parent dashboardContent = FXMLLoader.load(getClass().getResource("/fxml/Dashboard.fxml"));
            Stage stage = (Stage) contentPane.getScene().getWindow();
            stage.setScene(new Scene(dashboardContent));
            stage.setFullScreen(true);
        } catch (IOException e) {
            showAlert("Error", "Failed to reload dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ... (keep all your existing methods the same)
    // Only add the new handleDashboard() method above

    private void loadStatistics() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Total missing cases
            String totalCasesSql = "SELECT COUNT(*) AS total FROM registermissingpersons";
            try (PreparedStatement stmt = conn.prepareStatement(totalCasesSql);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    totalCasesLabel.setText(String.valueOf(rs.getInt("total")));
                }
            }

            // Solved cases (assuming you have a status field)
            String solvedCasesSql = "SELECT COUNT(*) AS solved FROM registermissingpersons WHERE status = 'Solved'";
            try (PreparedStatement stmt = conn.prepareStatement(solvedCasesSql);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    solvedCasesLabel.setText(String.valueOf(rs.getInt("solved")));
                }
            }

            // Active cases (assuming you have a status field)
            String activeCasesSql = "SELECT COUNT(*) AS active FROM registermissingpersons WHERE status = 'Active'";
            try (PreparedStatement stmt = conn.prepareStatement(activeCasesSql);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    activeCasesLabel.setText(String.valueOf(rs.getInt("active")));
                }
            }
        }
    }

    private void loadRecentActivity() throws SQLException {
        ObservableList<String> activities = FXCollections.observableArrayList();
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get recent missing person cases (last 5)
            String missingCasesSql = "SELECT id, full_name, date_missing FROM registermissingpersons " +
                                   "ORDER BY date_missing DESC LIMIT 3";
            try (PreparedStatement stmt = conn.prepareStatement(missingCasesSql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String formattedDate = formatDate(rs.getDate("date_missing"));
                    activities.add(String.format("New missing person case - %s (ID: %d, Date: %s)",
                            rs.getString("full_name"), rs.getInt("id"), formattedDate));
                }
            }

            // Get recent criminal records (last 2)
            String criminalRecordsSql = "SELECT id, full_name FROM criminal_records " +
                                     "ORDER BY id DESC LIMIT 2";
            try (PreparedStatement stmt = conn.prepareStatement(criminalRecordsSql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    activities.add(String.format("New criminal record added - %s (ID: %d)",
                            rs.getString("full_name"), rs.getInt("id")));
                }
            }

            recentActivityList.setItems(activities);
        }
    }

    private String formatDate(java.sql.Date date) {
        if (date == null) return "Unknown date";
        LocalDate localDate = date.toLocalDate();
        return localDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
    }

    @FXML
    private void handleRegisterMissing() {
        loadContent("/fxml/RegisterMissingPerson.fxml");
    }
    
    @FXML
    private void handleAddCriminal() {
        loadContent("/fxml/AddCriminalRecord.fxml");
    }

    @FXML
    private void handleManageProfile() {
        loadContent("/fxml/ManageProfile.fxml");
    }

    @FXML
    private void handleViewCases() {
        loadContent("/fxml/ViewMissingCases.fxml");
    }

    @FXML
    private void handleViewCriminals() {
        loadContent("/fxml/ViewCriminalRecord.fxml");
    }

    @FXML
    private void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Stage stage = (Stage) contentPane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setFullScreen(true);
        } catch (IOException e) {
            showAlert("Error", "Cannot logout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadContent(String fxmlPath) {
        try {
            if (contentPane == null) {
                throw new IllegalStateException("Content pane is not initialized");
            }
            
            URL resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                throw new IOException("Cannot find resource: " + fxmlPath);
            }
            
            Parent content = FXMLLoader.load(resource);
            contentPane.getChildren().clear();
            contentPane.getChildren().add(content);
        } catch (IOException e) {
            showAlert("Error", "Cannot load content: " + e.getMessage());
            e.printStackTrace();
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
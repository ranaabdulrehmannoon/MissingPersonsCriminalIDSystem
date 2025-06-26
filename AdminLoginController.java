package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdminLoginController {
    @FXML private TextField adminIdField;
    @FXML private PasswordField adminPasswordField;

    @FXML
    public void handleAdminLogin(ActionEvent event) {
        String adminId = adminIdField.getText();
        String password = adminPasswordField.getText();

        if (adminId.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Admin ID and password cannot be empty.");
            return;
        }

        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND is_admin = TRUE";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, adminId);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Set current session data
                Controller.currentUsername = adminId;
                Controller.currentPassword = password;
                Controller.isAdmin = true;

                // Load admin dashboard
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/AdminDashboard.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setFullScreen(true);
                stage.show();
            } else {
                showAlert("Login Failed", "Invalid admin credentials or not an admin account.");
            }

        } catch (Exception e) {
            showAlert("Database Error", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleRegularLoginClick(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setFullScreen(true);
        } catch (Exception e) {
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
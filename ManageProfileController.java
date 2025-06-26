package application;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ManageProfileController {
    @FXML private TextField usernameField;
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;

    @FXML
    public void initialize() {
        usernameField.setText(Controller.currentUsername);
        usernameField.setEditable(false);
    }
    
    @FXML
    private void handleSave() {
        String username = usernameField.getText();
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Error", "New password fields cannot be empty!");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showAlert("Error", "New passwords do not match!");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check if current password matches
            String checkSql = "SELECT password FROM users WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                String dbPassword = rs.getString("password");
                if (!currentPassword.equals(dbPassword)) {
                    showAlert("Error", "Current password is incorrect!");
                    return;
                }
            } else {
                showAlert("Error", "User not found!");
                return;
            }

            // Update password
            String updateSql = "UPDATE users SET password = ? WHERE username = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateSql);
            updateStmt.setString(1, newPassword);
            updateStmt.setString(2, username);

            int rowsAffected = updateStmt.executeUpdate();
            if (rowsAffected > 0) {
                showAlert("Success", "Password updated successfully!");
                ((Stage) btnSave.getScene().getWindow()).close();
            } else {
                showAlert("Error", "Failed to update password.");
            }

        } catch (SQLException e) {
            showAlert("Database Error", e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;

public class UserManagementController {
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> passwordColumn;
    @FXML private TableColumn<User, Boolean> adminColumn;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox adminCheckBox;
    @FXML private Button btnAddUser;
    @FXML private Button btnUpdateUser;
    @FXML private Button btnDeleteUser;
    @FXML private Button btnRefresh;

    private ObservableList<User> usersData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Set up the table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        adminColumn.setCellValueFactory(new PropertyValueFactory<>("admin"));

        // Load user data
        loadUsers();

        // Add selection listener
        usersTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showUserDetails(newValue));
    }

    private void loadUsers() {
        usersData.clear();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM users";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                boolean isAdmin = rs.getBoolean("is_admin");

                usersData.add(new User(id, username, password, isAdmin));
            }

            usersTable.setItems(usersData);

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load users: " + e.getMessage());
        }
    }

    private void showUserDetails(User user) {
        if (user != null) {
            usernameField.setText(user.getUsername());
            passwordField.setText(user.getPassword());
            adminCheckBox.setSelected(user.isAdmin());
        } else {
            usernameField.clear();
            passwordField.clear();
            adminCheckBox.setSelected(false);
        }
    }

    @FXML
    private void handleAddUser() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        boolean isAdmin = adminCheckBox.isSelected();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Validation Error", "Username and password cannot be empty");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO users (username, password, is_admin) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setBoolean(3, isAdmin);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                showAlert("Success", "User added successfully!");
                loadUsers();
                clearFields();
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to add user: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Selection Error", "Please select a user to update");
            return;
        }

        String username = usernameField.getText();
        String password = passwordField.getText();
        boolean isAdmin = adminCheckBox.isSelected();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Validation Error", "Username and password cannot be empty");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE users SET username = ?, password = ?, is_admin = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setBoolean(3, isAdmin);
            stmt.setInt(4, selectedUser.getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                showAlert("Success", "User updated successfully!");
                loadUsers();
                clearFields();
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to update user: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Selection Error", "Please select a user to delete");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to delete user: " + selectedUser.getUsername() + "?");
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String sql = "DELETE FROM users WHERE id = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, selectedUser.getId());

                    int affectedRows = stmt.executeUpdate();
                    if (affectedRows > 0) {
                        showAlert("Success", "User deleted successfully!");
                        loadUsers();
                        clearFields();
                    }
                } catch (SQLException e) {
                    showAlert("Database Error", "Failed to delete user: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleRefresh() {
        loadUsers();
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        adminCheckBox.setSelected(false);
        usersTable.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
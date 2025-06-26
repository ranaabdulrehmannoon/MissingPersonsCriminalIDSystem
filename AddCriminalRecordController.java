package application;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.sql.*;
import java.time.LocalDate;

public class AddCriminalRecordController {
    @FXML private TextField nameField;
    @FXML private TextField aliasesField;
    @FXML private DatePicker dobPicker;
    @FXML private TextArea crimesField;
    @FXML private TextArea locationField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private Button uploadPhotoBtn;
    @FXML private Label photoNameLabel;
    @FXML private Button btnSubmit;
    @FXML private Button btnClear;
    @FXML private Button btnCancel;

    private File photoFile;

    @FXML
    public void initialize() {
        
        statusComboBox.getItems().addAll("Wanted", "In Custody", "Released", "Deceased");
        statusComboBox.getSelectionModel().selectFirst();
        

        dobPicker.setValue(LocalDate.now());
    }

    @FXML
    public void handleUploadPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Criminal Photo");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        photoFile = fileChooser.showOpenDialog(uploadPhotoBtn.getScene().getWindow());
        if (photoFile != null) {
            photoNameLabel.setText(photoFile.getName());
        }
    }

    @FXML
    public void handleSubmit() {
        // Validate required fields
        if (nameField.getText().isEmpty() || 
            dobPicker.getValue() == null ||
            crimesField.getText().isEmpty() ||
            statusComboBox.getValue() == null) {
            
            showAlert("Validation Error", "Please fill all required fields");
            return;
        }

        // Database operation
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO criminal_records (" +
                "full_name, aliases, date_of_birth, crimes, " +
                "last_known_location, status, photo_path) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement stmt = conn.prepareStatement(sql);
            
            // Set parameters
            stmt.setString(1, nameField.getText());
            stmt.setString(2, aliasesField.getText().isEmpty() ? null : aliasesField.getText());
            stmt.setDate(3, Date.valueOf(dobPicker.getValue()));
            stmt.setString(4, crimesField.getText());
            stmt.setString(5, locationField.getText().isEmpty() ? null : locationField.getText());
            stmt.setString(6, statusComboBox.getValue());
            stmt.setString(7, photoFile != null ? photoFile.getAbsolutePath() : null);

            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                showAlert("Success", "Criminal record saved to database!");
                handleClear();
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Error saving record: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleClear() {
        nameField.clear();
        aliasesField.clear();
        dobPicker.setValue(LocalDate.now());
        crimesField.clear();
        locationField.clear();
        statusComboBox.getSelectionModel().selectFirst();
        photoNameLabel.setText("No file selected");
        photoFile = null;
    }

    @FXML
    public void handleCancel() {
        ((Stage) btnCancel.getScene().getWindow()).close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
package application;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;

public class RegisterMissingPersonController {

    @FXML private TextField nameField, ageField, heightField, weightField, cityField, stateField, zipField;
    @FXML private TextField reportingPersonField, relationshipField, contactNumberField, alternateContactField, emailField;
    @FXML private ComboBox<String> genderComboBox, eyeColorComboBox, hairColorComboBox;
    @FXML private DatePicker missingDatePicker;
    @FXML private TextArea featuresField, addressField, notesField;
    @FXML private Button uploadPhotoBtn, btnSubmit, btnClear, btnCancel;
    @FXML private Label photoNameLabel;

    private File photoFile;

    @FXML
    public void initialize() {
        genderComboBox.getItems().addAll("Male", "Female", "Other", "Unknown");
        eyeColorComboBox.getItems().addAll("Black", "Brown", "Blue", "Green", "Hazel", "Gray", "Other");
        hairColorComboBox.getItems().addAll("Black", "Brown", "Blonde", "Red", "Gray", "White", "Other");

        missingDatePicker.setValue(LocalDate.now());

        uploadPhotoBtn.setOnAction(e -> handleUploadPhoto());
        btnSubmit.setOnAction(e -> handleSubmit());
        btnClear.setOnAction(e -> handleClear());
        btnCancel.setOnAction(e -> handleCancel());
    }

    private void handleUploadPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Missing Person Photo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        photoFile = fileChooser.showOpenDialog(uploadPhotoBtn.getScene().getWindow());
        if (photoFile != null) {
            photoNameLabel.setText(photoFile.getName());
        }
    }

    private void handleSubmit() {
        // Validate required fields
        if (nameField.getText().isEmpty() || ageField.getText().isEmpty() ||
                genderComboBox.getValue() == null || missingDatePicker.getValue() == null ||
                addressField.getText().isEmpty() || cityField.getText().isEmpty() ||
                stateField.getText().isEmpty() || reportingPersonField.getText().isEmpty() ||
                relationshipField.getText().isEmpty() || contactNumberField.getText().isEmpty()) {
            showAlert("Validation Error", "Please fill all required fields (marked with *)");
            return;
        }

        try {
            Integer.parseInt(ageField.getText());
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Age must be a number");
            return;
        }

        // SQL Save Logic
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO RegisterMissingPersons (full_name, age, gender, date_missing, height_cm, weight_kg, eye_color, hair_color, features, address, city, state, zip, reporting_person, relationship, contact_number, alternate_contact, email, notes, photo_path) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nameField.getText());
            stmt.setInt(2, Integer.parseInt(ageField.getText()));
            stmt.setString(3, genderComboBox.getValue());
            stmt.setDate(4, java.sql.Date.valueOf(missingDatePicker.getValue()));
            stmt.setString(5, heightField.getText());
            stmt.setString(6, weightField.getText());
            stmt.setString(7, eyeColorComboBox.getValue());
            stmt.setString(8, hairColorComboBox.getValue());
            stmt.setString(9, featuresField.getText());
            stmt.setString(10, addressField.getText());
            stmt.setString(11, cityField.getText());
            stmt.setString(12, stateField.getText());
            stmt.setString(13, zipField.getText());
            stmt.setString(14, reportingPersonField.getText());
            stmt.setString(15, relationshipField.getText());
            stmt.setString(16, contactNumberField.getText());
            stmt.setString(17, alternateContactField.getText());
            stmt.setString(18, emailField.getText());
            stmt.setString(19, notesField.getText());
            stmt.setString(20, photoFile != null ? photoFile.getName() : null);

            stmt.executeUpdate();
            conn.close();

            showAlert("Success", "Missing person report submitted and saved to database!");
            handleClear();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "Could not save data: " + e.getMessage());
        }
    }

    private void handleClear() {
        nameField.clear(); ageField.clear(); heightField.clear(); weightField.clear(); cityField.clear();
        stateField.clear(); zipField.clear(); reportingPersonField.clear(); relationshipField.clear();
        contactNumberField.clear(); alternateContactField.clear(); emailField.clear(); featuresField.clear();
        addressField.clear(); notesField.clear();
        genderComboBox.getSelectionModel().clearSelection();
        eyeColorComboBox.getSelectionModel().clearSelection();
        hairColorComboBox.getSelectionModel().clearSelection();
        missingDatePicker.setValue(LocalDate.now());
        photoNameLabel.setText("No file selected");
        photoFile = null;
    }

    private void handleCancel() {
        StackPane contentPane = (StackPane) btnCancel.getScene().lookup("#contentPane");
        if (contentPane != null) {
            contentPane.getChildren().clear();
            VBox welcomeContent = new VBox(20);
            welcomeContent.setAlignment(Pos.TOP_LEFT);
            welcomeContent.setStyle("-fx-padding: 50;");
            welcomeContent.getChildren().addAll(
                    new Label("Welcome to the Missing Persons & Criminal Identification System") {{
                        setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
                    }},
                    new Label("➤ Register and track missing persons easily.") {{
                        setStyle("-fx-font-size: 18px; -fx-text-fill: #34495e;");
                    }},
                    new Label("➤ Report and manage criminal records efficiently.") {{
                        setStyle("-fx-font-size: 18px; -fx-text-fill: #34495e;");
                    }},
                    new Label("➤ View complete case histories.") {{
                        setStyle("-fx-font-size: 18px; -fx-text-fill: #34495e;");
                    }},
                    new Label("➤ Secure login and user-friendly interface.") {{
                        setStyle("-fx-font-size: 18px; -fx-text-fill: #34495e;");
                    }}
            );
            contentPane.getChildren().add(welcomeContent);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

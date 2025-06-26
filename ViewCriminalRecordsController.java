package application;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class ViewCriminalRecordsController {

    @FXML private TableView<CriminalRecord> criminalTable;
    @FXML private TableColumn<CriminalRecord, Integer> idColumn;
    @FXML private TableColumn<CriminalRecord, String> fullNameColumn;
    @FXML private TableColumn<CriminalRecord, Date> dobColumn;
    @FXML private TableColumn<CriminalRecord, String> statusColumn;
    @FXML private TableColumn<CriminalRecord, String> crimeColumn;

    @FXML
    public void initialize() {
        // Link columns to model fields
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        dobColumn.setCellValueFactory(new PropertyValueFactory<>("dob"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        crimeColumn.setCellValueFactory(new PropertyValueFactory<>("crimes"));

        loadCriminalRecords();
    }

    private void loadCriminalRecords() {
        ObservableList<CriminalRecord> criminalRecords = FXCollections.observableArrayList();

        String sql = "SELECT * FROM criminal_records";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String fullName = rs.getString("full_name");
                Date dob = rs.getDate("date_of_birth");
                String status = rs.getString("status");
                String crimes = rs.getString("crimes");

                criminalRecords.add(new CriminalRecord(id, fullName, dob, status, crimes));
            }

            criminalTable.setItems(criminalRecords);

        } catch (SQLException e) {
            showAlert("Database Error", "Error loading records: " + e.getMessage());
        }
    }

    @FXML
    public void handleRefresh() {
        loadCriminalRecords();
    }

    @FXML
    public void handleClose() {
        Stage stage = (Stage) criminalTable.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

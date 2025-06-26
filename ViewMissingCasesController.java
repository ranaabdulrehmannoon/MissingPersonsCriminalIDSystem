package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.*;

public class ViewMissingCasesController {

    @FXML
    private TableView<MissingCase> casesTable;

    private ObservableList<MissingCase> casesData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Initialize table with database data
        handleRefresh();
    }

    @FXML
    public void handleRefresh() {
        casesData.clear();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT id, full_name, age, date_missing, city FROM registermissingpersons";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String fullName = rs.getString("full_name");
                int age = rs.getInt("age");
                Date dateMissing = rs.getDate("date_missing");
                String city = rs.getString("city");

                casesData.add(new MissingCase(id, fullName, age, dateMissing, city));
            }

            casesTable.setItems(casesData);

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load cases: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleClose() {
        ((Stage) casesTable.getScene().getWindow()).close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

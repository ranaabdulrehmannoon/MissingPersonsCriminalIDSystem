package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
import java.time.LocalDate;

public class ReportGeneratorController {
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    @FXML private ComboBox<String> reportTypeComboBox;
    @FXML private Button btnGenerate;
    @FXML private Button btnPrint;
    @FXML private TableView<ReportData> reportTable;
    @FXML private TableColumn<ReportData, String> col1;
    @FXML private TableColumn<ReportData, String> col2;
    @FXML private TableColumn<ReportData, String> col3;
    @FXML private TableColumn<ReportData, String> col4;

    @FXML
    public void initialize() {
        // Initialize date pickers
        fromDatePicker.setValue(LocalDate.now().minusMonths(1));
        toDatePicker.setValue(LocalDate.now());

        // Initialize report type combo box
        reportTypeComboBox.getItems().addAll(
            "Missing Persons",
            "Criminal Records",
            "User Activity",
            "Case Status"
        );
        reportTypeComboBox.getSelectionModel().selectFirst();

        // Set up table columns
        col1.setCellValueFactory(new PropertyValueFactory<>("field1"));
        col2.setCellValueFactory(new PropertyValueFactory<>("field2"));
        col3.setCellValueFactory(new PropertyValueFactory<>("field3"));
        col4.setCellValueFactory(new PropertyValueFactory<>("field4"));
    }

    @FXML
    private void handleGenerate() {
        String reportType = reportTypeComboBox.getValue();
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();

        if (fromDate == null || toDate == null) {
            showAlert("Validation Error", "Please select both date ranges");
            return;
        }

        if (fromDate.isAfter(toDate)) {
            showAlert("Validation Error", "From date cannot be after To date");
            return;
        }

        try {
            ObservableList<ReportData> reportData = generateReportData(reportType, fromDate, toDate);
            reportTable.setItems(reportData);
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to generate report: " + e.getMessage());
        }
    }

    private ObservableList<ReportData> generateReportData(String reportType, LocalDate fromDate, LocalDate toDate) throws SQLException {
        ObservableList<ReportData> data = FXCollections.observableArrayList();

        try (Connection conn = DatabaseConnection.getConnection()) {
            switch (reportType) {
                case "Missing Persons":
                    generateMissingPersonsReport(conn, data, fromDate, toDate);
                    break;
                case "Criminal Records":
                    generateCriminalRecordsReport(conn, data, fromDate, toDate);
                    break;
                case "User Activity":
                    generateUserActivityReport(conn, data, fromDate, toDate);
                    break;
                case "Case Status":
                    generateCaseStatusReport(conn, data, fromDate, toDate);
                    break;
            }
        }

        return data;
    }

    private void generateMissingPersonsReport(Connection conn, ObservableList<ReportData> data, LocalDate fromDate, LocalDate toDate) throws SQLException {
        String sql = "SELECT id, full_name, date_missing, city FROM registermissingpersons " +
                     "WHERE date_missing BETWEEN ? AND ? ORDER BY date_missing";
        
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setDate(1, Date.valueOf(fromDate));
        stmt.setDate(2, Date.valueOf(toDate));
        
        ResultSet rs = stmt.executeQuery();
        
        // Configure columns for this report type
        col1.setText("Case ID");
        col2.setText("Name");
        col3.setText("Date Missing");
        col4.setText("City");
        
        while (rs.next()) {
            data.add(new ReportData(
                String.valueOf(rs.getInt("id")),
                rs.getString("full_name"),
                rs.getDate("date_missing").toString(),
                rs.getString("city")
            ));
        }
    }

    private void generateCriminalRecordsReport(Connection conn, ObservableList<ReportData> data, LocalDate fromDate, LocalDate toDate) throws SQLException {
        String sql = "SELECT id, full_name, status, crimes FROM criminal_records " +
                     "WHERE date_of_birth BETWEEN ? AND ? ORDER BY full_name";
        
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setDate(1, Date.valueOf(fromDate));
        stmt.setDate(2, Date.valueOf(toDate));
        
        ResultSet rs = stmt.executeQuery();
        
        // Configure columns for this report type
        col1.setText("Record ID");
        col2.setText("Name");
        col3.setText("Status");
        col4.setText("Crimes");
        
        while (rs.next()) {
            data.add(new ReportData(
                String.valueOf(rs.getInt("id")),
                rs.getString("full_name"),
                rs.getString("status"),
                rs.getString("crimes")
            ));
        }
    }

    private void generateUserActivityReport(Connection conn, ObservableList<ReportData> data, LocalDate fromDate, LocalDate toDate) throws SQLException {
        // This would require a user_activity table in your database
        // Placeholder implementation
        col1.setText("User ID");
        col2.setText("Username");
        col3.setText("Last Login");
        col4.setText("Actions");
        
        data.add(new ReportData("1", "admin", "2023-06-15", "10 actions"));
        data.add(new ReportData("2", "user1", "2023-06-14", "5 actions"));
    }

    private void generateCaseStatusReport(Connection conn, ObservableList<ReportData> data, LocalDate fromDate, LocalDate toDate) throws SQLException {
        // This would require a case_status table in your database
        // Placeholder implementation
        col1.setText("Case ID");
        col2.setText("Status");
        col3.setText("Last Updated");
        col4.setText("Officer");
        
        data.add(new ReportData("101", "Open", "2023-06-10", "Officer Smith"));
        data.add(new ReportData("102", "Closed", "2023-06-05", "Officer Johnson"));
    }

    @FXML
    private void handlePrint() {
        // Implement printing functionality
        showAlert("Print", "Print functionality will be implemented here");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class ReportData {
        private final String field1;
        private final String field2;
        private final String field3;
        private final String field4;

        public ReportData(String field1, String field2, String field3, String field4) {
            this.field1 = field1;
            this.field2 = field2;
            this.field3 = field3;
            this.field4 = field4;
        }

        public String getField1() {
            return field1;
        }

        public String getField2() {
            return field2;
        }

        public String getField3() {
            return field3;
        }

        public String getField4() {
            return field4;
        }
    }
}
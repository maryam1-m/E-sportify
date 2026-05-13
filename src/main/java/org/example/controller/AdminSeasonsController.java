package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class AdminSeasonsController extends AdminDashboardController {

    @FXML
    private Label totalSeasonsLabel;

    @FXML
    private TextField seasonIdField;

    @FXML
    private TextField seasonNameField;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<SeasonAdminRow> seasonsTable;

    @FXML
    private TableColumn<SeasonAdminRow, Integer> seasonIdColumn;

    @FXML
    private TableColumn<SeasonAdminRow, String> seasonNameColumn;

    @FXML
    private TableColumn<SeasonAdminRow, LocalDate> startDateColumn;

    @FXML
    private TableColumn<SeasonAdminRow, LocalDate> endDateColumn;

    @FXML
    private Label messageLabel;

    private final ObservableList<SeasonAdminRow> rows = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        if (seasonIdColumn != null) {
            seasonIdColumn.setCellValueFactory(new PropertyValueFactory<>("seasonId"));
        }
        if (seasonNameColumn != null) {
            seasonNameColumn.setCellValueFactory(new PropertyValueFactory<>("seasonName"));
        }
        if (startDateColumn != null) {
            startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        }
        if (endDateColumn != null) {
            endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        }
        reloadTable();
    }

    private void reloadTable() {
        rows.clear();
        String sql = "SELECT season_id, season_name, start_date, end_date FROM Season ORDER BY start_date";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LocalDate sd = rs.getDate("start_date") != null ? rs.getDate("start_date").toLocalDate() : null;
                LocalDate ed = rs.getDate("end_date") != null ? rs.getDate("end_date").toLocalDate() : null;
                rows.add(new SeasonAdminRow(rs.getInt("season_id"), rs.getString("season_name"), sd, ed));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (seasonsTable != null) {
            seasonsTable.setItems(rows);
        }
        if (totalSeasonsLabel != null) {
            totalSeasonsLabel.setText(String.valueOf(rows.size()));
        }
    }

    public void addSeason(ActionEvent event) {
        reloadTable();
    }

    public void updateSeason(ActionEvent event) {
        reloadTable();
    }

    public void deleteSeason(ActionEvent event) {
        reloadTable();
    }

    public void clearFields(ActionEvent event) {
        if (seasonIdField != null) {
            seasonIdField.clear();
        }
        if (seasonNameField != null) {
            seasonNameField.clear();
        }
        if (startDatePicker != null) {
            startDatePicker.setValue(null);
        }
        if (endDatePicker != null) {
            endDatePicker.setValue(null);
        }
        if (messageLabel != null) {
            messageLabel.setText("");
        }
    }

    public void searchSeasons(ActionEvent event) {
        reloadTable();
    }

    public void loadSeasons(ActionEvent event) {
        reloadTable();
    }

    public static class SeasonAdminRow {
        private final int seasonId;
        private final String seasonName;
        private final LocalDate startDate;
        private final LocalDate endDate;

        public SeasonAdminRow(int seasonId, String seasonName, LocalDate startDate, LocalDate endDate) {
            this.seasonId = seasonId;
            this.seasonName = seasonName;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public int getSeasonId() {
            return seasonId;
        }

        public String getSeasonName() {
            return seasonName;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public LocalDate getEndDate() {
            return endDate;
        }
    }
}

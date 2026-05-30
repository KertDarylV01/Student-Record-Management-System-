package com.example.studentmanagementsystem;

import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;

public class Controller {

    @FXML private TextField txtName;
    @FXML private TextField txtCourse;
    @FXML private ChoiceBox<YearLevel> cbYear;

    @FXML private TableView<Student> table;
    @FXML private TableColumn<Student, Integer> colId;
    @FXML private TableColumn<Student, String> colName;
    @FXML private TableColumn<Student, String> colCourse;
    @FXML private TableColumn<Student, String> colYear;

    private ObservableList<Student> list = FXCollections.observableArrayList();
    private Connection conn;
    private int selectedId = -1;

    @FXML
    public void initialize() {
        conn = DBConnection.connect();

        // Load Enum to ChoiceBox
        cbYear.getItems().setAll(YearLevel.values());

        // Table Columns Binding
        colId.setCellValueFactory(data -> data.getValue().idProperty().asObject());
        colName.setCellValueFactory(data -> data.getValue().nameProperty());
        colCourse.setCellValueFactory(data -> data.getValue().courseProperty());
        colYear.setCellValueFactory(data -> data.getValue().yearLevelProperty());

        loadData();

        // Row click event
        table.setOnMouseClicked(e -> {
            Student s = table.getSelectionModel().getSelectedItem();

            if (s != null) {
                selectedId = s.getId();
                txtName.setText(s.getName());
                txtCourse.setText(s.getCourse());

                try {
                    int year = Integer.parseInt(s.getYearLevel());
                    cbYear.setValue(YearLevel.values()[year - 1]);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void loadData() {
        list.clear();

        try {
            String query = "SELECT * FROM students";
            ResultSet rs = conn.createStatement().executeQuery(query);

            while (rs.next()) {
                list.add(new Student(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("course"),
                        String.valueOf(rs.getInt("year_level"))
                ));
            }

            table.setItems(list);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addStudent() {
        try {
            String query = "INSERT INTO students(name, course, year_level) VALUES (?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(query);

            pst.setString(1, txtName.getText());
            pst.setString(2, txtCourse.getText());
            pst.setInt(3, cbYear.getValue().ordinal() + 1);

            pst.executeUpdate();

            loadData();
            clearFields();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void updateStudent() {
        try {
            String query = "UPDATE students SET name=?, course=?, year_level=? WHERE id=?";
            PreparedStatement pst = conn.prepareStatement(query);

            pst.setString(1, txtName.getText());
            pst.setString(2, txtCourse.getText());
            pst.setInt(3, cbYear.getValue().ordinal() + 1);
            pst.setInt(4, selectedId);

            pst.executeUpdate();

            loadData();
            clearFields();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteStudent() {
        try {
            String query = "DELETE FROM students WHERE id=?";
            PreparedStatement pst = conn.prepareStatement(query);

            pst.setInt(1, selectedId);

            pst.executeUpdate();

            loadData();
            clearFields();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clearFields() {
        txtName.clear();
        txtCourse.clear();
        cbYear.setValue(null);
        selectedId = -1;
    }
}
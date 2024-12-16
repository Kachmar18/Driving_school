package autoschool.autoschool_javafx.Admin;

import autoschool.autoschool_javafx.BasicValues;
import autoschool.autoschool_javafx.DataBase;
import autoschool.autoschool_javafx.LanguageManager;
import autoschool.autoschool_javafx.Localisation;
import com.jfoenix.controls.JFXButton;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.*;
import java.util.Objects;

public class AdminPractice implements Localisation {
    public Label practiceLabel;
    public TextField keyWordTextField;
    public JFXButton createNewPracticeLessonButton;
    public TableView<FullPracticeData> schedulePracticeTableView;
    public TableColumn<FullPracticeData, String> dayPracticeColumn, hourPracticeColumn, routePracticeColumn, durationPracticeColumn, statusPracticeColumn, instructorPracticeColumn, studentPracticeColumn, operationsPracticeColumn;
    private AdminMenuWindow adminMenuWindow;


    public void setAdminMenuWindow(AdminMenuWindow adminMenuWindow) {
        this.adminMenuWindow = adminMenuWindow;
    }
    public void setCurrentLanguage() {
        translate();
    }
    public void translate() {
        practiceLabel.setText(LanguageManager.getString("practiceLabel"));
        createNewPracticeLessonButton.setText(LanguageManager.getString("createNewPracticeLessonButton"));

        dayPracticeColumn.setText(LanguageManager.getString("dayColumn"));
        hourPracticeColumn.setText(LanguageManager.getString("hourColumn"));
        routePracticeColumn.setText(LanguageManager.getString("routeColumn"));
        durationPracticeColumn.setText(LanguageManager.getString("durationPracticeColumn"));
        statusPracticeColumn.setText(LanguageManager.getString("statusColumn"));
        studentPracticeColumn.setText(LanguageManager.getString("studentRoleLabel"));
        instructorPracticeColumn.setText(LanguageManager.getString("instructorRoleLabel"));

        keyWordTextField.setPromptText(LanguageManager.getString("keyWordPrompt"));
    }

    public void initialize() {
        loadData();

        keyWordTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            loadData(); // Reload data on text change
        });

        operationsPracticeColumn.setCellFactory(createOperationsCellFactory());
    }

    public void loadData() {
        try {
            Connection connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD);
            if (!DataBase.isDatabaseConnected()) { // Check if the connection to the database is successful
                BasicValues.showErrorAlert(LanguageManager.getString("dbConnectionError"));
                return;
            }
            String sql = """
                    SELECT Практичні_заняття.id_практика,
                    Практичні_заняття.Дата,
                    Практичні_заняття.Час,
                    Практичні_заняття.Маршрут,
                    Практичні_заняття.`Тривалість, год`,
                    Практичні_заняття.Статус,
                    Інструктор.`Ім'я` AS `Ім'я інструктора`,
                    Інструктор.Прізвище AS `Прізвище інструктора`,
                    Студент.`Ім'я` AS `Ім'я студента`,
                    Студент.Прізвище AS `Прізвище студента`
                    FROM Практичні_заняття
                    JOIN Студент ON Практичні_заняття.id_студент = Студент.id_студент
                    JOIN Інструктор ON Практичні_заняття.id_інструктор = Інструктор.Id_інструктор""";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            ObservableList<FullPracticeData> data = FXCollections.observableArrayList();

            while (resultSet.next()) {
                FullPracticeData practice = new FullPracticeData( // Create CourseData objects and add them to the ObservableList
                        resultSet.getString("id_практика"),
                        resultSet.getString("Дата"),
                        resultSet.getString("Час"),
                        resultSet.getString("Маршрут"),
                        resultSet.getString("Тривалість, год"),
                        resultSet.getString("Статус"),
                        resultSet.getString("Ім'я інструктора") + " " + resultSet.getString("Прізвище інструктора"),
                        resultSet.getString("Ім'я студента") + " " + resultSet.getString("Прізвище студента")
                );
                data.add(practice);
            }
            dayPracticeColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
            hourPracticeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
            routePracticeColumn.setCellValueFactory(new PropertyValueFactory<>("route"));
            durationPracticeColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));
            statusPracticeColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
            instructorPracticeColumn.setCellValueFactory(new PropertyValueFactory<>("instructor"));
            studentPracticeColumn.setCellValueFactory(new PropertyValueFactory<>("student"));

            String keyword = keyWordTextField.getText().toLowerCase(); // Get the keyword from the TextField
            ObservableList<FullPracticeData> filteredData = data.filtered(practice ->
                    practice.getDate().toLowerCase().contains(keyword) ||
                            practice.getTime().toLowerCase().contains(keyword) ||
                            practice.getRoute().toLowerCase().contains(keyword) ||
                            practice.getDuration().toLowerCase().contains(keyword) ||
                            practice.getStatus().toLowerCase().contains(keyword) ||
                            practice.getInstructor().toLowerCase().contains(keyword) ||
                            practice.getStudent().toLowerCase().contains(keyword)
            );

            schedulePracticeTableView.setItems(data); // Set the data to the TableView
            schedulePracticeTableView.setItems(filteredData);

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static class FullPracticeData {
        private final SimpleStringProperty id_practice;
        private final SimpleStringProperty date, time, route, duration, status, instructor, student;
        public FullPracticeData(String id_practice, String date, String time, String route, String duration, String status, String instructor, String student) {
            this.id_practice = new SimpleStringProperty(id_practice);
            this.date = new SimpleStringProperty(date);
            this.time = new SimpleStringProperty(time);
            this.route = new SimpleStringProperty(route);
            this.duration = new SimpleStringProperty(duration);
            this.status = new SimpleStringProperty(status);
            this.instructor = new SimpleStringProperty(instructor);
            this.student = new SimpleStringProperty(student);
        }
        public String getId_practice() {
            return id_practice.get();
        }
        public String getDate() {
            return date.get();
        }
        public String getTime() {
            return time.get();
        }
        public String getRoute() {
            return route.get();
        }
        public String getDuration() {
            return duration.get();
        }
        public String getStatus() {
            return status.get();
        }
        public String getInstructor() {
            return instructor.get();
        }
        public String getStudent() {
            return student.get();
        }
    }

    public Callback<TableColumn<FullPracticeData, String>, TableCell<FullPracticeData, String>> createOperationsCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<FullPracticeData, String> call(TableColumn<FullPracticeData, String> param) {
                return new TableCell<>() {
                    final ImageView editIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(BasicValues.EDIT_RECORD_ICON))));
                    final ImageView doneIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(BasicValues.DONE_RECORD_ICON))));
                    final ImageView deleteIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(BasicValues.DELETE_RECORD_ICON))));
                    {
                        editIcon.setFitWidth(27);
                        editIcon.setFitHeight(27);

                        doneIcon.setFitWidth(27);
                        doneIcon.setFitHeight(27);

                        deleteIcon.setFitWidth(27);
                        deleteIcon.setFitHeight(27);
                    }
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox iconsContainer = new HBox(10);
                            iconsContainer.getChildren().addAll(editIcon, doneIcon, deleteIcon);

                            editIcon.setOnMouseClicked(event -> {
                                try {
                                    handleEditAction(getTableRow().getIndex());
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }

                            });

                            doneIcon.setOnMouseClicked(event -> handleDoneAction(getTableRow().getIndex()));
                            deleteIcon.setOnMouseClicked(event -> handleDeleteAction(getTableRow().getIndex()));

                            setGraphic(iconsContainer);
                        }
                    }
                };
            }
        };
    }

    private void handleEditAction(int rowIndex) throws IOException {
        System.out.println("Edit action for row " + rowIndex); // Perform edit action for the specified row index

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/autoschool/autoschool_javafx/admin/admin-new-practice.fxml"));
        Parent fxml = loader.load();

        AdminNewPractice adminNewPractice = loader.getController();
        adminNewPractice.setCurrentLanguage();
        adminNewPractice.setAdminMenuWindow(adminMenuWindow);

        adminNewPractice.statusLabel.setVisible(true);
        adminNewPractice.statusComboBox.setVisible(true);
        adminNewPractice.createNewPracticeTitleText.setText(LanguageManager.getString("updatePracticeTitleText"));

        FullPracticeData selectedPractice = schedulePracticeTableView.getItems().get(rowIndex);
        adminNewPractice.setEditPracticeData(selectedPractice, selectedPractice.getId_practice());

        adminMenuWindow.contentArea.getChildren().removeAll();
        adminMenuWindow.contentArea.getChildren().setAll(fxml);
    }

    private void handleDoneAction(int rowIndex) {
        try {
            FullPracticeData selectedPractice = schedulePracticeTableView.getItems().get(rowIndex);
            String practiceId = selectedPractice.getId_practice();

            if (selectedPractice.getStatus().equals("Завершено")) { // Add a check to see if the practice is already completed
                BasicValues.showInformationDialog(LanguageManager.getString("practiceAlreadyCompletedInformation"));
            } else { // Practice is not completed, proceed with the update
                if (BasicValues.showConfirmationDialog(LanguageManager.getString("donePracticeConfirmation"))) {
                    Connection connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD);

                    String updateSql = "UPDATE `практичні_заняття` SET `Статус` = 'Завершено' WHERE `практичні_заняття`.`id_практика` = ?";
                    try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
                        updateStatement.setString(1, practiceId);
                        updateStatement.executeUpdate();
                    }
                    connection.close();

                    BasicValues.showInformationDialog(LanguageManager.getString("donePracticeSuccess"));
                    System.out.println("Updated status to 'Завершено' for practice with ID: " + practiceId);

                    loadData();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void handleDeleteAction(int rowIndex) {
        try {
            FullPracticeData selectedPractice = schedulePracticeTableView.getItems().get(rowIndex);
            String practiceId = selectedPractice.getId_practice();

            if (BasicValues.showConfirmationDialog(LanguageManager.getString("deletePracticeConfirmation"))) {
                Connection connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD);

                String updateSql = "DELETE FROM `практичні_заняття` WHERE `id_практика` = ?";
                try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
                    updateStatement.setString(1, practiceId);
                    updateStatement.executeUpdate();
                }
                connection.close();

                BasicValues.showInformationDialog(LanguageManager.getString("deletePracticeSuccess"));
                System.out.println("Delete practice with ID: " + selectedPractice);

                loadData();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            BasicValues.showInformationDialog(LanguageManager.getString("deletePracticeRecordFailedError"));
        }
    }

    public void createNewPracticeLessonButton() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/autoschool/autoschool_javafx/admin/admin-new-practice.fxml"));
        Parent fxml = loader.load();

        AdminNewPractice adminNewPractice = loader.getController();
        adminNewPractice.setCurrentLanguage();
        adminNewPractice.setAdminMenuWindow(adminMenuWindow);

        adminMenuWindow.contentArea.getChildren().removeAll();
        adminMenuWindow.contentArea.getChildren().setAll(fxml);
    }
}

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

public class AdminCourses implements Localisation {
    public Label theoryLabel;
    public TextField keyWordTextField;
    public JFXButton createNewCourseButton;
    public TableView<FullInfoCourse> coursesTableView;
    public TableColumn<FullInfoCourse, String> nameColumn, startColumn, endColumn, statusColumn, categoryColumn, addressColumn, scheduleColumn, studentsColumn, instructorColumn, operateCourseColumn;

    private AdminMenuWindow adminMenuWindow;

    public void setAdminMenuWindow(AdminMenuWindow adminMenuWindow) {
        this.adminMenuWindow = adminMenuWindow;
    }
    public void setCurrentLanguage() {
        translate();
    }
    public void translate() {
        theoryLabel.setText((LanguageManager.getString("theoryLabel")));
        keyWordTextField.setPromptText(LanguageManager.getString("keyWordPrompt"));
        createNewCourseButton.setText(LanguageManager.getString("createNewCourseButton"));

        nameColumn.setText(LanguageManager.getString("nameColumn"));
        startColumn.setText(LanguageManager.getString("startColumn"));
        endColumn.setText(LanguageManager.getString("endColumn"));
        statusColumn.setText(LanguageManager.getString("statusColumn"));
        categoryColumn.setText(LanguageManager.getString("categoryColumn"));
        addressColumn.setText(LanguageManager.getString("addressColumn"));
        scheduleColumn.setText(LanguageManager.getString("timelineColumn"));
        instructorColumn.setText(LanguageManager.getString("instructorRoleLabel"));
        studentsColumn.setText(LanguageManager.getString("amountTheoryColumn"));
    }

    public void initialize() {
        loadInfoCourse();

        keyWordTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            loadInfoCourse(); // Reload data on text change
        });

        operateCourseColumn.setCellFactory(createOperationsCellFactory());
    }

    private void loadInfoCourse() {
        try {
            Connection connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD);
            if (!DataBase.isDatabaseConnected()) { // Check if the connection to the database is successful
                BasicValues.showErrorAlert(LanguageManager.getString("dbConnectionError"));
                return;
            }

            String sql = """
                    SELECT
                       Курси.id_курси,
                       Курси.Назва,
                       Курси.Початок,
                       Курси.Кінець,
                       Курси.Статус,
                       Курси.Категорія,
                       Локація.Місто,
                       Локація.Вулиця,
                       Локація.Будинок,
                       Розклад.День,
                       Розклад.Година,
                       Інструктор.`Ім'я`,
                       Інструктор.`Прізвище`,
                       COUNT(DISTINCT Студент.id_студент) + COUNT(DISTINCT Студент_додатковий.id_студент) AS "Кількість студентів"
                    FROM
                        Курси
                    LEFT JOIN
                        Інструктор ON Курси.`id_інструктор` = Інструктор.`Id_інструктор`
                     LEFT JOIN
                        Локація ON Курси.id_локація = Локація.id_локація
                    LEFT JOIN
                        Розклад ON Курси.id_розклад = Розклад.id_розклад
                    LEFT JOIN
                         Студент ON Курси.id_курси = Студент.id_курси
                    LEFT JOIN
                         Студент AS Студент_додатковий ON Курси.id_курси = Студент_додатковий.додатковий_курс
                    GROUP BY
                       Курси.id_курси,
                       Курси.Назва,
                       Курси.Початок,
                       Курси.Кінець,
                       Курси.Статус,
                       Курси.Категорія,
                       Локація.Місто,
                       Локація.Вулиця,
                       Локація.Будинок,
                       Розклад.День,
                       Розклад.Година,
                       Інструктор.`Ім'я`,
                       Інструктор.`Прізвище`;
                    """;

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            ObservableList<FullInfoCourse> data = FXCollections.observableArrayList();

            while (resultSet.next()) {
                FullInfoCourse infoCourse = new FullInfoCourse(
                        resultSet.getString("id_курси"),
                        resultSet.getString("Назва"),
                        resultSet.getString("Початок"),
                        resultSet.getString("Кінець"),
                        resultSet.getString("Статус"),
                        resultSet.getString("Категорія"),
                        resultSet.getString("День") + " " + resultSet.getString("Година"),
                        resultSet.getString("Місто"),
                        resultSet.getString("Вулиця"),
                        resultSet.getString("Будинок"),
                        resultSet.getString("Ім'я") + " " + resultSet.getString("Прізвище"),
                        resultSet.getString("Кількість студентів")
                );
                data.add(infoCourse);
            }

            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name")); // Set the cell value factories for each TableColumn
            startColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
            endColumn.setCellValueFactory(new PropertyValueFactory<>("end"));
            statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
            categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
            addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
            scheduleColumn.setCellValueFactory(new PropertyValueFactory<>("schedule"));
            instructorColumn.setCellValueFactory(new PropertyValueFactory<>("instructor"));
            studentsColumn.setCellValueFactory(new PropertyValueFactory<>("students"));

            String keyword = keyWordTextField.getText().toLowerCase(); // Get the keyword from the TextField
            ObservableList<FullInfoCourse> filteredData = data.filtered(course ->
                    course.getName().toLowerCase().contains(keyword) ||
                    course.getStart().toLowerCase().contains(keyword) ||
                    course.getEnd().toLowerCase().contains(keyword) ||
                    course.getStatus().toLowerCase().contains(keyword) ||
                    course.getCategory().toLowerCase().contains(keyword) ||
                    course.getAddress().toLowerCase().contains(keyword) ||
                    course.getSchedule().toLowerCase().contains(keyword) ||
                    course.getInstructor().toLowerCase().contains(keyword)
            );

            coursesTableView.setItems(data); // Set the data to the TableView
            coursesTableView.setItems(filteredData);

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static class FullInfoCourse {
        private final SimpleStringProperty id_course;
        private final SimpleStringProperty name;
        private final SimpleStringProperty start, end;
        private final SimpleStringProperty status, category;
        private final SimpleStringProperty schedule, address;
        private final SimpleStringProperty instructor, students;

        public FullInfoCourse(String id_course, String name, String start, String end, String status, String category, String schedule, String city, String street, String house, String instructor, String students) {
            this.id_course = new SimpleStringProperty(id_course);
            this.name = new SimpleStringProperty(name);
            this.start = new SimpleStringProperty(start);
            this.end = new SimpleStringProperty(end);
            this.status = new SimpleStringProperty(status);
            this.category = new SimpleStringProperty(category);
            this.schedule = new SimpleStringProperty(schedule);
            this.address = new SimpleStringProperty(city + ", " + LanguageManager.getString("streetShortForm") + street + ", " + LanguageManager.getString("buildingShortForm")+ house);
            this.instructor = new SimpleStringProperty(instructor);
            this.students = new SimpleStringProperty(students);
        }
        public String getId_course() {
            return id_course.get();
        }
        public String getName() {
            return name.get();
        }
        public String getStart() {
            return start.get();
        }
        public String getEnd() {
            return end.get();
        }
        public String getStatus() {
            return status.get();
        }
        public String getCategory() {
            return category.get();
        }
        public String getSchedule() {
            return schedule.get();
        }
        public String getAddress() {
            return address.get();
        }
        public String getInstructor() {
            return instructor.get();
        }
        public String getStudents() {
            return students.get();
        }
    }

    public Callback<TableColumn<FullInfoCourse, String>, TableCell<FullInfoCourse, String>> createOperationsCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<FullInfoCourse, String> call(TableColumn<FullInfoCourse, String> param) {
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

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/autoschool/autoschool_javafx/admin/admin-new-course.fxml"));
        Parent fxml = loader.load();

        AdminNewCourse adminNewCourse = loader.getController();
        adminNewCourse.setCurrentLanguage();
        adminNewCourse.setAdminMenuWindow(adminMenuWindow);

        adminNewCourse.statusLabel.setVisible(true);
        adminNewCourse.statusComboBox.setVisible(true);
        adminNewCourse.createNewCourseTitleText.setText(LanguageManager.getString("updateTheoryTitleText"));

        FullInfoCourse selectedCourse = coursesTableView.getItems().get(rowIndex);
        adminNewCourse.setEditCourseData(selectedCourse, selectedCourse.getId_course());

        adminMenuWindow.contentArea.getChildren().removeAll();
        adminMenuWindow.contentArea.getChildren().setAll(fxml);
    }

    private void handleDoneAction(int rowIndex) {
        try {
            FullInfoCourse selectedCourse = coursesTableView.getItems().get(rowIndex);
            String courseId = selectedCourse.getId_course();

            if (selectedCourse.getStatus().equals("Завершено")) { // Add a check to see if the practice is already completed
                BasicValues.showInformationDialog(LanguageManager.getString("courseAlreadyCompletedInformation"));
            } else { // Practice is not completed, proceed with the update
                if (BasicValues.showConfirmationDialog(LanguageManager.getString("doneCourseConfirmation"))) {
                    Connection connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD);

                    String updateSql = "UPDATE `Курси` SET `Статус` = 'Завершено' WHERE `id_курси` = ?";
                    try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
                        updateStatement.setString(1, courseId);
                        updateStatement.executeUpdate();
                    }
                    connection.close();

                    BasicValues.showInformationDialog(LanguageManager.getString("doneCourseSuccess"));
                    System.out.println("Updated status to 'Завершено' for course with ID: " + courseId);

                    loadInfoCourse();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            BasicValues.showInformationDialog(LanguageManager.getString("deleteCourseRecordFailedError"));
        }
    }

    private void handleDeleteAction(int rowIndex) {
        try {
            FullInfoCourse selectedCourse = coursesTableView.getItems().get(rowIndex);
            String courseId = selectedCourse.getId_course();

            if (BasicValues.showConfirmationDialog(LanguageManager.getString("deleteCourseConfirmation"))) {
                Connection connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD);

                String updateSql = "DELETE FROM `курси` WHERE `id_курси` = ?";
                try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
                    updateStatement.setString(1, courseId);
                    updateStatement.executeUpdate();
                }
                connection.close();

                BasicValues.showInformationDialog(LanguageManager.getString("deleteCourseSuccess"));
                System.out.println("Updated status to 'Завершено' for practice with ID: " + courseId);

                loadInfoCourse();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createNewCourseButton() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/autoschool/autoschool_javafx/admin/admin-new-course.fxml"));
        Parent fxml = loader.load();

        AdminNewCourse adminNewCourse = loader.getController();
        adminNewCourse.setCurrentLanguage();
        adminNewCourse.setAdminMenuWindow(adminMenuWindow);

        adminMenuWindow.contentArea.getChildren().removeAll();
        adminMenuWindow.contentArea.getChildren().setAll(fxml);
    }
}

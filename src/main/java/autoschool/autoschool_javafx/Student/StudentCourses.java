package autoschool.autoschool_javafx.Student;

import autoschool.autoschool_javafx.BasicValues;
import autoschool.autoschool_javafx.DataBase;
import autoschool.autoschool_javafx.LanguageManager;
import autoschool.autoschool_javafx.Localisation;
import com.jfoenix.controls.JFXButton;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.sql.*;
import java.util.Objects;
import java.util.Optional;

public class StudentCourses implements Localisation {
    public TableView<CourseData> coursesTableView;
    public TableColumn<CourseData, String> nameColumn, startColumn, endColumn, categoryColumn, cityColumn, streetColumn, houseColumn;
    public Label chooseCourseLabel, mainCourseLabel, minorCourseLabel;
    public TextField keyWordTextField;
    public TableColumn<CourseData, Void> selectCourseColumn;

    public void setStudentMenuWindow(StudentMenuWindow studentMenuWindow) {
    }
    public void setCurrentLanguage() {
        translate();
    }
    public void translate() {
        chooseCourseLabel.setText(LanguageManager.getString("chooseCourseLabel"));
        keyWordTextField.setPromptText(LanguageManager.getString("keyWordPrompt"));
        nameColumn.setText(LanguageManager.getString("nameColumn"));
        startColumn.setText(LanguageManager.getString("startColumn"));
        endColumn.setText(LanguageManager.getString("endColumn"));
        categoryColumn.setText(LanguageManager.getString("categoryColumn"));
        cityColumn.setText(LanguageManager.getString("cityColumn"));
        streetColumn.setText(LanguageManager.getString("streetColumn"));
        houseColumn.setText(LanguageManager.getString("houseColumn"));
    }

    public void initialize() {
        loadData(); // Call the method to load data into the table
        loadUserCourses();

        keyWordTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            loadData(); // Reload data on text change
        });

        selectCourseColumn.setCellFactory(col -> new TableCell<CourseData, Void>() {
            private final JFXButton selectCourse = new JFXButton(LanguageManager.getString("signUpCourseTableCell"));

            {
                ImageView imageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(BasicValues.LeftICON))));
                imageView.setFitWidth(22);
                imageView.setFitHeight(22);
                selectCourse.setGraphic(imageView);

                selectCourse.setOnAction(event -> {
                    CourseData courseData = getTableView().getItems().get(getIndex());
                    handleEnrollment(courseData);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(selectCourse);
                }
            }
        });
    }

    private void handleEnrollment(CourseData courseData) {
        try (Connection connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD)) {
            if (!DataBase.isDatabaseConnected()) {
                BasicValues.showErrorAlert(LanguageManager.getString("dbConnectionError"));
                return;
            }

            String checkQuery = "SELECT `id_курси`, `додатковий_курс` FROM `Студент` WHERE `id_студент` = ?";
            try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
                checkStatement.setInt(1, Integer.parseInt(String.valueOf(StudentMenuWindow.id_student)));

                ResultSet checkResult = checkStatement.executeQuery();

                if (checkResult.next()) {
                    String idCourseValue = checkResult.getString("id_курси");
                    String additionalCourseValue = checkResult.getString("додатковий_курс");

                    if (idCourseValue == null || idCourseValue.isEmpty()) { // id_курси column is empty, update it
                        updateCourse(StudentMenuWindow.id_student, courseData.getId_course(), "id_курси", LanguageManager.getString("registrationMainCourseSuccess") +" "+ courseData.getName());
                        loadUserCourses();
                    } else if (additionalCourseValue == null || additionalCourseValue.isEmpty()) {
                        // id_курси column is filled, but додатковий_курс is empty, update додатковий_курс
                        if (!idCourseValue.equals(courseData.getId_course())) {
                            updateCourse(StudentMenuWindow.id_student, courseData.getId_course(), "додатковий_курс", LanguageManager.getString("registrationAdditionalCourseSuccess") +" "+ courseData.getName());
                            loadUserCourses();
                        } else {
                            BasicValues.showInformationDialog(LanguageManager.getString("registrationCourseDuplicate"));
                        }
                    } else { // Both columns are filled, prompt the user to choose
                        if (!idCourseValue.equals(courseData.getId_course()) && !additionalCourseValue.equals(courseData.getId_course())) {
                            boolean chooseIdCourse = showCourseConfirmationDialog(LanguageManager.getString("chooseCellCourseConfirmation"));
                            if (chooseIdCourse) { // Update id_курси
                                updateCourse(StudentMenuWindow.id_student, courseData.getId_course(), "id_курси", LanguageManager.getString("registrationMainCourseSuccess") +" "+ courseData.getName());
                                loadUserCourses();
                            } else { // Update додатковий_курс
                                updateCourse(StudentMenuWindow.id_student, courseData.getId_course(), "додатковий_курс", LanguageManager.getString("registrationAdditionalCourseSuccess") +" "+  courseData.getName());
                                loadUserCourses();
                            }
                        } else {
                            BasicValues.showInformationDialog(LanguageManager.getString("registrationCourseDuplicate"));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateCourse(int studentId, String courseId, String columnName, String successMessage) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD)) {
            String updateQuery = "UPDATE `Студент` SET `" + columnName + "` = ? WHERE `id_студент` = ?";
            try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                updateStatement.setString(1, courseId);
                updateStatement.setInt(2, studentId);

                int affectedRows = updateStatement.executeUpdate();

                if (affectedRows > 0) {
                    BasicValues.showInformationDialog(successMessage);
                    System.out.println(courseId);
                } else {
                    BasicValues.showInformationDialog(LanguageManager.getString("registrationFailedError"));
                }
            }
        }
    }

    private void loadUserCourses() {
        try (Connection connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD)) {
            if (!DataBase.isDatabaseConnected()) {
                BasicValues.showErrorAlert(LanguageManager.getString("dbConnectionError"));
                return;
            }

            String query = "SELECT `id_курси`, `додатковий_курс` FROM `Студент` WHERE `id_студент` = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, Integer.parseInt(String.valueOf(StudentMenuWindow.id_student)));
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    String mainCourseId = resultSet.getString("id_курси");
                    String minorCourseId = resultSet.getString("додатковий_курс");

                    if (mainCourseId != null && !mainCourseId.isEmpty()) {
                        String mainCourseName = getCourseNameById(mainCourseId);
                        setLabelVisible(mainCourseLabel, true);
                        mainCourseLabel.setText(LanguageManager.getString("mainCourseLabel") + ": " + mainCourseName);
                    } else {
                        setLabelVisible(mainCourseLabel, false);
                    }

                    if (minorCourseId != null && !minorCourseId.isEmpty()) {
                        String minorCourseName = getCourseNameById(minorCourseId);
                        setLabelVisible(minorCourseLabel, true);
                        minorCourseLabel.setText(LanguageManager.getString("minorCourseLabel") + ": " + minorCourseName);
                    } else {
                        setLabelVisible(minorCourseLabel, false);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setLabelVisible(Label label, boolean isVisible) {
        label.setVisible(isVisible);
        label.setManaged(isVisible);
    }

    private String getCourseNameById(String courseId) {
        String courseName = "";

        try (Connection connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD)) {
            if (!DataBase.isDatabaseConnected()) {
                BasicValues.showErrorAlert(LanguageManager.getString("dbConnectionError"));
                return courseName;
            }

            String query = "SELECT `Назва` FROM `Курси` WHERE `id_курси` = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, courseId);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    courseName = resultSet.getString("Назва");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return courseName;
    }

    private boolean showCourseConfirmationDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(LanguageManager.getString("confirmationDialog"));
        alert.setHeaderText(message);

        ButtonType buttonTypeYes = new ButtonType(LanguageManager.getString("buttonTypeYesConfirmation"));
        ButtonType buttonTypeNo = new ButtonType(LanguageManager.getString("buttonTypeNoConfirmation"), ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == buttonTypeYes;
    }

    public void loadData() {
        try {
            Connection connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD);
            if (!DataBase.isDatabaseConnected()) { // Check if the connection to the database is successful
                BasicValues.showErrorAlert(LanguageManager.getString("dbConnectionError"));
                return;
            }

            StringBuilder sql = new StringBuilder("SELECT Курси.id_курси, Курси.Назва, Курси.Початок, Курси.Кінець, Курси.Категорія, Локація.Місто, Локація.Вулиця, Локація.Будинок FROM Курси JOIN Локація ON Курси.id_локація = Локація.id_локація JOIN Студент ON Курси.Категорія = Студент.Категорія AND Студент.id_студент = ?");

            if (!keyWordTextField.getText().isEmpty()) { // Append WHERE clause if a keyword is entered
                sql.append(" WHERE ");
                sql.append("CONCAT(Курси.Назва, ' ', Курси.Початок, ' ', Курси.Кінець, ' ', Курси.Категорія, ' ', Локація.Місто, ' ', Локація.Вулиця, ' ', Локація.Будинок) LIKE '%").append(keyWordTextField.getText()).append("%'");
            }

            PreparedStatement preparedStatement = connection.prepareStatement(sql.toString());
            preparedStatement.setString(1, String.valueOf(StudentMenuWindow.id_student));
            ResultSet resultSet = preparedStatement.executeQuery();

            ObservableList<CourseData> data = FXCollections.observableArrayList();

            while (resultSet.next()) {
                CourseData course = new CourseData( // Create CourseData objects and add them to the ObservableList
                        resultSet.getString("id_курси"),
                        resultSet.getString("Назва"),
                        resultSet.getString("Початок"),
                        resultSet.getString("Кінець"),
                        resultSet.getString("Категорія"),
                        resultSet.getString("Місто"),
                        resultSet.getString("Вулиця"),
                        resultSet.getString("Будинок")
                );
                data.add(course);
            }

            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name")); // Set the cell value factories for each TableColumn
            startColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
            endColumn.setCellValueFactory(new PropertyValueFactory<>("end"));
            categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
            cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
            streetColumn.setCellValueFactory(new PropertyValueFactory<>("street"));
            houseColumn.setCellValueFactory(new PropertyValueFactory<>("house"));

            coursesTableView.setItems(data); // Set the data to the TableView

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static class CourseData {
        private final SimpleStringProperty id_course;
        private final SimpleStringProperty name, start, end, category;
        private final SimpleStringProperty city,  street, house;

        public CourseData(String id_course, String name, String start, String end, String category, String city, String street, String house) {
            this.id_course = new SimpleStringProperty(id_course);
            this.name = new SimpleStringProperty(name);
            this.start = new SimpleStringProperty(start);
            this.end = new SimpleStringProperty(end);
            this.category = new SimpleStringProperty(category);
            this.city = new SimpleStringProperty(city);
            this.street = new SimpleStringProperty(street);
            this.house = new SimpleStringProperty(house);
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

        public String getCategory() {
            return category.get();
        }

        public String getCity() {
            return city.get();
        }

        public String getStreet() {
            return street.get();
        }

        public String getHouse() {
            return house.get();
        }
    }

}

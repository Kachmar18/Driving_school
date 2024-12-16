package autoschool.autoschool_javafx.Admin;

import autoschool.autoschool_javafx.*;
import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AdminNewCourse implements Localisation {
    public Text createNewCourseTitleText;
    public Label nameLabel, startEndDateTimeLabel, categoryLabel, locationLabel,scheduleLabel, instructorLabel, statusLabel, errorTipLabel;
    public TextField nameTextField;
    public DatePicker startDatePicker, endDatePicker;
    public ComboBox<String> categoryComboBox, locationComboBox, scheduleComboBox, instructorComboBox, statusComboBox;
    public JFXButton returnButton, createCourseButton;

    private AdminMenuWindow adminMenuWindow;
    private LocalDate startDate, endDate;

    public void setAdminMenuWindow(AdminMenuWindow adminMenuWindow) {
        this.adminMenuWindow = adminMenuWindow;
    }
    public void setCurrentLanguage() {
        translate();
    }
    public void translate() {
        createNewCourseTitleText.setText(LanguageManager.getString("createNewCourseTitleText"));
        nameLabel.setText(LanguageManager.getString("nameColumn"));
        startEndDateTimeLabel.setText(LanguageManager.getString("startEndDateTimeLabel"));
        categoryLabel.setText(LanguageManager.getString("categoryColumn"));
        statusLabel.setText(LanguageManager.getString("statusColumn"));
        locationLabel.setText(LanguageManager.getString("locationLabel"));
        scheduleLabel.setText(LanguageManager.getString("timelineColumn"));
        instructorLabel.setText(LanguageManager.getString("instructorRoleLabel"));
        errorTipLabel.setText(LanguageManager.getString("errorTipLabel"));
        returnButton.setText(LanguageManager.getString("returnButton"));
        createCourseButton.setText(LanguageManager.getString("editAccount"));

        nameTextField.setPromptText(LanguageManager.getString("nameTextFieldPrompt"));
        startDatePicker.setPromptText(LanguageManager.getString("startDatePickerPrompt"));
        endDatePicker.setPromptText(LanguageManager.getString("endDatePickerPrompt"));
        categoryComboBox.setPromptText(LanguageManager.getString("categoryComboBoxPrompt2"));
        statusComboBox.setPromptText(LanguageManager.getString("statusComboBoxPrompt"));
        locationComboBox.setPromptText(LanguageManager.getString("locationComboBoxPrompt"));
        scheduleComboBox.setPromptText(LanguageManager.getString("scheduleComboBoxPrompt"));
        instructorComboBox.setPromptText(LanguageManager.getString("instructorComboBoxPrompt"));
    }

    public void initialize() {
        startDatePicker.setDayCellFactory(getDayCellFactory());
        endDatePicker.setDayCellFactory(getDayCellFactory());

        CommonMethods.categoryComboBoxList(categoryComboBox);
        CommonMethods.statusComboBoxList(statusComboBox);

        populateLocationComboBox(locationComboBox);
        locationComboBox.getItems().add(LanguageManager.getString("changeInfoLocationText"));
        locationComboBox.setOnAction(event -> {
            String selectedLocation = locationComboBox.getSelectionModel().getSelectedItem();
            if (selectedLocation.equals(LanguageManager.getString("changeInfoLocationText"))) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/autoschool/autoschool_javafx/admin/edit-location.fxml"));
                    Parent fxml = loader.load();

                    AdminEditLocation adminEditLocation  = loader.getController();
                    adminEditLocation.setAdminMenuWindow(adminMenuWindow);
                    adminEditLocation.setCurrentLanguage();

                    adminMenuWindow.contentArea.getChildren().removeAll();
                    adminMenuWindow.contentArea.getChildren().setAll(fxml);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        populateScheduleComboBox(scheduleComboBox);
        scheduleComboBox.getItems().add(LanguageManager.getString("changeInfoScheduleText"));
        scheduleComboBox.setOnAction(event -> {
            String selectedSchedule = scheduleComboBox.getSelectionModel().getSelectedItem();
            if (selectedSchedule.equals(LanguageManager.getString("changeInfoScheduleText"))) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/autoschool/autoschool_javafx/admin/edit-schedule.fxml"));
                    Parent fxml = loader.load();

                    AdminEditSchedule adminEditSchedule   = loader.getController();
                    adminEditSchedule.setAdminMenuWindow(adminMenuWindow);
                    adminEditSchedule.setCurrentLanguage();

                    adminMenuWindow.contentArea.getChildren().removeAll();
                    adminMenuWindow.contentArea.getChildren().setAll(fxml);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        populateInstructorComboBox();
    }

    public static void populateLocationComboBox(ComboBox<String> comboBox) {
        ObservableList<String> locationList = FXCollections.observableArrayList();

        try (Connection connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT `id_локація`, `Місто`, `Вулиця`, `Будинок` FROM `локація`");
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                String locationFullData = resultSet.getString("Місто") + ", " + LanguageManager.getString("streetShortForm") +
                        resultSet.getString("Вулиця") + ", " + LanguageManager.getString("buildingShortForm") + resultSet.getString("Будинок");

                locationList.add(locationFullData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        comboBox.setItems(locationList);
    }

    public static void populateScheduleComboBox(ComboBox<String> comboBox) {
        ObservableList<String> scheduleList = FXCollections.observableArrayList();

        try (Connection connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT `id_розклад`, `День`, `Година` FROM `розклад`");
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                String scheduleFullData = resultSet.getString("День") + " " + resultSet.getString("Година");
                scheduleList.add(scheduleFullData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        comboBox.setItems(scheduleList);
    }

    private void populateInstructorComboBox() {
        ObservableList<String> instructorList = FXCollections.observableArrayList();

        try (Connection connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT `Id_інструктор`, `Ім'я`, `Прізвище` FROM `інструктор`");
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                String instructorFullName = resultSet.getString("Ім'я") + " " + resultSet.getString("Прізвище");
                instructorList.add(instructorFullName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        instructorComboBox.setItems(instructorList);
    }

    public void startDatePicker(ActionEvent event) {
        DatePicker startDatePicker = (DatePicker) event.getSource();
        startDate = startDatePicker.getValue();

        if (startDate != null) {
            String formattedDate = startDate.format(DateTimeFormatter.ISO_DATE);
            System.out.println("Selected Date (Formatted): " + formattedDate);
        } else {
            System.out.println("No date selected.");
            BasicValues.showErrorAlert(LanguageManager.getString("chooseDateError"));
        }
    }
    public void endDatePicker(ActionEvent event) {
        DatePicker endDatePicker = (DatePicker) event.getSource();
        endDate = endDatePicker.getValue();

        if (startDate != null && endDate != null) {
            if (endDate.isBefore(startDate) || endDate.isEqual(startDate)) {
                System.out.println("End date must be after the start date.");
                BasicValues.showInformationDialog(LanguageManager.getString("invalidSelectionDate"));
                endDatePicker.setValue(null); // Скинути значення endDatePicker
            }
        }
    }
    private Callback<DatePicker, DateCell> getDayCellFactory() {
        return datePicker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setDisable(item.isBefore(LocalDate.now()));
            }
        };
    }

    public void createCourseButton() {
        if (areAllFieldsFilled()) {
            errorTipLabel.setVisible(false);

            if (!DataBase.isDatabaseConnected()) {
                BasicValues.showErrorAlert(LanguageManager.getString("dbConnectionError"));
                return;
            }

            boolean userConfirmed = false;

            if(!statusComboBox.isVisible()){
                userConfirmed = BasicValues.showConfirmationDialog(LanguageManager.getString("createCourseConfirmation"));
            } else if (statusComboBox.isVisible()) {
                userConfirmed = BasicValues.showConfirmationDialog(LanguageManager.getString("updateCourseConfirmation"));
            }

            if (userConfirmed) {
                try (Connection connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD)) {
                    String selectedLocationFullData = locationComboBox.getSelectionModel().getSelectedItem();
                    String selectedScheduleFullData = scheduleComboBox.getSelectionModel().getSelectedItem();
                    String selectedInstructorFullName = instructorComboBox.getSelectionModel().getSelectedItem();

                    String[] locationData = selectedLocationFullData.split(", ");
                    String city = locationData[0];
                    String[] streetData = locationData[1].split(LanguageManager.getString("streetShortForm"));
                    String street = streetData[1];
                    String[] houseData = locationData[2].split(LanguageManager.getString("buildingShortForm"));
                    String house = houseData[1];

                    String[] scheduleData = selectedScheduleFullData.split(" "); // Split the full name into first name and last name
                    String day = scheduleData[0];
                    String hours = scheduleData[1];

                    String[] names = selectedInstructorFullName.split(" "); // Split the full name into first name and last name
                    String firstName = names[0];
                    String lastName = names[1];

                    int locationId = getLocationId(connection, city, street, house);
                    int scheduleId = getScheduleId(connection, day, hours);
                    int instructorId = getInstructorId(connection, firstName, lastName);

                    if(!statusComboBox.isVisible()){
                        boolean insertionSuccessful = insertCourseIntoDatabase(connection, locationId, scheduleId, instructorId);

                        if (insertionSuccessful) {
                            BasicValues.showInformationDialog(LanguageManager.getString("createNewCourseRecordInformation"));
                        }

                    } else if (statusComboBox.isVisible()) {
                        boolean updateSuccessful = updateCourseIntoDatabase(connection, locationId, scheduleId, instructorId);

                        if (updateSuccessful) {  // Print a message based on the result of the insertion
                            BasicValues.showConfirmationDialog(LanguageManager.getString("updateCourseRecordInformation"));
                        } else {
                            System.out.println("Error: Unable to update practice record into the database.");
                            BasicValues.showConfirmationDialog(LanguageManager.getString("updateCourseRecordFailedError"));
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }else {
                BasicValues.showInformationDialog(LanguageManager.getString("createNewCourseRecordFailedError"));
            }
        } else {
            errorTipLabel.setVisible(true);
            BasicValues.showWarningAlert((LanguageManager.getString("fillAllFieldsWarning")));
        }
    }

    public static int getLocationId(Connection connection, String city, String street, String house) throws SQLException {
        int locationId = -1;

        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT `id_локація` FROM `локація` WHERE `Місто` = ? AND `Вулиця` = ? AND `Будинок` = ?")) {
            preparedStatement.setString(1, city);
            preparedStatement.setString(2, street);
            preparedStatement.setString(3, house);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    locationId = resultSet.getInt("id_локація");
                }
            }
        }
        return locationId;
    }
    public static int getScheduleId(Connection connection, String day, String hours) throws SQLException {
        int scheduleId = -1;

        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT `id_розклад`FROM `розклад` WHERE `День` = ? AND `Година` = ?")) {
            preparedStatement.setString(1, day);
            preparedStatement.setString(2, hours);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    scheduleId = resultSet.getInt("id_розклад");
                }
            }
        }
        return scheduleId;
    }
    private int getInstructorId(Connection connection, String firstName, String lastName) throws SQLException {
        int instructorId = -1;

        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT `Id_інструктор` FROM Інструктор WHERE `Ім'я` = ? AND `Прізвище` = ?")) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    instructorId = resultSet.getInt("Id_інструктор");
                }
            }
        }
        return instructorId;
    }

    private boolean insertCourseIntoDatabase(Connection connection, int locationId, int scheduleId, int instructorId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `курси`(`Назва`, `Початок`, `Кінець`, `Категорія`, `Статус`, `id_локація`, `id_розклад`, `id_інструктор`) VALUES (?, ?, ?, ?,'Заплановано', ?, ?, ?)")) {
            preparedStatement.setString(1, nameTextField.getText());
            preparedStatement.setDate(2, java.sql.Date.valueOf(startDatePicker.getValue()));
            preparedStatement.setDate(3, java.sql.Date.valueOf(endDatePicker.getValue()));
            preparedStatement.setString(4, categoryComboBox.getValue());
            preparedStatement.setInt(5, locationId);
            preparedStatement.setInt(6, scheduleId);
            preparedStatement.setInt(7, instructorId);

            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            BasicValues.showInformationDialog(LanguageManager.getString("createNewCourseRecordFailedError") + e.getMessage());
            return false;
        }
    }

    private String selectedCourseId;
    private boolean updateCourseIntoDatabase(Connection connection, int locationId, int scheduleId, int instructorId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `курси` SET `Назва`= ?,`Початок`= ?, `Кінець`= ?, `Категорія`= ?, `Статус`= ?, `id_локація`= ?, `id_інструктор`= ?, `id_розклад`= ? WHERE `id_курси` = ?")) {
            preparedStatement.setString(1, nameTextField.getText());
            preparedStatement.setDate(2, java.sql.Date.valueOf(startDatePicker.getValue()));
            preparedStatement.setDate(3, java.sql.Date.valueOf(endDatePicker.getValue()));
            preparedStatement.setString(4, categoryComboBox.getValue());
            preparedStatement.setString(5, statusComboBox.getValue());
            preparedStatement.setInt(6, locationId);
            preparedStatement.setInt(7, instructorId);
            preparedStatement.setInt(8, scheduleId);
            preparedStatement.setInt(9, Integer.parseInt(selectedCourseId));

            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;
        }catch (SQLException e) {
            e.printStackTrace();
            BasicValues.showInformationDialog(LanguageManager.getString("updateCourseRecordFailedError") + e.getMessage());
            return false;
        }
    }

    public void setEditCourseData(AdminCourses.FullInfoCourse courseFullData, String courseId) {
        this.selectedCourseId = courseId;
        if (courseFullData != null) {

            nameTextField.setText(courseFullData.getName());

            startDate = LocalDate.parse(courseFullData.getStart());
            startDatePicker.setValue(startDate);
            endDate = LocalDate.parse(courseFullData.getEnd());
            endDatePicker.setValue(endDate);

            categoryComboBox.setValue(courseFullData.getCategory());
            locationComboBox.setValue(courseFullData.getAddress());
            scheduleComboBox.setValue(courseFullData.getSchedule());
            instructorComboBox.setValue(courseFullData.getInstructor());
            statusComboBox.setValue(courseFullData.getStatus());
        }
    }

    private boolean areAllFieldsFilled() {                                             // * Перевірка заповненості полів
        TextField[] textFields = {nameTextField};

        boolean allFieldsFilled = true;
        for (TextField textField : textFields) { // Перевірка, чи всі текстові поля заповнені
            if (textField.getText().trim().isEmpty()) {
                textField.setStyle(BasicValues.HIGHLIGHT_BORDERS);
                allFieldsFilled = false;
            } else {
                textField.setStyle(""); // Якщо поле заповнено, знімаємо стиль CSS
            }
        }

        if (startDatePicker.getValue() != null) { // Check if a date is selected
            startDatePicker.setStyle("");
        } else {
            startDatePicker.setStyle(BasicValues.HIGHLIGHT_BORDERS);
            allFieldsFilled = false;
        }

        if (endDatePicker.getValue() != null) { // Check if a date is selected
            endDatePicker.setStyle("");
        } else {
            endDatePicker.setStyle(BasicValues.HIGHLIGHT_BORDERS);
            allFieldsFilled = false;
        }

        if (categoryComboBox.getValue() == null || categoryComboBox.getValue().isEmpty()) { // Перевірка, чи обрано категорію
            categoryComboBox.setStyle(BasicValues.HIGHLIGHT_BORDERS);
            allFieldsFilled = false;
        } else {
            categoryComboBox.setStyle("");
        }

        if (locationComboBox.getValue() == null || locationComboBox.getValue().isEmpty()) { // Перевірка, чи обрано категорію
            locationComboBox.setStyle(BasicValues.HIGHLIGHT_BORDERS);
            allFieldsFilled = false;
        } else {
            locationComboBox.setStyle("");
        }

        if (scheduleComboBox.getValue() == null || scheduleComboBox.getValue().isEmpty()) { // Перевірка, чи обрано категорію
            scheduleComboBox.setStyle(BasicValues.HIGHLIGHT_BORDERS);
            allFieldsFilled = false;
        } else {
            scheduleComboBox.setStyle("");
        }

        if (instructorComboBox.getValue() == null || instructorComboBox.getValue().isEmpty()) { // Перевірка, чи обрано категорію
            instructorComboBox.setStyle(BasicValues.HIGHLIGHT_BORDERS);
            allFieldsFilled = false;
        } else {
            instructorComboBox.setStyle("");
        }

        return allFieldsFilled;
    }

    public void returnButton() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/autoschool/autoschool_javafx/admin/admin-courses.fxml"));
        Parent fxml = loader.load();

        AdminCourses adminCourses = loader.getController();
        adminCourses.setAdminMenuWindow(adminMenuWindow);
        adminCourses.setCurrentLanguage();

        adminMenuWindow.contentArea.getChildren().removeAll();
        adminMenuWindow.contentArea.getChildren().setAll(fxml);
    }
}

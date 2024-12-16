package autoschool.autoschool_javafx.Admin;

import autoschool.autoschool_javafx.BasicValues;

import autoschool.autoschool_javafx.CommonMethods;
import autoschool.autoschool_javafx.DataBase;
import autoschool.autoschool_javafx.LanguageManager;
import com.jfoenix.controls.JFXButton;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.Objects;
import java.util.Optional;

public class AdminEditSchedule extends Application {
    public TextField startTimeTextField, endTimeTextField;
    public ComboBox<String> scheduleInfoCombobox, dayCombobox;
    public Label scheduleLabel, dayLabel, startEndLabel, errorTipLabel;
    public JFXButton deleteScheduleButton, createScheduleButton, saveInfoButton, returnButton;

    public String day, startTime, endTime, hours;
    public int scheduleId;
    public Text editRecordTitleText;

    private AdminMenuWindow adminMenuWindow;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/autoschool/autoschool_javafx/admin/edit-schedule.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle(BasicValues.NAME);
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream(BasicValues.ICON))));
        primaryStage.setScene(new Scene(root, BasicValues.WINDOW_WIDTH, BasicValues.WINDOW_HEIGHT));

        primaryStage.setResizable(true); // * Відключаємо можливість масштабування вікна

        primaryStage.show();
    }

    public void setAdminMenuWindow(AdminMenuWindow adminMenuWindow) {
        this.adminMenuWindow = adminMenuWindow;
    }
    public void setCurrentLanguage() {
        translate();
    }
    public void translate() {
        editRecordTitleText.setText(LanguageManager.getString("editRecordTitleText"));
        scheduleLabel.setText(LanguageManager.getString("scheduleButton"));
        dayLabel.setText(LanguageManager.getString("dayLabel"));
        startEndLabel.setText(LanguageManager.getString("startEndLabel"));
        errorTipLabel.setText(LanguageManager.getString("errorTipLabel"));

        deleteScheduleButton.setText(LanguageManager.getString("deleteRecordButton"));
        createScheduleButton.setText(LanguageManager.getString("createRecordButton"));
        saveInfoButton.setText(LanguageManager.getString("editAccount"));
        returnButton.setText(LanguageManager.getString("returnButton"));

        scheduleInfoCombobox.setPromptText(LanguageManager.getString("scheduleComboBoxPrompt"));
        dayCombobox.setPromptText(LanguageManager.getString("dayComboBoxPrompt"));
        startTimeTextField.setPromptText(LanguageManager.getString("startTimeTextFieldPrompt"));
        endTimeTextField.setPromptText(LanguageManager.getString("endTimeTextFieldPrompt"));
    }

    public void initialize() {
        CommonMethods.timePickerRestrictions(startTimeTextField);
        CommonMethods.timePickerRestrictions(endTimeTextField);

        populateScheduleComboBox(scheduleInfoCombobox);

        CommonMethods.dayComboBoxList(dayCombobox);

        scheduleInfoCombobox.setValue(LanguageManager.getString("addNewScheduleText"));

        saveInfoButton.setDisable(true);
        deleteScheduleButton.setDisable(true);
        createScheduleButton.setDisable(false);
        dayCombobox.setDisable(false);
        startTimeTextField.setDisable(false);
        endTimeTextField.setDisable(false);

        scheduleInfoCombobox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                if(newValue.equals(LanguageManager.getString("addNewScheduleText"))) {
                    clearFields(); // Очистити поля, якщо обрано "Додати нову локацію"

                    saveInfoButton.setDisable(true);
                    deleteScheduleButton.setDisable(true);
                } else {
                    String[] scheduleParts = newValue.split(" "); // Розділити рядок за пробілом

                    if (scheduleParts.length >= 2) {
                        day = scheduleParts[0]; // Отримати першу частину як день
                        String timeRange = scheduleParts[1]; // Отримати другу частину як діапазон часу

                        String[] timeParts = timeRange.split("-"); // Розділити діапазон часу за тире

                        if (timeParts.length == 2) {
                            String startTime = timeParts[0]; // Початковий час
                            String endTime = timeParts[1]; // Кінцевий час

                            hours = startTime + "-" + endTime;

                            dayCombobox.setValue(day);
                            startTimeTextField.setText(startTime);
                            endTimeTextField.setText(endTime);

                            Connection connection;
                            try {
                                connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD);
                                scheduleId = AdminNewCourse.getScheduleId(connection, day, hours);
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }

                            saveInfoButton.setDisable(false);
                            deleteScheduleButton.setDisable(false);
                            createScheduleButton.setDisable(false);
                            dayCombobox.setDisable(false);
                            startTimeTextField.setDisable(false);
                            endTimeTextField.setDisable(false);
                        }
                    }
                }
            }
        });

    }

    private void clearFields() {
        dayCombobox.setValue(null);
        startTimeTextField.clear();
        endTimeTextField.clear();

        saveInfoButton.setDisable(true);
        deleteScheduleButton.setDisable(true);

        day = null;
        startTime = null;
        endTime = null;
    }

    public void populateScheduleComboBox(ComboBox<String> comboBox) {
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
        scheduleList.add(LanguageManager.getString("addNewScheduleText"));
        comboBox.setItems(scheduleList);
    }


    public void deleteScheduleButton() {
        if (areAllFieldsFilled()) {
            errorTipLabel.setVisible(false);

            deleteScheduleById(scheduleId);
        } else {
            errorTipLabel.setVisible(true);
            BasicValues.showWarningAlert((LanguageManager.getString("fillAllFieldsWarning")));
        }
    }
    public void deleteScheduleById(int scheduleId) {
        if (areAllFieldsFilled()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(LanguageManager.getString("confirmationDialog"));
            alert.setHeaderText(LanguageManager.getString("deleteConformation"));
            alert.setContentText(LanguageManager.getString("additionalInfoDeleteConfirmation") + "\n" +
                            LanguageManager.getString("deleteScheduleConfirmation") + "\n" + LanguageManager.getString("dayLabel") + ": "
                            + day + "\n" + LanguageManager.getString("startEndLabel") + ": " + hours);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try (Connection connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD);
                     PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM `розклад` WHERE `id_розклад` = ?")) {
                    preparedStatement.setInt(1, scheduleId);
                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        populateScheduleComboBox(scheduleInfoCombobox); // Refresh the combo box after deletion
                        BasicValues.showInformationDialog(LanguageManager.getString("deleteScheduleSuccess"));
                        clearFields(); // Clear fields after successful deletion
                        scheduleInfoCombobox.getSelectionModel().select(LanguageManager.getString("addNewScheduleText"));
                        saveInfoButton.setDisable(true);
                    } else {
                        BasicValues.showInformationDialog(LanguageManager.getString("deleteScheduleRecordFailedError"));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    BasicValues.showInformationDialog(LanguageManager.getString("deleteScheduleRecordFailedError") + " " + e.getMessage());
                }
            }
        } else {
            BasicValues.showWarningAlert((LanguageManager.getString("fillAllFieldsWarning")));
        }
    }


    public void createScheduleButton() {
        if (areAllFieldsFilled()) {
            errorTipLabel.setVisible(false);

            day = dayCombobox.getValue();
            startTime= startTimeTextField.getText();
            endTime = endTimeTextField.getText();

            hours = startTime + "-" + endTime;

            insertNewSchedule(day, hours);
        } else {
            errorTipLabel.setVisible(true);
            BasicValues.showWarningAlert((LanguageManager.getString("fillAllFieldsWarning")));
        }
    }
    public void insertNewSchedule(String day, String hours) {
        try (Connection connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `розклад`(`День`, `Година`) VALUES (?, ?)")) {
            if (isScheduleAlreadyRegistered(connection, day, hours)) {
                BasicValues.showInformationDialog(LanguageManager.getString("alreadyExistScheduleInfo"));
                return;
            }
            preparedStatement.setString(1, day);
            preparedStatement.setString(2, hours);

            int rowsAffected = preparedStatement.executeUpdate(); // Виконати запит на вставку даних

            if (rowsAffected > 0) {
                BasicValues.showInformationDialog(LanguageManager.getString("scheduleInsertedSuccessfully"));
                populateScheduleComboBox(scheduleInfoCombobox);
                dayCombobox.valueProperty().set(null);
                startTimeTextField.setText(startTime);
                endTimeTextField.setText(endTime);
            } else {
                BasicValues.showInformationDialog(LanguageManager.getString("scheduleInsertionFailed"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            BasicValues.showInformationDialog(LanguageManager.getString("scheduleInsertionFailed") + e.getMessage());
        }
    }

    private boolean isScheduleAlreadyRegistered(Connection connection, String day, String hours) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM `розклад` WHERE `День` = ? AND `Година` = ?;")) {
            preparedStatement.setString(1, day);
            preparedStatement.setString(2, hours);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        }
        return false;
    }

    public void saveInfoButton() {
        if (areAllFieldsFilled()) {
            errorTipLabel.setVisible(false);

            day = dayCombobox.getValue();
            startTime= startTimeTextField.getText();
            endTime = endTimeTextField.getText();

            hours = startTime + "-" + endTime;

            updateSchedule(day, hours, scheduleId);
        } else {
            errorTipLabel.setVisible(true);
            BasicValues.showWarningAlert((LanguageManager.getString("fillAllFieldsWarning")));
        }
    }
    public void updateSchedule(String day, String hours, int scheduleId) {
        try (Connection connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `розклад` SET `День`= ?,`Година`= ? WHERE `id_розклад` = ?")) {
            if (isScheduleAlreadyRegistered(connection, day, hours)) {
                BasicValues.showInformationDialog(LanguageManager.getString("alreadyExistScheduleInfo"));
                return;
            }
            preparedStatement.setString(1, day);
            preparedStatement.setString(2, hours);
            preparedStatement.setInt(3, scheduleId);

            int rowsAffected = preparedStatement.executeUpdate(); // Execute the update query

            if (rowsAffected > 0) {
                BasicValues.showInformationDialog(LanguageManager.getString("updateScheduleRecordInformation"));
                populateScheduleComboBox(scheduleInfoCombobox); // Refresh the combo box after update
            } else {
                BasicValues.showInformationDialog(LanguageManager.getString("updateScheduleRecordFailedError"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            BasicValues.showInformationDialog(LanguageManager.getString("updateScheduleRecordFailedError") + e.getMessage());
        }
    }

    private boolean areAllFieldsFilled() {                                             // * Перевірка заповненості полів
        TextField[] textFields = {startTimeTextField, endTimeTextField};

        boolean allFieldsFilled = true;
        for (TextField textField : textFields) { // Перевірка, чи всі текстові поля заповнені
            if (textField.getText().trim().isEmpty()) {
                textField.setStyle(BasicValues.HIGHLIGHT_BORDERS);
                allFieldsFilled = false;
            } else {
                textField.setStyle(""); // Якщо поле заповнено, знімаємо стиль CSS
            }
        }

        if (dayCombobox.getValue() == null || dayCombobox.getValue().isEmpty()) { // Перевірка, чи обрано категорію
            dayCombobox.setStyle(BasicValues.HIGHLIGHT_BORDERS);
            allFieldsFilled = false;
        } else {
            dayCombobox.setStyle("");
        }

        if (startTimeTextField.getText().equals(endTimeTextField.getText())) {
            startTimeTextField.setStyle(BasicValues.HIGHLIGHT_BORDERS);
            endTimeTextField.setStyle(BasicValues.HIGHLIGHT_BORDERS);
            allFieldsFilled = false;
        } else {
            startTimeTextField.setStyle("");
            endTimeTextField.setStyle("");
        }

        return allFieldsFilled;
    }

    public void returnButton() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/autoschool/autoschool_javafx/admin/admin-new-course.fxml"));
        Parent fxml = loader.load();

        AdminNewCourse adminNewCourse = loader.getController();
        adminNewCourse.setCurrentLanguage();
        adminNewCourse.setAdminMenuWindow(adminMenuWindow);

        adminMenuWindow.contentArea.getChildren().removeAll();
        adminMenuWindow.contentArea.getChildren().setAll(fxml);
    }
}

package autoschool.autoschool_javafx.Instructor;

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

public class InstructorNewPractice implements Localisation {
    public Text createNewPracticeTitleText;
    public JFXButton returnButton, createPracticeButton;
    public Label dateTimeLabel, routeLabel, durationLabel, chooseStudentLabel, statusLabel, errorTipLabel;
    public TextField timePickerTextField, routeTextField;
    public DatePicker datePicker;
    public ComboBox<String> durationComboBox, studentComboBox, statusComboBox;

    private InstructorMenuWindow instructorMenuWindow;
    private LocalDate Date;

    public void setInstructorMenuWindow(InstructorMenuWindow instructorMenuWindow) {
        this.instructorMenuWindow = instructorMenuWindow;
    }

    public void setCurrentLanguage() {
        translate();
    }

    public void translate() {
        createNewPracticeTitleText.setText(LanguageManager.getString("createNewPracticeTitleText"));

        returnButton.setText(LanguageManager.getString("returnButton"));
        createPracticeButton.setText(LanguageManager.getString("editAccount"));

        chooseStudentLabel.setText(LanguageManager.getString("studentRoleLabel"));
        dateTimeLabel.setText(LanguageManager.getString("dateTimeLabel"));
        routeLabel.setText(LanguageManager.getString("routeColumn"));
        statusLabel.setText(LanguageManager.getString("statusColumn"));
        durationLabel.setText(LanguageManager.getString("durationLabel"));
        errorTipLabel.setText(LanguageManager.getString("errorTipLabel"));

        datePicker.setPromptText(LanguageManager.getString("datePickerPrompt"));
        timePickerTextField.setPromptText(LanguageManager.getString("timePickerTextFieldPrompt"));
        routeTextField.setPromptText(LanguageManager.getString("routeTextFieldPrompt"));
        durationComboBox.setPromptText(LanguageManager.getString("durationComboBoxPrompt"));
        studentComboBox.setPromptText(LanguageManager.getString("studentComboBoxPrompt"));
        statusComboBox.setPromptText(LanguageManager.getString("statusComboBoxPrompt"));
    }

    public void initialize() {
        CommonMethods.timePickerRestrictions(timePickerTextField);

        datePicker.setDayCellFactory(getDayCellFactory());

        CommonMethods.durationComboBoxList(durationComboBox);
        CommonMethods.statusComboBoxList(statusComboBox);

        populateStudentComboBox();
    }

    private void populateStudentComboBox() {
        ObservableList<String> studentsList = FXCollections.observableArrayList();

        try (Connection connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT `id_студент`, `Ім'я`, `Прізвище` FROM `студент`");
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String studentFullName = resultSet.getString("Ім'я") + " " + resultSet.getString("Прізвище");
                studentsList.add(studentFullName);
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception based on your application's needs
        }

        studentComboBox.setItems(studentsList);
    }

    public void datePicker(ActionEvent event) {
        DatePicker datePicker = (DatePicker) event.getSource();
        Date = datePicker.getValue();

        if (Date != null) {
            String formattedDate = Date.format(DateTimeFormatter.ISO_DATE);
            System.out.println("Selected Date (Formatted): " + formattedDate);
        } else {
            System.out.println("No date selected.");
            BasicValues.showErrorAlert(LanguageManager.getString("chooseDateError"));
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

    public void createPracticeButton() {
        if (areAllFieldsFilled()) {
            errorTipLabel.setVisible(false);

            if (!DataBase.isDatabaseConnected()) {
                BasicValues.showErrorAlert(LanguageManager.getString("dbConnectionError"));
                return;
            }

            boolean userConfirmed = false;

            if(!statusComboBox.isVisible()){
                userConfirmed = BasicValues.showConfirmationDialog(LanguageManager.getString("createNewPracticeConfirmation"));
            } else if (statusComboBox.isVisible()) {
                userConfirmed = BasicValues.showConfirmationDialog(LanguageManager.getString("updateNewPracticeConfirmation"));
            }

            if (userConfirmed) {
                try (Connection connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD)) {
                    String selectedStudentFullName = studentComboBox.getSelectionModel().getSelectedItem(); // Get the selected student's full name from the combo box

                    String[] names = selectedStudentFullName.split(" "); // Split the full name into first name and last name
                    String firstName = names[0];
                    String lastName = names[1];

                    int studentId = getStudentId(connection, firstName, lastName); // Retrieve the student's ID based on their first name and last name

                    if(!statusComboBox.isVisible()){
                        boolean insertionSuccessful = insertPracticeIntoDatabase(connection, studentId); // Insert the new practice record into the database

                        if (insertionSuccessful) {  // Print a message based on the result of the insertion
                            System.out.println("Practice record inserted into the database successfully.");
                            BasicValues.showConfirmationDialog(LanguageManager.getString("createNewPracticeRecordInformation"));
                        } else {
                            System.out.println("Error: Unable to insert practice record into the database.");
                            BasicValues.showConfirmationDialog(LanguageManager.getString("createNewPracticeRecordFailedError"));
                        }
                    } else if (statusComboBox.isVisible()) {
                        boolean updateSuccessful = updatePracticeIntoDatabase(connection, studentId); // Update the  practice record into the database

                        if (updateSuccessful) {  // Print a message based on the result of the insertion
                            System.out.println("Practice record updated into the database successfully.");
                            BasicValues.showConfirmationDialog(LanguageManager.getString("updatePracticeRecordInformation"));
                        } else {
                            System.out.println("Error: Unable to update practice record into the database.");
                            BasicValues.showConfirmationDialog(LanguageManager.getString("updatePracticeRecordFailedError"));
                        }
                    }

                } catch (SQLException e) {
                    e.printStackTrace(); // Handle the exception based on your application's needs
                }
            }
        } else {
            errorTipLabel.setVisible(true);
            BasicValues.showWarningAlert((LanguageManager.getString("fillAllFieldsWarning")));
        }
    }

    private int getStudentId(Connection connection, String firstName, String lastName) throws SQLException {
        int studentId = -1;

        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT `id_студент` FROM `студент` WHERE `Ім'я` = ? AND `Прізвище` = ?")) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    studentId = resultSet.getInt("id_студент");
                }
            }
        }
        return studentId;
    }

    private boolean insertPracticeIntoDatabase(Connection connection, int studentId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `практичні_заняття`(`Дата`, `Час`, `Маршрут`, `Тривалість, год`, `Статус`, `id_інструктор`, `id_студент`) VALUES (?,?,?,?,?,?,?)")) {
            preparedStatement.setDate(1, java.sql.Date.valueOf(datePicker.getValue()));
            preparedStatement.setString(2, timePickerTextField.getText());
            preparedStatement.setString(3, routeTextField.getText());
            preparedStatement.setString(4, durationComboBox.getValue());
            preparedStatement.setString(5, "Заплановано");
            preparedStatement.setInt(6, InstructorMenuWindow.id_instructor);
            preparedStatement.setInt(7, studentId);

            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;
        }
    }

    private String selectedPracticeId;
    private boolean updatePracticeIntoDatabase(Connection connection, int studentId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `практичні_заняття` SET `Дата`= ?,`Час`= ?,`Маршрут`= ?,`Тривалість, год`= ?,`Статус`= ?,`id_інструктор`= ?,`id_студент`= ? WHERE `id_практика` = ?")) {
            preparedStatement.setDate(1, java.sql.Date.valueOf(datePicker.getValue()));
            preparedStatement.setString(2, timePickerTextField.getText());
            preparedStatement.setString(3, routeTextField.getText());
            preparedStatement.setString(4, durationComboBox.getValue());
            preparedStatement.setString(5, statusComboBox.getValue());
            preparedStatement.setInt(6, InstructorMenuWindow.id_instructor);
            preparedStatement.setInt(7, studentId);
            preparedStatement.setInt(8, Integer.parseInt(selectedPracticeId));

            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;
        }
    }

    public void setEditPracticeData(InstructorPracticeSchedule.PracticeData practiceData, String practiceId) {
        this.selectedPracticeId = practiceId;
        if (practiceData != null) {

            Date = LocalDate.parse(practiceData.getDate());
            datePicker.setValue(Date);

            timePickerTextField.setText(practiceData.getTime());
            routeTextField.setText(practiceData.getRoute());

            durationComboBox.setValue(practiceData.getDuration());
            studentComboBox.setValue(practiceData.getStudent());
            statusComboBox.setValue(practiceData.getStatus());
        }
    }

    private boolean areAllFieldsFilled() {                                             // * Перевірка заповненості полів
        TextField[] textFields = { // Масив із текстових полів, які потрібно перевірити
                timePickerTextField, routeTextField};

        boolean allFieldsFilled = true;
        for (TextField textField : textFields) { // Перевірка, чи всі текстові поля заповнені
            if (textField.getText().trim().isEmpty()) {
                textField.setStyle(BasicValues.HIGHLIGHT_BORDERS);
                allFieldsFilled = false;
            } else {
                textField.setStyle(""); // Якщо поле заповнено, знімаємо стиль CSS
            }
        }

        if (datePicker.getValue() != null) { // Check if a date is selected
            datePicker.setStyle("");
        } else {
            datePicker.setStyle(BasicValues.HIGHLIGHT_BORDERS);
            allFieldsFilled = false;
        }

        if (durationComboBox.getValue() == null || durationComboBox.getValue().isEmpty()) { // Перевірка, чи обрано категорію
            durationComboBox.setStyle(BasicValues.HIGHLIGHT_BORDERS);
            allFieldsFilled = false;
        } else {
            durationComboBox.setStyle("");
        }

        if (studentComboBox.getValue() == null || studentComboBox.getValue().isEmpty()) { // Перевірка, чи обрано категорію
            studentComboBox.setStyle(BasicValues.HIGHLIGHT_BORDERS);
            allFieldsFilled = false;
        } else {
            studentComboBox.setStyle("");
        }

        return allFieldsFilled;
    }

    public void returnButton() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/autoschool/autoschool_javafx/Instructor/instructor-practice.fxml"));
        Parent fxml = loader.load();

        InstructorPracticeSchedule instructorPracticeSchedule = loader.getController();
        instructorPracticeSchedule.setInstructorMenuWindow(instructorMenuWindow);
        instructorPracticeSchedule.setCurrentLanguage();


        instructorMenuWindow.contentArea.getChildren().removeAll();
        instructorMenuWindow.contentArea.getChildren().setAll(fxml);
    }
}

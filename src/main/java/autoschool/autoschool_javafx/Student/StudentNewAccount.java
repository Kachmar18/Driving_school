package autoschool.autoschool_javafx.Student;

import autoschool.autoschool_javafx.*;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;


public class StudentNewAccount implements Localisation {
    public JFXButton returnButton, createNewAccount;
    public ImageView roleIcon, passwordIcon;
    public Label newAccountTitleLabel, roleLabel, namingLabel, dateBirthLabel, phoneLabel, emailLabel, sexLabel, categoryLabel, newPasswordLabel, errorTipLabel;
    public TextField lastNameTextField, firstNameTextField, secondNameTextField, phoneTextField, emailTextField;
    public RadioButton SexFemaleRadioButton, SexMaleRadioButton;
    public ToggleGroup sexRadioButtons;
    public ComboBox<String> categoryComboBox;
    public PasswordField passwordTextField;
    public ToggleButton showPasswordButton;
    public DatePicker datePicker;

    private int currentRoleState;
    private String sex;
    private LocalDate birthDate;

    public void initialize() {
        CommonMethods.applyNameLimit(lastNameTextField);
        CommonMethods.applyNameLimit(firstNameTextField);
        CommonMethods.applySingleWordLimit(secondNameTextField);

        CommonMethods.applyPhoneFormat(phoneTextField);

        CommonMethods.setupDatePicker(datePicker);
    }

    public void setCurrentLanguage() {
        translate();
    }

    public void setCurrentRoleState(int roleState) {
        currentRoleState = roleState;
        translate();
    }

    public void translate() {
        newAccountTitleLabel.setText(LanguageManager.getString("newAccountTitleLabel"));
        returnButton.setText(LanguageManager.getString("returnButton"));
        createNewAccount.setText(LanguageManager.getString("createNewAccount"));
        namingLabel.setText(LanguageManager.getString("namingLabel"));
        dateBirthLabel.setText(LanguageManager.getString("dateBirthLabel"));
        phoneLabel.setText(LanguageManager.getString("phoneLabel"));
        emailLabel.setText(LanguageManager.getString("emailLabel"));
        sexLabel.setText(LanguageManager.getString("sexLabel"));
        categoryLabel.setText(LanguageManager.getString("categoryLabel"));
        newPasswordLabel.setText(LanguageManager.getString("newPasswordLabel"));
        SexFemaleRadioButton.setText(LanguageManager.getString("SexFemaleRadioButton"));
        SexMaleRadioButton.setText(LanguageManager.getString("SexMaleRadioButton"));
        errorTipLabel.setText(LanguageManager.getString("errorTipLabel"));

        lastNameTextField.setPromptText(LanguageManager.getString("lastNamePrompt"));
        firstNameTextField.setPromptText(LanguageManager.getString("firstNamePrompt"));
        secondNameTextField.setPromptText(LanguageManager.getString("secondNamePrompt"));
        datePicker.setPromptText(LanguageManager.getString("datePickerPrompt"));
        categoryComboBox.setPromptText(LanguageManager.getString("categoryComboBoxPrompt"));

        CommonMethods.updateRoleLabelAndIcon(roleLabel, roleIcon, this.getClass());
    }

    public void datePicker() {
        birthDate = datePicker.getValue();
        if (birthDate != null) {
            String formattedDate = birthDate.format(DateTimeFormatter.ISO_DATE);
            System.out.println("Selected Date (Formatted): " + formattedDate);
        } else {
            System.out.println("No date selected.");
            BasicValues.showErrorAlert(LanguageManager.getString("chooseDateError"));
        }
    }

    public void categoryComboBox() {
        CommonMethods.categoryComboBoxList(categoryComboBox);
    }

    public void sexValuesRadioButton(){
        if (SexFemaleRadioButton.isSelected()){
            sex = "Жінка";
        }else if (SexMaleRadioButton.isSelected()){
            sex = "Чоловік";
        }
    }

    public void showPasswordButton() {
        CommonMethods.showHidePassword(passwordTextField, passwordIcon, getClass());
    }

    public void createNewStudentAccount() throws SQLException {
        if (areAllFieldsFilled()) {
            errorTipLabel.setVisible(false);

            if (!DataBase.isDatabaseConnected()) { // Check if the connection to the database is successful
                BasicValues.showErrorAlert(LanguageManager.getString("dbConnectionError"));
                return;
            }

            boolean userConfirmed = BasicValues.showConfirmationDialog(LanguageManager.getString("createNewAccountConfirmation"));

            if (userConfirmed) {
                sexValuesRadioButton();
                String errorMessage = insertStudentAccountToDatabase();

                if (errorMessage == null) {
                    BasicValues.showConfirmationDialog(LanguageManager.getString("accountCreationSuccess") +
                            LanguageManager.getString("loginEmail") + emailTextField.getText() + "\n" +
                            LanguageManager.getString("loginPassword") + passwordTextField.getText());
                } else {
                    BasicValues.showErrorAlert(LanguageManager.getString("accountCreationError") + "\n" + errorMessage);
                }
            }
        } else {
            errorTipLabel.setVisible(true);
            BasicValues.showWarningAlert((LanguageManager.getString("fillAllFieldsWarning")));
        }
    }

    private String insertStudentAccountToDatabase() throws SQLException {
        Connection connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD);
        try{
            if (DataBase.isAccountAlreadyRegistered(connection, DataBase.sql_checkExistingStudentAccountInDatabase, phoneTextField, emailTextField)) {
                return LanguageManager.getString("checkStudentExistingError");
            }
            try (PreparedStatement statement = connection.prepareStatement(DataBase.sql_insertStudentAccountToDatabase)) {
                statement.setString(1, firstNameTextField.getText());
                statement.setString(2, secondNameTextField.getText());
                statement.setString(3, lastNameTextField.getText());
                statement.setString(4, String.valueOf(birthDate));
                statement.setString(5, phoneTextField.getText());
                statement.setString(6, emailTextField.getText());
                statement.setString(7, sex);
                statement.setString(8, categoryComboBox.getValue());
                statement.setString(9, passwordTextField.getText());

                int rowsAffected = statement.executeUpdate(); // Execute the update
                if (rowsAffected > 0) { // Check the result of the update
                    return null; // Success
                } else {
                    return "Не змінено жодного рядка. Перевірте правильність наданих даних.";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    private boolean areAllFieldsFilled() {                                             // * Перевірка заповненості полів
        TextField[] textFields = { // Масив із текстових полів, які потрібно перевірити
                lastNameTextField, firstNameTextField, secondNameTextField, phoneTextField, emailTextField, passwordTextField
        };

        boolean allFieldsFilled = true;
        for (TextField textField : textFields) { // Перевірка, чи всі текстові поля заповнені
            if (textField.getText().trim().isEmpty()) {
                textField.setStyle(BasicValues.HIGHLIGHT_BORDERS);
                allFieldsFilled = false;
            } else {
                textField.setStyle(""); // Якщо поле заповнено, знімаємо стиль CSS
            }
        }

        String lastName = lastNameTextField.getText().trim();
        boolean isLastNameValid = CommonMethods.isValidName(lastName);
        lastNameTextField.setStyle(!isLastNameValid ? BasicValues.HIGHLIGHT_BORDERS : "");
        allFieldsFilled &= isLastNameValid;

        String firstName = firstNameTextField.getText().trim();
        boolean isFirstNameValid = CommonMethods.isValidName(firstName);
        firstNameTextField.setStyle(!isFirstNameValid ? BasicValues.HIGHLIGHT_BORDERS : "");
        allFieldsFilled &= isFirstNameValid;

        if (datePicker.getValue() != null) { // Check if a date is selected
            LocalDate currentDate = LocalDate.now();
            int age = Period.between(birthDate, currentDate).getYears();

            if (age < 16 || age > 100) {
                datePicker.setStyle(BasicValues.HIGHLIGHT_BORDERS);
                allFieldsFilled = false;
            } else {
                datePicker.setStyle("");
            }
        } else {
            datePicker.setStyle(BasicValues.HIGHLIGHT_BORDERS);
            allFieldsFilled = false;
        }

        emailTextField.setStyle(emailTextField.getText().matches(BasicValues.EMAIL_FORMAT) ? "" : BasicValues.HIGHLIGHT_BORDERS);

        phoneTextField.setStyle(phoneTextField.getText().matches(BasicValues.PHONE_FORMAT) ? "" : BasicValues.HIGHLIGHT_BORDERS);

        if (!SexFemaleRadioButton.isSelected() && !SexMaleRadioButton.isSelected()) { // Перевірка, чи обрано стать
            SexFemaleRadioButton.setStyle(BasicValues.HIGHLIGHT_BORDERS);
            SexMaleRadioButton.setStyle(BasicValues.HIGHLIGHT_BORDERS);
            allFieldsFilled = false;
        } else {
            SexFemaleRadioButton.setStyle("");
            SexMaleRadioButton.setStyle("");
        }

        if (categoryComboBox.getValue() == null || categoryComboBox.getValue().isEmpty()) { // Перевірка, чи обрано категорію
            categoryComboBox.setStyle(BasicValues.HIGHLIGHT_BORDERS);
            allFieldsFilled = false;
        } else {
            categoryComboBox.setStyle("");
        }

        return allFieldsFilled;
    }

    public void returnButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/autoschool/autoschool_javafx/main-window.fxml"));
        Parent root = loader.load();

        MainWindow mainWindow = loader.getController();
        mainWindow.setCurrentRoleState(currentRoleState); // Pass the current role state back
        mainWindow.updateLanguageIcon(); // Call the method to update the language icon

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}

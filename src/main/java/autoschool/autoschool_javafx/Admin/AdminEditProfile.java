package autoschool.autoschool_javafx.Admin;

import autoschool.autoschool_javafx.*;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class AdminEditProfile implements Localisation {
    public JFXButton returnButton, editAccount;
    public ImageView roleIcon, passwordIcon;
    public Text editAccountTitleText;
    public Label roleLabel, namingLabel, dateBirthLabel, phoneLabel, emailLabel, sexLabel, newPasswordLabel, errorTipLabel;
    public TextField lastNameTextField, firstNameTextField, secondNameTextField, phoneTextField, emailTextField;
    public RadioButton SexFemaleRadioButton, SexMaleRadioButton;
    public ToggleGroup sexRadioButtons;
    public PasswordField passwordTextField;
    public ToggleButton showPasswordButton;
    public DatePicker datePicker;

    private AdminMenuWindow adminMenuWindow;
    private int currentRoleState;
    private String sex;
    private LocalDate birthDate;

    public void setAdminMenuWindow(AdminMenuWindow adminMenuWindow) {
        this.adminMenuWindow = adminMenuWindow;
    }

    public void setCurrentLanguage() {
        translate();
    }

    public void setCurrentRoleState(int roleState) {
        currentRoleState = roleState;
        translate();
    }

    public void translate() {
        editAccountTitleText.setText(LanguageManager.getString("editAccountTitleText"));
        returnButton.setText(LanguageManager.getString("returnButton"));
        editAccount.setText(LanguageManager.getString("editAccount"));
        namingLabel.setText(LanguageManager.getString("namingLabel"));
        dateBirthLabel.setText(LanguageManager.getString("dateBirthLabel"));
        phoneLabel.setText(LanguageManager.getString("phoneLabel"));
        emailLabel.setText(LanguageManager.getString("emailLabel"));
        sexLabel.setText(LanguageManager.getString("sexLabel"));
        newPasswordLabel.setText(LanguageManager.getString("newPasswordLabel"));
        SexFemaleRadioButton.setText(LanguageManager.getString("SexFemaleRadioButton"));
        SexMaleRadioButton.setText(LanguageManager.getString("SexMaleRadioButton"));
        errorTipLabel.setText(LanguageManager.getString("errorTipLabel"));

        lastNameTextField.setPromptText(LanguageManager.getString("lastNamePrompt"));
        firstNameTextField.setPromptText(LanguageManager.getString("firstNamePrompt"));
        secondNameTextField.setPromptText(LanguageManager.getString("secondNamePrompt"));
        datePicker.setPromptText(LanguageManager.getString("datePickerPrompt"));
    }

    public void initialize() throws SQLException {
        selectInformationFromDatabase();

        CommonMethods.applyNameLimit(lastNameTextField);
        CommonMethods.applyNameLimit(firstNameTextField);
        CommonMethods.applySingleWordLimit(secondNameTextField);

        CommonMethods.setupDatePicker(datePicker);
    }

    private String originalPhone, originalEmail;

    public void selectInformationFromDatabase() throws SQLException {
        Connection connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD);
        try (PreparedStatement preparedStatement = connection.prepareStatement(DataBase.sql_selectInformationAdminAccount)) {
            preparedStatement.setString(1, String.valueOf(AdminMenuWindow.id_admin));

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    firstNameTextField.setText(resultSet.getString("Ім'я"));
                    secondNameTextField.setText(resultSet.getString("По-батькові"));
                    lastNameTextField.setText(resultSet.getString("Прізвище"));

                    birthDate = LocalDate.parse(resultSet.getString("Дата_народження"));
                    datePicker.setValue(birthDate);

                    CommonMethods.applyPhoneExistedFormat(phoneTextField);
                    phoneTextField.setText(resultSet.getString("Номер_телефону"));

                    emailTextField.setText(resultSet.getString("E-mail"));

                    sex = resultSet.getString("Стать");
                    setSexRadioButtons(sex);

                    passwordTextField.setText(resultSet.getString("Пароль_входу"));

                    originalPhone = resultSet.getString("Номер_телефону");
                    originalEmail = resultSet.getString("E-mail");
                } else {
                    BasicValues.showErrorAlert(LanguageManager.getString("loginFailedError"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    public void sexValuesRadioButton(){
        if (SexFemaleRadioButton.isSelected()){
            sex = "Жінка";
        }else if (SexMaleRadioButton.isSelected()){
            sex = "Чоловік";
        }
    }

    private void setSexRadioButtons(String sex) {
        if ("Жінка".equals(sex)) {
            SexFemaleRadioButton.setSelected(true);
        } else if ("Чоловік".equals(sex)) {
            SexMaleRadioButton.setSelected(true);
        }
    }

    public void showPasswordButton() {
        CommonMethods.showHidePassword(passwordTextField, passwordIcon, getClass());
    }

    public void editAccount() throws SQLException {
        if (areAllFieldsFilled()) {
            errorTipLabel.setVisible(false);

            if (!DataBase.isDatabaseConnected()) {
                BasicValues.showErrorAlert(LanguageManager.getString("dbConnectionError"));
                return;
            }

            boolean userConfirmed = BasicValues.showConfirmationDialog(LanguageManager.getString("updateProfileConfirmation"));

            if (userConfirmed) {
                sexValuesRadioButton();
                String errorMessage = updateAdminProfileInDatabase();
                if (errorMessage == null) {
                    BasicValues.showConfirmationDialog(LanguageManager.getString("updateProfileSuccess"));
                } else {
                    BasicValues.showErrorAlert(LanguageManager.getString("accountUpdateError") + "\n" + errorMessage);
                }
            }
        } else {
            errorTipLabel.setVisible(true);
            BasicValues.showWarningAlert((LanguageManager.getString("fillAllFieldsWarning")));
        }
    }

    public String updateAdminProfileInDatabase() throws SQLException {
        Connection connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD);
        try {
            if (!phoneTextField.getText().equals(originalPhone) || !emailTextField.getText().equals(originalEmail)) {
                if (isDuplicateEntry(connection)) {
                    return "Такий номер телефону або E-mail вже існує в базі даних.";
                }
            }
            try (PreparedStatement statement = connection.prepareStatement(DataBase.sql_editProfileAdminAccount)) {
                statement.setString(1, firstNameTextField.getText());
                statement.setString(2, secondNameTextField.getText());
                statement.setString(3, lastNameTextField.getText());
                statement.setString(4, String.valueOf(birthDate));
                statement.setString(5, phoneTextField.getText());
                statement.setString(6, emailTextField.getText());
                statement.setString(7, sex);
                statement.setString(8, passwordTextField.getText());
                statement.setString(9, String.valueOf(AdminMenuWindow.id_admin));

                int rowsAffected = statement.executeUpdate(); // Execute the update
                if (rowsAffected > 0) { // Check the result of the update
                    adminMenuWindow.updateAdminInfo(firstNameTextField.getText(), lastNameTextField.getText());
                    return null; // Success
                } else {
                    return "Не змінено жодного рядка. Перевірте правильність наданих даних.";
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return e.getMessage(); // Return the SQL exception message
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    private boolean isDuplicateEntry(Connection connection) throws SQLException {
        String selectQuery;
        if (!phoneTextField.getText().equals(originalPhone) && emailTextField.getText().equals(originalEmail)) {
            selectQuery = "SELECT * FROM `адміністратор` WHERE `Номер_телефону` = ?";
        } else if (phoneTextField.getText().equals(originalPhone) && !emailTextField.getText().equals(originalEmail)) {
            selectQuery = "SELECT * FROM `адміністратор` WHERE `E-mail` = ?";
        } else {
            selectQuery = "SELECT * FROM `адміністратор` WHERE `Номер_телефону` = ? OR `E-mail` = ?";
        }

        try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
            if (!phoneTextField.getText().equals(originalPhone)) {
                selectStatement.setString(1, phoneTextField.getText());
            }
            if (!emailTextField.getText().equals(originalEmail)) {
                selectStatement.setString(1, emailTextField.getText());
            }

            try (ResultSet resultSet = selectStatement.executeQuery()) {
                return resultSet.next();
            }
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

        return allFieldsFilled;
    }

    public void returnButton() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/autoschool/autoschool_javafx/settings.fxml"));
        Parent fxml = loader.load();

        SettingsWindow settingsWindow = loader.getController();
        settingsWindow.setCurrentRoleState(currentRoleState);
        settingsWindow.setAdminMenuWindow(adminMenuWindow);
        settingsWindow.updateLanguageIcon();

        adminMenuWindow.contentArea.getChildren().removeAll();
        adminMenuWindow.contentArea.getChildren().setAll(fxml);
    }
}

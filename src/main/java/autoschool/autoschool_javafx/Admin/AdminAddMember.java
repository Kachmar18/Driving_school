package autoschool.autoschool_javafx.Admin;

import autoschool.autoschool_javafx.*;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class AdminAddMember implements Localisation {
    public JFXButton addMemberButton, returnButton;
    public Text editMemebrsTitleText;
    public TextField lastNameTextField, firstNameTextField, secondNameTextField, phoneTextField, emailTextField;
    public Label namingLabel, dateBirthLabel, phoneLabel, emailLabel, sexLabel, categoryLabel, errorTipLabel;
    public DatePicker datePicker;
    public RadioButton SexMaleRadioButton, SexFemaleRadioButton;
    public ComboBox<String> categoryComboBox;
    public ToggleGroup sexRadioButtons;

    private AdminMenuWindow adminMenuWindow;
    private String sex;
    private LocalDate birthDate;
    private String sql_checkExistingMember, sql_insertMember, sql_updateMember, sql_checkDuplicate;

    public void setAdminMenuWindow(AdminMenuWindow adminMenuWindow) {this.adminMenuWindow = adminMenuWindow;}
    public void setCurrentLanguage() {
        translate();
    }
    public void translate() {
        namingLabel.setText(LanguageManager.getString("namingLabel"));
        dateBirthLabel.setText(LanguageManager.getString("dateBirthLabel"));
        phoneLabel.setText(LanguageManager.getString("phoneLabel"));
        emailLabel.setText(LanguageManager.getString("emailLabel"));
        sexLabel.setText(LanguageManager.getString("sexLabel"));
        categoryLabel.setText(LanguageManager.getString("categoryColumn"));

        errorTipLabel.setText(LanguageManager.getString("errorTipLabel"));
        returnButton.setText(LanguageManager.getString("returnButton"));
        addMemberButton.setText(LanguageManager.getString("editAccount"));

        lastNameTextField.setPromptText(LanguageManager.getString("lastNamePrompt"));
        firstNameTextField.setPromptText(LanguageManager.getString("firstNamePrompt"));
        secondNameTextField.setPromptText(LanguageManager.getString("secondNamePrompt"));
        datePicker.setPromptText(LanguageManager.getString("datePickerPrompt"));
        categoryComboBox.setPromptText(LanguageManager.getString("categoryComboBoxPrompt2"));
    }

    public void initialize() {
        CommonMethods.applyNameLimit(lastNameTextField);
        CommonMethods.applyNameLimit(firstNameTextField);
        CommonMethods.applySingleWordLimit(secondNameTextField);

        CommonMethods.applyPhoneFormat(phoneTextField);

        CommonMethods.setupDatePicker(datePicker);

        System.out.println("Value of selectMember in AdminSplit " + AdminSplitMembers.selectMember);

        switch (AdminSplitMembers.selectMember) {
            case 1 -> {
                sql_checkExistingMember = DataBase.sql_checkExistingInstructorAccountInDatabase;
                sql_insertMember = "INSERT INTO `інструктор`(`Ім'я`, `По-батькові`, `Прізвище`, `Дата_народження`, `Номер_телефону`, `E-mail`, `Стать`, `Категорія`) VALUES (?,?,?,?,?,?,?,?)";
                sql_updateMember = "UPDATE `інструктор` SET `Ім'я`=?,`По-батькові`=?,`Прізвище`=?,`Дата_народження`=?,`Номер_телефону`=?,`E-mail`=?,`Стать`=?,`Категорія`=? WHERE `Id_інструктор` = ?";
            }
            case 2 -> {
                sql_checkExistingMember = DataBase.sql_checkExistingStudentAccountInDatabase;
                sql_insertMember = "INSERT INTO `студент`(`Ім'я`, `По-батькові`, `Прізвище`, `Дата_народження`, `Номер_телефону`, `E-mail`, `Стать`, `Категорія`) VALUES (?,?,?,?,?,?,?,?)";
                sql_updateMember = "UPDATE `студент` SET `Ім'я`=?,`По-батькові`=?,`Прізвище`=?,`Дата_народження`=?,`Номер_телефону`=?,`E-mail`=?,`Стать`=?,`Категорія`=? WHERE `id_студент` = ?";
            }
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
    private void setSexRadioButtons(String sex) {
        if ("Жінка".equals(sex)) {
            SexFemaleRadioButton.setSelected(true);
        } else if ("Чоловік".equals(sex)) {
            SexMaleRadioButton.setSelected(true);
        }
    }
    public void addMemberButton() throws SQLException {
        if (areAllFieldsFilled()) {
            errorTipLabel.setVisible(false);

            if (!DataBase.isDatabaseConnected()) { // Check if the connection to the database is successful
                BasicValues.showErrorAlert(LanguageManager.getString("dbConnectionError"));
                return;
            }

            boolean userConfirmed = BasicValues.showConfirmationDialog(LanguageManager.getString("saveInfoAccountConfirmation"));

            if (userConfirmed) {
                sexValuesRadioButton();
                if (selectedMemberId != null){
                    String errorMessage = updateMemberIntoDatabase();
                    if (errorMessage == null) {
                        BasicValues.showConfirmationDialog(LanguageManager.getString("updateProfileSuccess"));
                    } else {
                        BasicValues.showErrorAlert(LanguageManager.getString("accountUpdateError") + "\n" + errorMessage);
                    }
                } else {
                    String errorMessage = insertMemberAccountToDatabase();

                    if (errorMessage == null) {
                        BasicValues.showConfirmationDialog(LanguageManager.getString("accountCreationSuccess"));
                    } else {
                        BasicValues.showErrorAlert(LanguageManager.getString("accountCreationError") + "\n" + errorMessage);
                    }
                }
            }
        } else {
            errorTipLabel.setVisible(true);
            BasicValues.showWarningAlert((LanguageManager.getString("fillAllFieldsWarning")));
        }
    }

    private String insertMemberAccountToDatabase() throws SQLException {
        Connection connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD);
        try{
            if (DataBase.isAccountAlreadyRegistered(connection, sql_checkExistingMember, phoneTextField, emailTextField)) {
                return LanguageManager.getString("checkRecordExistingError");
            }

            try (PreparedStatement statement = connection.prepareStatement(sql_insertMember)) {
                statement.setString(1, firstNameTextField.getText());
                statement.setString(2, secondNameTextField.getText());
                statement.setString(3, lastNameTextField.getText());
                statement.setString(4, String.valueOf(birthDate));
                statement.setString(5, phoneTextField.getText());
                statement.setString(6, emailTextField.getText());
                statement.setString(7, sex);
                statement.setString(8, categoryComboBox.getValue());

                int rowsAffected = statement.executeUpdate(); // Execute the update
                if (rowsAffected > 0) { // Check the result of the update
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

    private String selectedMemberId, originalPhone, originalEmail;
    private String updateMemberIntoDatabase() throws SQLException {
        Connection connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD);
        try{
            if (!phoneTextField.getText().equals(originalPhone) || !emailTextField.getText().equals(originalEmail)) {
                if (isDuplicateEntry(connection)) {
                    return LanguageManager.getString("checkRecordExistingError");
                }
            }

            try (PreparedStatement statement = connection.prepareStatement(sql_updateMember)) {
            statement.setString(1, firstNameTextField.getText());
            statement.setString(2, secondNameTextField.getText());
            statement.setString(3, lastNameTextField.getText());
            statement.setString(4, String.valueOf(birthDate));
            statement.setString(5, phoneTextField.getText());
            statement.setString(6, emailTextField.getText());
            statement.setString(7, sex);
            statement.setString(8, categoryComboBox.getValue());
            statement.setInt(9, Integer.parseInt(selectedMemberId));

                int rowsAffected = statement.executeUpdate(); // Execute the update
                if (rowsAffected > 0) { // Check the result of the update
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

    public void setEditMemberData(AdminHandleMembers.FullData memberData, String memberId) {
        this.selectedMemberId = memberId;
        if (memberData != null) {
            String selectedMemberFullName = memberData.getName();
            String[] member_names = selectedMemberFullName.split(" "); // Split the full name into first name and last name
            String member_firstName = member_names[0];
            String member_secondName = member_names[1];
            String member_lastName = member_names[2];

            lastNameTextField.setText(member_lastName);
            firstNameTextField.setText(member_firstName);
            secondNameTextField.setText(member_secondName);

            birthDate = LocalDate.parse(memberData.getDateBirth());
            datePicker.setValue(birthDate);

            CommonMethods.applyPhoneExistedFormat(phoneTextField);

            originalPhone = memberData.getNumber();
            originalEmail = memberData.getEmail();
            phoneTextField.setText(originalPhone);
            emailTextField.setText(originalEmail);

            setSexRadioButtons(memberData.getSex());

            categoryComboBox.setValue(memberData.getCategory());
        }
    }

    private boolean isDuplicateEntry(Connection connection) throws SQLException {
        if (!phoneTextField.getText().equals(originalPhone) && emailTextField.getText().equals(originalEmail)) {
            switch (AdminSplitMembers.selectMember) {
                case 1 -> sql_checkDuplicate = "SELECT * FROM `інструктор` WHERE `Номер_телефону` = ?";
                case 2 -> sql_checkDuplicate = "SELECT * FROM `студент` WHERE `Номер_телефону` = ?";
            }
        } else if (phoneTextField.getText().equals(originalPhone) && !emailTextField.getText().equals(originalEmail)) {
            switch (AdminSplitMembers.selectMember) {
                case 1 -> sql_checkDuplicate = "SELECT * FROM `інструктор` WHERE `E-mail` = ?";
                case 2 -> sql_checkDuplicate = "SELECT * FROM `студент` WHERE `E-mail` = ?";
            }
        } else {
            switch (AdminSplitMembers.selectMember) {
                case 1 -> sql_checkDuplicate = "SELECT * FROM `інструктор` WHERE `Номер_телефону` = ? OR `E-mail` = ?";
                case 2 -> sql_checkDuplicate = "SELECT * FROM `студент` WHERE `Номер_телефону` = ? OR `E-mail` = ?";
            }
        }
        try (PreparedStatement selectStatement = connection.prepareStatement(sql_checkDuplicate)) {
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
                lastNameTextField, firstNameTextField, secondNameTextField, phoneTextField, emailTextField};

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

    public void returnButton() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/autoschool/autoschool_javafx/Admin/admin-members.fxml"));
        Parent fxml = loader.load();

        AdminHandleMembers adminHandleMembers = loader.getController();
        adminHandleMembers.setAdminMenuWindow(adminMenuWindow); // Передача посилання на StudentMenuWindow
        adminHandleMembers.setCurrentLanguage();

        switch (AdminSplitMembers.selectMember){
            case 1 -> {
                adminHandleMembers.membersInfoLabel.setText(LanguageManager.getString("instructorsInfoLabel"));
                adminHandleMembers.addNewMemberButton.setText(LanguageManager.getString("addNewInstructorLabel"));
            }

            case 2 -> {
                adminHandleMembers.membersInfoLabel.setText(LanguageManager.getString("studentsInfoLabel"));
                adminHandleMembers.addNewMemberButton.setText(LanguageManager.getString("addNewStudentLabel"));
            }
        }

        adminMenuWindow.contentArea.getChildren().removeAll();
        adminMenuWindow.contentArea.getChildren().setAll(fxml);
    }
}

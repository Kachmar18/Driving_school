package autoschool.autoschool_javafx;

import autoschool.autoschool_javafx.Admin.AdminMenuWindow;
import autoschool.autoschool_javafx.Admin.AdminNewAccount;
import autoschool.autoschool_javafx.Instructor.InstructorMenuWindow;
import autoschool.autoschool_javafx.Instructor.InstructorNewAccount;
import autoschool.autoschool_javafx.Student.StudentMenuWindow;
import autoschool.autoschool_javafx.Student.StudentNewAccount;
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
import java.sql.*;

public class LoginWindow implements Localisation {
    public JFXButton forgetPasswordButton, createNewAccount, loginButton, returnButton;
    public ImageView roleIcon, passwordIcon;
    public Label loginTitleLabel, roleLabel, emailLabel, passwordLabel, namingLabel, errorTipLabel;
    public TextField emailTextField, lastNameTextField, firstNameTextField;
    public PasswordField passwordTextField;
    public ToggleButton showPasswordButton;

    private Parent root;
    private int currentRoleState;
    private boolean isPasswordForgotten = false;
    private String originalPasswordText = "";

    public void initialize() {
        CommonMethods.applyNameLimit(lastNameTextField);
        CommonMethods.applyNameLimit(firstNameTextField);
    }

    public void setCurrentLanguage() {
        translate();
    }

    public void setCurrentRoleState(int roleState) {
        currentRoleState = roleState;
        translate();
    }

    public void translate() {
        loginTitleLabel.setText(LanguageManager.getString("loginTitleLabel"));
        forgetPasswordButton.setText(LanguageManager.getString("forgetPasswordButton"));
        createNewAccount.setText(LanguageManager.getString("createNewAccount"));
        loginButton.setText(LanguageManager.getString("loginButton"));
        returnButton.setText(LanguageManager.getString("returnButton"));
        emailLabel.setText(LanguageManager.getString("emailLabel"));
        passwordLabel.setText(LanguageManager.getString("passwordLabel"));
        namingLabel.setText(LanguageManager.getString("namingLoginLabel"));
        errorTipLabel.setText(LanguageManager.getString("errorTipLabel"));

        lastNameTextField.setPromptText(LanguageManager.getString("lastNameLoginPrompt"));
        firstNameTextField.setPromptText(LanguageManager.getString("firstNameLoginPrompt"));

        CommonMethods.updateRoleLabelAndIcon(roleLabel, roleIcon, this.getClass());
    }

    public void showPasswordButton() {
        CommonMethods.showHidePassword(passwordTextField, passwordIcon, getClass());
    }

    public void forgetPasswordButton() {
        if (!isPasswordForgotten) {  // Save the original state
            originalPasswordText = passwordTextField.getText();

            namingLabel.setVisible(true); // Set the new state
            lastNameTextField.setVisible(true);
            firstNameTextField.setVisible(true);

            passwordLabel.setDisable(true);
            passwordTextField.setDisable(true);
            showPasswordButton.setDisable(true);

            passwordTextField.clear(); // Optionally, clear or modify the password field
        } else {
            namingLabel.setVisible(false); // Restore the original state
            lastNameTextField.setVisible(false);
            firstNameTextField.setVisible(false);

            passwordLabel.setDisable(false);
            passwordTextField.setDisable(false);
            showPasswordButton.setDisable(false);

            passwordTextField.setText(originalPasswordText); // Optionally, restore the original password text
        }
        isPasswordForgotten = !isPasswordForgotten; // Toggle the state for the next click
    }

    public void createNewAccount(ActionEvent event) throws IOException {
        switch (MainWindow.currentRoleState) {
            case 0 -> {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/autoschool/autoschool_javafx/student/student-new-account.fxml"));
                root = loader.load();

                StudentNewAccount studentNewAccount = loader.getController();
                studentNewAccount.setCurrentRoleState(currentRoleState);
                studentNewAccount.setCurrentLanguage();
            }
            case 1 -> {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/autoschool/autoschool_javafx/instructor/instructor-new-account.fxml"));
                root = loader.load();

                InstructorNewAccount instructorNewAccount = loader.getController();
                instructorNewAccount.setCurrentRoleState(currentRoleState);
                instructorNewAccount.setCurrentLanguage();
            }
            case 2 -> {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/autoschool/autoschool_javafx/admin/admin-new-account.fxml"));
                root = loader.load();

                AdminNewAccount adminNewAccount = loader.getController();
                adminNewAccount.setCurrentRoleState(currentRoleState);
                adminNewAccount.setCurrentLanguage();
            }
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void loginButton(ActionEvent event) throws IOException, SQLException {
        if (areAllFieldsFilled()) {
            errorTipLabel.setVisible(false);
            
            if (!DataBase.isDatabaseConnected()) { // Check if the connection to the database is successful
                BasicValues.showErrorAlert(LanguageManager.getString("dbConnectionError"));
                return;
            }

            boolean userConfirmed = BasicValues.showConfirmationDialog(LanguageManager.getString("loginInSystemConfirmation"));

            if (userConfirmed) {
                Connection connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD);

                switch (MainWindow.currentRoleState) {
                    case 0 -> {
                        String[] studentInfo = DataBase.loginAccount(connection, DataBase.sql_loginStudentAccount, emailTextField, passwordTextField, lastNameTextField, firstNameTextField, "id_студент");

                        if (studentInfo != null) {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/autoschool/autoschool_javafx/student/student-menu.fxml"));
                            root = loader.load();

                            StudentMenuWindow studentMenuWindow = loader.getController();
                            studentMenuWindow.setCurrentRoleState(currentRoleState);
                            studentMenuWindow.setCurrentLanguage();

                            studentMenuWindow.setStudentInfo(studentInfo[0], studentInfo[1], studentInfo[2]);
                        }
                    }
                    case 1 -> {
                        String[] instructorInfo = DataBase.loginAccount(connection, DataBase.sql_loginInstructorAccount, emailTextField, passwordTextField, lastNameTextField, firstNameTextField, "Id_інструктор");

                        if (instructorInfo != null) {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/autoschool/autoschool_javafx/instructor/instructor-menu.fxml"));
                            root = loader.load();

                            InstructorMenuWindow instructorMenuWindow = loader.getController();
                            instructorMenuWindow.setCurrentRoleState(currentRoleState);
                            instructorMenuWindow.setCurrentLanguage();

                            instructorMenuWindow.setInstructorInfo(instructorInfo[0], instructorInfo[1], instructorInfo[2]);
                        }
                    }
                    case 2 -> {
                        String[] adminInfo = DataBase.loginAccount(connection, DataBase.sql_loginAdminAccount, emailTextField, passwordTextField, lastNameTextField, firstNameTextField, "id_адміністратор");

                        if (adminInfo != null) {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/autoschool/autoschool_javafx/admin/admin-menu.fxml"));
                            root = loader.load();

                            AdminMenuWindow adminMenuWindow = loader.getController();
                            adminMenuWindow.setCurrentRoleState(currentRoleState);
                            adminMenuWindow.setCurrentLanguage();

                            adminMenuWindow.setAdminInfo(adminInfo[0], adminInfo[1], adminInfo[2]);
                        }
                    }
                }
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
        } else {
            errorTipLabel.setVisible(true);
            BasicValues.showWarningAlert((LanguageManager.getString("fillAllFieldsWarning")));
        }
    }

    private boolean areAllFieldsFilled() {                                             // * Перевірка заповненості полів
        TextField[] textFields;
        boolean allFieldsFilled = true;

        if (!isPasswordForgotten) {
            textFields = new TextField[]{emailTextField, passwordTextField};
        }
        else {
            textFields = new TextField[]{ emailTextField, lastNameTextField, firstNameTextField};
        }

        for (TextField textField : textFields) { // Перевірка, чи всі текстові поля заповнені
            if (textField.getText().trim().isEmpty()) {
                textField.setStyle(BasicValues.HIGHLIGHT_BORDERS);
                allFieldsFilled = false;
            } else {
                textField.setStyle(""); // Якщо поле заповнено, знімаємо стиль CSS
            }
        }

        emailTextField.setStyle(emailTextField.getText().matches(BasicValues.EMAIL_FORMAT) ? "" : BasicValues.HIGHLIGHT_BORDERS);

        if (isPasswordForgotten) {
            String lastName = lastNameTextField.getText().trim();
            boolean isLastNameValid = CommonMethods.isValidName(lastName);
            if (!isLastNameValid) {
                lastNameTextField.setStyle(BasicValues.HIGHLIGHT_BORDERS);
                allFieldsFilled = false;
            } else {
                lastNameTextField.setStyle("");
            }

            String firstName = firstNameTextField.getText().trim();
            boolean isFirstNameValid = CommonMethods.isValidName(firstName);
            if (!isFirstNameValid) {
                firstNameTextField.setStyle(BasicValues.HIGHLIGHT_BORDERS);
                allFieldsFilled = false;
            } else {
                firstNameTextField.setStyle("");
            }
        }

        return allFieldsFilled;
    }

    public void returnButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-window.fxml"));
        Parent root = loader.load();

        MainWindow mainWindow = loader.getController();
        mainWindow.setCurrentRoleState(currentRoleState);// Pass the current role state back

        mainWindow.updateLanguageIcon(); // Call the method to update the language icon

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}

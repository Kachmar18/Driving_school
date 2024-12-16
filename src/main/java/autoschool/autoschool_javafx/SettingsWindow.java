package autoschool.autoschool_javafx;

import autoschool.autoschool_javafx.Admin.AdminEditProfile;
import autoschool.autoschool_javafx.Admin.AdminMenuWindow;
import autoschool.autoschool_javafx.Instructor.InstructorEditProfile;
import autoschool.autoschool_javafx.Instructor.InstructorMenuWindow;
import autoschool.autoschool_javafx.Student.StudentEditProfile;
import autoschool.autoschool_javafx.Student.StudentMenuWindow;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.sql.*;
import java.util.Objects;

public class SettingsWindow implements Localisation{
    public Label accountLabel, languageLabel;
    public JFXButton editAccountButton, deleteAccountButton, languageButton;
    public ImageView switchLanguageIcon;

    public int currentRoleState;

    private StudentMenuWindow studentMenuWindow;
    private AdminMenuWindow adminMenuWindow;
    private InstructorMenuWindow instructorMenuWindow;

    public void setStudentMenuWindow(StudentMenuWindow menuWindow) {
        this.studentMenuWindow = menuWindow;
    }
    public void setAdminMenuWindow(AdminMenuWindow menuWindow) {
        this.adminMenuWindow = menuWindow;
    }
    public void setInstructorMenuWindow(InstructorMenuWindow menuWindow) {
        this.instructorMenuWindow = menuWindow;
    }

    public void setCurrentLanguage() {
        translate();
    }

    public void setCurrentRoleState(int roleState) {
        currentRoleState = roleState;
        translate();
    }

    public void translate() {
        accountLabel.setText(LanguageManager.getString("accountLabel"));
        languageLabel.setText(LanguageManager.getString("languageLabel"));

        editAccountButton.setText(LanguageManager.getString("editAccountButton"));
        deleteAccountButton.setText(LanguageManager.getString("deleteAccountButton"));
        languageButton.setText(LanguageManager.getString("languageButton"));
    }

    public void editAccountButton() throws IOException{
        if (!DataBase.isDatabaseConnected()) { // Check if the connection to the database is successful
            BasicValues.showErrorAlert(LanguageManager.getString("dbConnectionError"));
            return;
        }
        
        switch (MainWindow.currentRoleState) {
            case 0 -> {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/autoschool/autoschool_javafx/Student/student-edit-profile.fxml"));
                Parent fxml = loader.load();

                StudentEditProfile studentEditProfile = loader.getController();
                studentEditProfile.setCurrentRoleState(currentRoleState);
                studentEditProfile.setCurrentLanguage();
                studentEditProfile.setStudentMenuWindow(studentMenuWindow);

                studentMenuWindow.contentArea.getChildren().removeAll();
                studentMenuWindow.contentArea.getChildren().setAll(fxml);
            }
            case 1 -> {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/autoschool/autoschool_javafx/Instructor/instructor-edit-profile.fxml"));
                Parent fxml = loader.load();

                InstructorEditProfile instructorEditProfile = loader.getController();
                instructorEditProfile.setCurrentRoleState(currentRoleState);
                instructorEditProfile.setCurrentLanguage();
                instructorEditProfile.setInstructorMenuWindow(instructorMenuWindow);

                instructorMenuWindow.contentArea.getChildren().removeAll();
                instructorMenuWindow.contentArea.getChildren().setAll(fxml);
            }
            case 2 -> {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/autoschool/autoschool_javafx/admin/admin-edit-profile.fxml"));
                Parent fxml = loader.load();

                AdminEditProfile adminEditProfile = loader.getController();
                adminEditProfile.setCurrentRoleState(currentRoleState);
                adminEditProfile.setCurrentLanguage();
                adminEditProfile.setAdminMenuWindow(adminMenuWindow);

                adminMenuWindow.contentArea.getChildren().removeAll();
                adminMenuWindow.contentArea.getChildren().setAll(fxml);
            }
        }
    }

    public void deleteAccountButton(ActionEvent event) throws SQLException, IOException {
        if (!DataBase.isDatabaseConnected()) { // Check if the connection to the database is successful
            BasicValues.showErrorAlert(LanguageManager.getString("dbConnectionError"));
            return;
        }

        boolean userConfirmed = BasicValues.showConfirmationDialog(LanguageManager.getString("deleteAccountConfirmation"));

        if (userConfirmed) {
            Pair<String, String> credentials = showCredentialsInputDialog();
            if (credentials != null) {
                if (isValidEmail(credentials.getKey())) { // Перевірка чи всі поля заповнені та, чи email підходить під регулярний вираз
                    String email = credentials.getKey();
                    String password = credentials.getValue();

                    boolean continueDelete = BasicValues.showConfirmationDialog(LanguageManager.getString("deactivationAccountConfirmation")); // Відображення повідомлення про неможливість відновлення
                    if (continueDelete){
                        String errorMessage = null;
                        switch (MainWindow.currentRoleState) {
                            case 0 -> errorMessage = deleteAccountFromDatabase(MainWindow.currentRoleState, email, password, DataBase.sql_deleteStudentAccount);
                            case 1 -> errorMessage = deleteAccountFromDatabase(MainWindow.currentRoleState, email, password, DataBase.sql_deleteInstructorAccount);
                            case 2 -> errorMessage = deleteAccountFromDatabase(MainWindow.currentRoleState, email, password, DataBase.sql_deleteAdminAccount);
                        }

                        if (errorMessage == null) {
                            BasicValues.showInformationDialog(LanguageManager.getString("deleteAccountSuccess"));
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/autoschool/autoschool_javafx/main-window.fxml"));
                            Parent root = loader.load();

                            MainWindow mainWindow = loader.getController();
                            mainWindow.setCurrentRoleState(currentRoleState);// Pass the current role state back
                            mainWindow.updateLanguageIcon(); // Call the method to update the language icon

                            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                            Scene scene = new Scene(root);
                            stage.setScene(scene);
                            stage.show();
                        } else {
                            BasicValues.showErrorAlert(errorMessage);
                        }
                    }
                } else {
                    BasicValues.showInformationDialog(LanguageManager.getString("emailFormat"));
                }
            }
        }
    }

    public static String deleteAccountFromDatabase(int currentRoleState, String email, String password, String sql_statement) throws SQLException {
        Connection connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD);
        String errorMessage = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql_statement)) {
            switch (currentRoleState) {
                case 0 -> preparedStatement.setString(1, String.valueOf(StudentMenuWindow.id_student));
                case 1 -> preparedStatement.setString(1, String.valueOf(InstructorMenuWindow.id_instructor));
                case 2 -> preparedStatement.setString(1, String.valueOf(AdminMenuWindow.id_admin));
            }
            preparedStatement.setString(2, String.valueOf(email));
            preparedStatement.setString(3, String.valueOf(password));

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                errorMessage = LanguageManager.getString("deleteFailedError");
            }
        } catch (SQLException e) {
            errorMessage = LanguageManager.getString("dbConnectionError");
            e.printStackTrace();
        }
        return errorMessage;
    }

    private Pair<String, String> showCredentialsInputDialog() {
        Dialog<Pair<String, String>> dialog = new Dialog<>(); // Створення діалогового вікна для введення email та паролю
        dialog.setTitle(LanguageManager.getString("enteringData"));
        dialog.setHeaderText(LanguageManager.getString("inputEmailPassword"));

        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow(); // Встановлення іконки для діалогового вікна
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream(BasicValues.ICON))));

        TextField emailField = new TextField(); // Створення полів для введення email та паролю
        PasswordField passwordField = new PasswordField();

        GridPane grid = new GridPane(); // Створення макета GridPane для розміщення полів
        grid.add(new Label("Email:"), 0, 0);
        grid.add(emailField, 1, 0);
        grid.add(new Label(LanguageManager.getString("passwordField")), 0, 1);
        grid.add(passwordField, 1, 1);

        ColumnConstraints columnConstraints = new ColumnConstraints(); // Налаштування ColumnConstraints для розтягування полів до кінця вікна
        columnConstraints.setFillWidth(true);
        columnConstraints.setHgrow(javafx.scene.layout.Priority.ALWAYS);
        grid.getColumnConstraints().add(columnConstraints);

        dialog.getDialogPane().setContent(grid); // Додавання полів до діалогового вікна

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL); // Додавання кнопок "OK" та "Скасувати"

        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK); // Отримання кнопки "OK" та налаштування події для неї
        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            if (emailField.getText().isEmpty() || passwordField.getText().isEmpty()) { // Перевірка чи всі поля заповнені
                event.consume();  // Відміна події
                BasicValues.showInformationDialog(LanguageManager.getString("fillAllFieldsWarning"));
                return;
            }

            if (!isValidEmail(emailField.getText())) { // Перевірка валідності email за допомогою регулярного виразу
                event.consume();  // Відміна події
                BasicValues.showInformationDialog(LanguageManager.getString("emailFormat"));
            }
        });

        dialog.setResultConverter(dialogButton -> { // Налаштування результатів для кнопки "OK"
            if (dialogButton == ButtonType.OK) { // Перевірка чи всі поля заповнені
                if (emailField.getText().isEmpty() || passwordField.getText().isEmpty() || !isValidEmail(emailField.getText())) {
                    return null;  // Повертаємо null, щоб вказати, що дані не повинні бути використані
                }
                return new Pair<>(emailField.getText(), passwordField.getText());
            }
            return null;
        });

        return dialog.showAndWait().orElse(null); // Виклик діалогового вікна і отримання результату
    }

    private boolean isValidEmail(String email) {
        return email.matches(BasicValues.EMAIL_FORMAT);
    }

    public void switchLanguageButton() {
        String newLanguage = "ua";
        if (LanguageManager.getCurrentLanguage().equals("ua")) {
            newLanguage = "en";
        }
        LanguageManager.init(newLanguage);
        translate();
        updateLanguageIcon();

        if (studentMenuWindow != null) { // Оновлення мови у головному вікні
            studentMenuWindow.setCurrentLanguage();
        }else if (instructorMenuWindow != null) { // Оновлення мови у головному вікні
            instructorMenuWindow.setCurrentLanguage();
        }else if (adminMenuWindow != null) { // Оновлення мови у головному вікні
            adminMenuWindow.setCurrentLanguage();
        }
    }

    public void updateLanguageIcon() {
        String iconPath = switch (LanguageManager.getCurrentLanguage()) {
            case "ua" -> BasicValues.UKRAINE_FLAG_ICON;
            case "en" -> BasicValues.ENGLAND_FLAG_ICON;
            default -> throw new IllegalStateException("Unexpected value: " + LanguageManager.getCurrentLanguage());
        };
        switchLanguageIcon.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(iconPath))));
    }
}

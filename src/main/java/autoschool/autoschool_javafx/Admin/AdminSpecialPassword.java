package autoschool.autoschool_javafx.Admin;

import autoschool.autoschool_javafx.BasicValues;
import autoschool.autoschool_javafx.DataBase;
import autoschool.autoschool_javafx.LanguageManager;
import autoschool.autoschool_javafx.Localisation;
import com.jfoenix.controls.JFXButton;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AdminSpecialPassword implements Localisation {
    public JFXButton createNewSpecialPasswordButton;
    public Label specialPasswordLabel, titlePasswords;
    public VBox vBox;
    public ScrollPane scrollPane;
    public AdminMenuWindow adminMenuWindow;


    public void setAdminMenuWindow(AdminMenuWindow adminMenuWindow) {
        this.adminMenuWindow = adminMenuWindow;
    }
    public void setCurrentLanguage() {
        translate();
    }
    public void translate() {
        createNewSpecialPasswordButton.setText(LanguageManager.getString("createNewSpecialPasswordButton"));
        specialPasswordLabel.setText(LanguageManager.getString("specialPasswordTitleLabel"));
        titlePasswords.setText(LanguageManager.getString("titlePasswords"));
    }

    public void initialize() {
        displayPasswords();

        scrollPane.setContent(vBox);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }

    public int selectedId;
    private final List<String[]> passwordRecords = new ArrayList<>();

    private void displayPasswords() {
        try (Connection connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD)) {
            String sql = "SELECT `id_вхід`, `Пароль_адміністратор`, `Пароль_інструктор`, `Дата_створення` FROM `спец_вхід` ";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        int specialPasswordsId = resultSet.getInt("id_вхід");
                        String password_Admin = resultSet.getString("Пароль_адміністратор");
                        String password_Instructor = resultSet.getString("Пароль_інструктор");
                        String creationDate = resultSet.getString("Дата_створення");

                        System.out.println(specialPasswordsId +"  " + password_Admin + "          " + password_Instructor + "          " + creationDate);

                        String labelText = "               " + password_Admin + "                                   " + password_Instructor + "                               " + creationDate;
                        Label label = new Label(labelText);
                        label.getStyleClass().add("label");

                        ImageView deleteIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(BasicValues.DELETE_RECORD_ICON))));

                        deleteIcon.setFitWidth(27);
                        deleteIcon.setFitHeight(27);

                        HBox iconBox = new HBox(deleteIcon);
                        iconBox.setAlignment(Pos.CENTER_RIGHT); // Set alignment to right
                        iconBox.setSpacing(25);
                        label.setGraphic(iconBox);

                        vBox.getChildren().add(label);

                        String[] record = {String.valueOf(specialPasswordsId), password_Admin, password_Instructor, creationDate};
                        passwordRecords.add(record);

                        deleteIcon.setOnMouseClicked(event -> {
                            int index = vBox.getChildren().indexOf(label);
                            String[] selectedRecord = passwordRecords.get(index);
                            selectedId = Integer.parseInt(selectedRecord[0]);

                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle(LanguageManager.getString("deleteDialogConfirmation"));
                            alert.setHeaderText(null);
                            alert.setContentText(LanguageManager.getString("deletePasswordConfirmation"));

                            alert.showAndWait().ifPresent(response -> {
                                if (response == ButtonType.OK) {
                                    boolean deleteResult = deleteRecord(selectedId);
                                    if (deleteResult) {
                                        BasicValues.showInformationDialog(LanguageManager.getString("deletePasswordSuccess"));

                                        vBox.getChildren().remove(label);
                                        vBox.getChildren().clear();
                                        displayPasswords();
                                    } else {
                                        BasicValues.showErrorAlert(LanguageManager.getString("deleteDeletingError"));
                                    }
                                }
                            });

                            vBox.getChildren().clear();
                            displayPasswords();
                        });
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Метод для видалення запису з бази даних, повертає true якщо видалення успішне, false в іншому випадку
    private boolean deleteRecord(int id) {
        try (Connection connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD)) {
            String countSql = "SELECT COUNT(*) AS count FROM `спец_вхід`";
            try (PreparedStatement countStatement = connection.prepareStatement(countSql)) {
                try (ResultSet countResult = countStatement.executeQuery()) {
                    countResult.next();
                    int count = countResult.getInt("count");

                    if (count <= 2) {
                        BasicValues.showErrorAlert(LanguageManager.getString("deletePasswordError"));
                        return false;
                    }
                }
            }

            String deleteSql = "DELETE FROM `спец_вхід` WHERE `id_вхід` = ?";
            try (PreparedStatement statement = connection.prepareStatement(deleteSql)) {
                statement.setInt(1, id);
                int affectedRows = statement.executeUpdate();
                System.out.println("Affected rows: " + affectedRows);
                return affectedRows > 0; // Повертаємо true, якщо хоча б один рядок було видалено
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Помилка видалення
        }
    }

    public void createNewSpecialPasswordButton() {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setWidth(400);
        dialog.setHeight(100);
        dialog.setTitle(LanguageManager.getString("createSpecialPasswordLabel"));
        dialog.setHeaderText(LanguageManager.getString("enterSpecialPasswordLabel"));

        ButtonType createButtonType = new ButtonType(LanguageManager.getString("createButton"), ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        TextField adminPasswordField = new TextField();
        adminPasswordField.setPromptText(LanguageManager.getString("adminPasswordField"));
        adminPasswordField.setPrefWidth(300);
        TextField instructorPasswordField = new TextField();
        instructorPasswordField.setPromptText(LanguageManager.getString("instructorPasswordField"));
        instructorPasswordField.setPrefWidth(300);

        Node createButton = dialog.getDialogPane().lookupButton(createButtonType);
        createButton.setDisable(true);
        adminPasswordField.textProperty().addListener((observable, oldValue, newValue) -> createButton.setDisable(newValue.trim().isEmpty() || newValue.length() != 14 || instructorPasswordField.getText().trim().isEmpty() || instructorPasswordField.getText().length() != 14));
        instructorPasswordField.textProperty().addListener((observable, oldValue, newValue) -> createButton.setDisable(newValue.trim().isEmpty() || newValue.length() != 14 || adminPasswordField.getText().trim().isEmpty() || adminPasswordField.getText().length() != 14));

        GridPane grid = new GridPane();
        grid.setPrefWidth(500);
        grid.setPrefHeight(120);
        grid.add(new Label(LanguageManager.getString("adminPasswordLabel")), 0, 0);
        grid.add(adminPasswordField, 1, 0);
        grid.add(new Label(LanguageManager.getString("instructorPasswordLabel")), 0, 1);
        grid.add(instructorPasswordField, 1, 1);
        dialog.getDialogPane().setContent(grid);

        dialog.getDialogPane().heightProperty().addListener((observable, oldValue, newValue) -> grid.setPrefHeight(newValue.doubleValue()));
        dialog.getDialogPane().widthProperty().addListener((observable, oldValue, newValue) -> grid.setPrefWidth(newValue.doubleValue()));

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                return new Pair<>(adminPasswordField.getText(), instructorPasswordField.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();
        result.ifPresent(pair -> {
            insertPasswordRecord(pair.getKey(), pair.getValue());
            vBox.getChildren().clear();
            displayPasswords();
        });
    }
    private void insertPasswordRecord(String adminPassword, String instructorPassword) {
        try (Connection connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD)) {
            String sql = "INSERT INTO `спец_вхід` (`Пароль_адміністратор`, `Пароль_інструктор`) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, adminPassword);
                statement.setString(2, instructorPassword);
                int affectedRows = statement.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("Новий спеціальний пароль успішно створено.");
                    BasicValues.showInformationDialog(LanguageManager.getString("createNewPasswordRecordInformation"));
                } else {
                    System.out.println("Не вдалося створити новий спеціальний пароль.");
                    BasicValues.showErrorAlert(LanguageManager.getString("createNewPasswordRecordFailedError"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
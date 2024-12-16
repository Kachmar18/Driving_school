package autoschool.autoschool_javafx.Admin;

import autoschool.autoschool_javafx.*;
import com.jfoenix.controls.JFXButton;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class AdminMenuWindow implements Localisation {
    public Label menuLabel, initialsLabel, searchTipLabel;
    public JFXButton coursesMenuButton, practiceMenuButton, membersMenuButton, statisticsMenuButton;

    public Pane sliderPane;
    public ImageView roleIcon;
    public StackPane contentArea;
    public JFXButton coursesSliderButton, practiceSliderButton, membersSliderButton, specialPasswordSliderButton, statisticsSliderButton, settingsSliderButton, exitSliderButton;

    private int currentRoleState;
    public static int id_admin;
    public static String firstname, lastname;

    public void setAdminInfo(String id, String name, String surname) {
        initialsLabel.setText(name + " " + surname); // Use the provided information to update the UI
        id_admin = Integer.parseInt(id);
        firstname = name;
        lastname = surname;
        BasicValues.showInformationDialog(initialsLabel.getText()+ LanguageManager.getString("loginSuccess") + "\n"
                +  LanguageManager.getString("loginWelcome") );
    }

    public void updateAdminInfo(String name, String surname) {
        initialsLabel.setText(name + " " + surname);
    }
    public void setCurrentLanguage() {
        translate();
    }
    public void setCurrentRoleState(int roleState) {
        currentRoleState = roleState;
        translate();
    }

    public void translate() {
        menuLabel.setText(LanguageManager.getString("menuLabel"));
        coursesMenuButton.setText(LanguageManager.getString("coursesMenuButton"));
        practiceMenuButton.setText(LanguageManager.getString("practiceMenuButton"));
        membersMenuButton.setText(LanguageManager.getString("membersMenuButton"));
        statisticsMenuButton.setText(LanguageManager.getString("statisticsMenuButton"));

        coursesSliderButton.setText(LanguageManager.getString("coursesSlider2Button"));
        practiceSliderButton.setText(LanguageManager.getString("practiceSliderButton"));
        membersSliderButton.setText(LanguageManager.getString("membersSliderButton"));
        specialPasswordSliderButton.setText(LanguageManager.getString("specialPasswordSliderButton"));
        statisticsSliderButton.setText(LanguageManager.getString("statisticsMenuButton"));
        settingsSliderButton.setText(LanguageManager.getString("settingsButton"));
        exitSliderButton.setText(LanguageManager.getString("exitButton"));
        searchTipLabel.setText(LanguageManager.getString("searchTipLabel"));
    }

    public void initialize() throws IOException {
        //handleSettings();
    }

    private void resetButtonStyles() {
        coursesMenuButton.getStyleClass().remove("menu-item-clicked");
        practiceMenuButton.getStyleClass().remove("menu-item-clicked");
        membersMenuButton.getStyleClass().remove("menu-item-clicked");
        statisticsMenuButton.getStyleClass().remove("menu-item-clicked");

        coursesSliderButton.getStyleClass().remove("sidebar-item-clicked");
        practiceSliderButton.getStyleClass().remove("sidebar-item-clicked");
        membersSliderButton.getStyleClass().remove("sidebar-item-clicked");
        specialPasswordSliderButton.getStyleClass().remove("sidebar-item-clicked");
        statisticsSliderButton.getStyleClass().remove("sidebar-item-clicked");
        settingsSliderButton.getStyleClass().remove("sidebar-item-clicked");
    }

    public void showSliderPane() { // Slide in to the original position
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.6), sliderPane);
        transition.setToX(270);
        transition.play();
    }

    public void hideSliderPane() { // Slide out to the left
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.6), sliderPane);
        transition.setToX(-sliderPane.getWidth());
        transition.play();
    }

    public void handleCourses() throws IOException {
        resetButtonStyles();

        coursesMenuButton.getStyleClass().add("menu-item-clicked");
        coursesSliderButton.getStyleClass().add("sidebar-item-clicked");

        if (!DataBase.isDatabaseConnected()) { // Check if the connection to the database is successful
            BasicValues.showErrorAlert(LanguageManager.getString("dbConnectionError"));
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/autoschool/autoschool_javafx/admin/admin-courses.fxml"));
        Parent fxml = loader.load();

        AdminCourses adminCourses = loader.getController();
        adminCourses.setAdminMenuWindow(this); // Передача посилання на StudentMenuWindow
        adminCourses.setCurrentLanguage();

        contentArea.getChildren().removeAll();
        contentArea.getChildren().setAll(fxml);
    }

    public void handlePractice() throws IOException {
        resetButtonStyles();

        practiceMenuButton.getStyleClass().add("menu-item-clicked");
        practiceSliderButton.getStyleClass().add("sidebar-item-clicked");

        if (!DataBase.isDatabaseConnected()) { // Check if the connection to the database is successful
            BasicValues.showErrorAlert(LanguageManager.getString("dbConnectionError"));
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/autoschool/autoschool_javafx/Admin/admin-practice.fxml"));
        Parent fxml = loader.load();

        AdminPractice adminPractice = loader.getController();
        adminPractice.setAdminMenuWindow(this); // Передача посилання на StudentMenuWindow
        adminPractice.setCurrentLanguage();

        contentArea.getChildren().removeAll();
        contentArea.getChildren().setAll(fxml);
    }

    public void handleSplitMembers() throws IOException {
        resetButtonStyles();

        membersMenuButton.getStyleClass().add("menu-item-clicked");
        membersSliderButton.getStyleClass().add("sidebar-item-clicked");

        if (!DataBase.isDatabaseConnected()) { // Check if the connection to the database is successful
            BasicValues.showErrorAlert(LanguageManager.getString("dbConnectionError"));
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/autoschool/autoschool_javafx/admin/admin-split-members.fxml"));
        Parent fxml = loader.load();

        AdminSplitMembers adminSplitMembers = loader.getController();
        adminSplitMembers.setAdminMenuWindow(this); // Передача посилання на StudentMenuWindow
        adminSplitMembers.setCurrentLanguage();

        contentArea.getChildren().removeAll();
        contentArea.getChildren().setAll(fxml);
    }

    public void handleSpecialPassword() throws IOException {
        resetButtonStyles();

        specialPasswordSliderButton.getStyleClass().add("sidebar-item-clicked");

        if (!DataBase.isDatabaseConnected()) { // Check if the connection to the database is successful
            BasicValues.showErrorAlert(LanguageManager.getString("dbConnectionError"));
            return;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/autoschool/autoschool_javafx/admin/admin-special-password.fxml"));
        Parent fxml = loader.load();

        AdminSpecialPassword adminSpecialPassword = loader.getController();
        adminSpecialPassword.setAdminMenuWindow(this); // Передача посилання на StudentMenuWindow
        adminSpecialPassword.setCurrentLanguage();

        contentArea.getChildren().removeAll();
        contentArea.getChildren().setAll(fxml);
    }

    public void handleStatistics() throws IOException {
        resetButtonStyles();

        statisticsMenuButton.getStyleClass().add("menu-item-clicked");
        statisticsSliderButton.getStyleClass().add("sidebar-item-clicked");

        if (!DataBase.isDatabaseConnected()) { // Check if the connection to the database is successful
            BasicValues.showErrorAlert(LanguageManager.getString("dbConnectionError"));
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/autoschool/autoschool_javafx/admin/admin-statistics.fxml"));
        Parent fxml = loader.load();

        AdminStatistics adminStatistics = loader.getController();
        adminStatistics.setAdminMenuWindow(this); // Передача посилання на StudentMenuWindow
        adminStatistics.setCurrentLanguage();

        contentArea.getChildren().removeAll();
        contentArea.getChildren().setAll(fxml);
    }

    public void handleSettings() throws IOException {
        resetButtonStyles();

        settingsSliderButton.getStyleClass().add("sidebar-item-clicked");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/autoschool/autoschool_javafx/settings.fxml"));
        Parent fxml = loader.load();

        SettingsWindow settingsWindow = loader.getController();
        settingsWindow.setCurrentRoleState(currentRoleState);
        settingsWindow.setAdminMenuWindow(this); // Передача посилання на StudentMenuWindow
        settingsWindow.setCurrentLanguage();

        settingsWindow.updateLanguageIcon(); // Call the method to update the language icon

        contentArea.getChildren().removeAll();
        contentArea.getChildren().setAll(fxml);
    }

    public void handleExit(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/autoschool/autoschool_javafx/main-window.fxml"));
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

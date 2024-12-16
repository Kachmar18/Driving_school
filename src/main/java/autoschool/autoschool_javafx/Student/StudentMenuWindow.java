package autoschool.autoschool_javafx.Student;

import autoschool.autoschool_javafx.*;
import autoschool.autoschool_javafx.TestsPDR.TestsSplitWindow;
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

public class StudentMenuWindow implements Localisation {
    public Label menuLabel, initialsLabel, searchTipLabel;
    public JFXButton coursesMenuButton, scheduleMenuButton, testsMenuButton;
    public StackPane contentArea;
    public JFXButton coursesSliderButton, scheduleSliderButton, testsSliderButton, settingsSliderButton, exitSliderButton;
    public ImageView roleIcon;
    public Pane sliderPane;

    private int currentRoleState;
    public static int id_student;
    public static String firstname, lastname;

    public void setStudentInfo(String id, String name, String surname) {
        initialsLabel.setText(name + " " + surname); // Use the provided information to update the UI, e.g., set labels
        id_student = Integer.parseInt(id);
        firstname = name;
        lastname = surname;
        BasicValues.showInformationDialog(initialsLabel.getText()+ LanguageManager.getString("loginSuccess") + "\n"
                +  LanguageManager.getString("loginWelcome") );
    }

    public void updateStudentInfo(String name, String surname) {
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
        scheduleMenuButton.setText(LanguageManager.getString("scheduleButton"));
        testsMenuButton.setText(LanguageManager.getString("testsMenuButton"));

        coursesSliderButton.setText(LanguageManager.getString("coursesSlider2Button"));
        scheduleSliderButton.setText(LanguageManager.getString("scheduleButton"));
        testsSliderButton.setText(LanguageManager.getString("testsSliderButton"));
        settingsSliderButton.setText(LanguageManager.getString("settingsButton"));
        exitSliderButton.setText(LanguageManager.getString("exitButton"));
        searchTipLabel.setText(LanguageManager.getString("searchTipLabel"));
    }

    public void initialize() throws IOException {
        //handleCourses();
    }

    private void resetButtonStyles() {
        coursesMenuButton.getStyleClass().remove("menu-item-clicked");
        scheduleMenuButton.getStyleClass().remove("menu-item-clicked");
        testsMenuButton.getStyleClass().remove("menu-item-clicked");

        coursesSliderButton.getStyleClass().remove("sidebar-item-clicked");
        scheduleSliderButton.getStyleClass().remove("sidebar-item-clicked");
        testsSliderButton.getStyleClass().remove("sidebar-item-clicked");
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

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/autoschool/autoschool_javafx/Student/student-courses.fxml"));
        Parent fxml = loader.load();

        StudentCourses studentCourses = loader.getController();
        studentCourses.setStudentMenuWindow(this); // Передача посилання на StudentMenuWindow
        studentCourses.setCurrentLanguage();

        contentArea.getChildren().removeAll();
        contentArea.getChildren().setAll(fxml);
    }

    public void handleSchedule() throws IOException {
        resetButtonStyles();

        scheduleMenuButton.getStyleClass().add("menu-item-clicked");
        scheduleSliderButton.getStyleClass().add("sidebar-item-clicked");

        if (!DataBase.isDatabaseConnected()) { // Check if the connection to the database is successful
            BasicValues.showErrorAlert(LanguageManager.getString("dbConnectionError"));
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/autoschool/autoschool_javafx/Student/student-schedule.fxml"));
        Parent fxml = loader.load();

        StudentSchedule studentSchedule = loader.getController();
        studentSchedule.setStudentMenuWindow(this); // Передача посилання на StudentMenuWindow
        studentSchedule.setCurrentLanguage();

        contentArea.getChildren().removeAll();
        contentArea.getChildren().setAll(fxml);
    }

    public void handleTests() throws IOException {
        resetButtonStyles();

        testsMenuButton.getStyleClass().add("menu-item-clicked");
        testsSliderButton.getStyleClass().add("sidebar-item-clicked");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/autoschool/autoschool_javafx/TestsPDR/tests-split.fxml"));
        Parent fxml = loader.load();

        TestsSplitWindow testsSplitWindow = loader.getController();
        testsSplitWindow.setCurrentRoleState(currentRoleState);
        testsSplitWindow.setStudentMenuWindow(this);
        testsSplitWindow.setCurrentLanguage();

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
        settingsWindow.setStudentMenuWindow(this);
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

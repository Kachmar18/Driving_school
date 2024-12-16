package autoschool.autoschool_javafx.TestsPDR;

import autoschool.autoschool_javafx.BasicValues;
import autoschool.autoschool_javafx.Instructor.InstructorMenuWindow;
import autoschool.autoschool_javafx.LanguageManager;
import autoschool.autoschool_javafx.Localisation;
import autoschool.autoschool_javafx.MainWindow;
import autoschool.autoschool_javafx.Student.StudentMenuWindow;
import com.jfoenix.controls.JFXButton;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class TestsSplitWindow extends Application implements Localisation {
    public JFXButton passExamButton, trainingRideTestButton, trafficControlTestButton;
    public Label testPDRTopics, questions_20, questions_14, questions_7;
    public Hyperlink hyperlinkTests;

    public int currentRoleState;

    private StudentMenuWindow studentMenuWindow;
    private InstructorMenuWindow instructorMenuWindow;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/autoschool/autoschool_javafx/TestsPDR/tests-split.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle(BasicValues.NAME);
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream(BasicValues.ICON))));
        primaryStage.setScene(new Scene(root, BasicValues.WINDOW_WIDTH, BasicValues.WINDOW_HEIGHT));

        primaryStage.setResizable(false); // * Відключаємо можливість масштабування вікна

        primaryStage.show();
    }

    public void setStudentMenuWindow(StudentMenuWindow menuWindow) {
        this.studentMenuWindow = menuWindow;
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
        passExamButton.setText(LanguageManager.getString("passExamButton"));
        trainingRideTestButton.setText(LanguageManager.getString("trainingRideTestButton"));
        trafficControlTestButton.setText(LanguageManager.getString("trafficControlTestButton"));

        testPDRTopics.setText(LanguageManager.getString("testPDRTopics"));
        questions_20.setText(LanguageManager.getString("questions_20"));
        questions_14.setText(LanguageManager.getString("questions_14"));
        questions_7.setText(LanguageManager.getString("questions_7"));

        hyperlinkTests.setText(LanguageManager.getString("hyperlinkTests"));
    }
    
    public void passExamButton() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/autoschool/autoschool_javafx/TestsPDR/test-exam.fxml"));
        Parent fxml = loader.load();

        ExamTestWindow examTestWindow = loader.getController();
        examTestWindow.setCurrentRoleState(currentRoleState);
        examTestWindow.setCurrentLanguage();

        switch (MainWindow.currentRoleState) {
            case 0 -> {
                examTestWindow.setStudentMenuWindow(studentMenuWindow);

                studentMenuWindow.contentArea.getChildren().removeAll();
                studentMenuWindow.contentArea.getChildren().setAll(fxml);
            }
            case 1 -> {
                examTestWindow.setInstructorMenuWindow(instructorMenuWindow);

                instructorMenuWindow.contentArea.getChildren().removeAll();
                instructorMenuWindow.contentArea.getChildren().setAll(fxml);
            }
        }
    }

    public void trainingRideTestButton() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/autoschool/autoschool_javafx/TestsPDR/test-training-ride.fxml"));
        Parent fxml = loader.load();

        TrainingRideTestWindow trainingRideTestWindow = loader.getController();
        trainingRideTestWindow.setCurrentRoleState(currentRoleState);
        trainingRideTestWindow.setCurrentLanguage();

        switch (MainWindow.currentRoleState) {
            case 0 -> {
                trainingRideTestWindow.setStudentMenuWindow(studentMenuWindow);

                studentMenuWindow.contentArea.getChildren().removeAll();
                studentMenuWindow.contentArea.getChildren().setAll(fxml);
            }
            case 1 -> {
                trainingRideTestWindow.setInstructorMenuWindow(instructorMenuWindow);

                instructorMenuWindow.contentArea.getChildren().removeAll();
                instructorMenuWindow.contentArea.getChildren().setAll(fxml);
            }
        }
    }

    public void trafficControlTestButton() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/autoschool/autoschool_javafx/TestsPDR/test-traffic-control.fxml"));
        Parent fxml = loader.load();

        TrafficControlTestWindow trafficControlTestWindow  = loader.getController();
        trafficControlTestWindow.setCurrentRoleState(currentRoleState);
        trafficControlTestWindow.setCurrentLanguage();

        switch (MainWindow.currentRoleState) {
            case 0 -> {
                trafficControlTestWindow .setStudentMenuWindow(studentMenuWindow);

                studentMenuWindow.contentArea.getChildren().removeAll();
                studentMenuWindow.contentArea.getChildren().setAll(fxml);
            }
            case 1 -> {
                trafficControlTestWindow .setInstructorMenuWindow(instructorMenuWindow);

                instructorMenuWindow.contentArea.getChildren().removeAll();
                instructorMenuWindow.contentArea.getChildren().setAll(fxml);
            }
        }
    }

    public void openHyperlinkTests() {
        this.getHostServices().showDocument(BasicValues.WEBSITE_TESTS_PDR);
    }
}

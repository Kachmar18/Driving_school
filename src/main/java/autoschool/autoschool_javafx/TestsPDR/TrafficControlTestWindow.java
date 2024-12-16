package autoschool.autoschool_javafx.TestsPDR;

import autoschool.autoschool_javafx.*;
import autoschool.autoschool_javafx.Instructor.InstructorMenuWindow;
import autoschool.autoschool_javafx.Student.StudentMenuWindow;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrafficControlTestWindow implements Localisation {
    public Label trafficControlTestLabel, errorTipLabel;
    public ToggleGroup task_1, task_2, task_3, task_4, task_5, task_6, task_7;
    public JFXButton doneTestButton, returnButton;
    public Label questionLabelTC_1, questionLabelTC_2, questionLabelTC_3, questionLabelTC_4, questionLabelTC_5, questionLabelTC_6, questionLabelTC_7;
    public JFXRadioButton task1TC_1RB, task1TC_2RB, task1TC_3RB, task1TC_4RB, task1TC_5RB, task2TC_1RB, task2TC_2RB, task2TC_3RB, task3TC_1RB, task3TC_2RB, task3TC_3RB,
            task4TC_1RB, task4TC_2RB, task4TC_3RB, task5TC_1RB, task5TC_2RB, task5TC_3RB, task6TC_1RB, task6TC_2RB, task6TC_3RB, task6TC_4RB, task6TC_5RB, task7TC_1RB, task7TC_2RB, task7TC_3RB, task7TC_4RB, task7TC_5RB;

    private final Map<String, String> correctAnswers = new HashMap<>();

    private StudentMenuWindow studentMenuWindow;
    private InstructorMenuWindow instructorMenuWindow;
    private int currentRoleState;

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
        trafficControlTestLabel.setText(LanguageManager.getString("trafficControlTestLabel"));
        errorTipLabel.setText(LanguageManager.getString("errorTipLabel"));
        doneTestButton.setText(LanguageManager.getString("doneTestButton"));
        returnButton.setText(LanguageManager.getString("returnButton"));

        questionLabelTC_1.setText(LanguageManager.getString("questionLabelTC_1"));
        questionLabelTC_2.setText(LanguageManager.getString("questionLabelTC_2"));
        questionLabelTC_3.setText(LanguageManager.getString("questionLabelTC_3"));
        questionLabelTC_4.setText(LanguageManager.getString("questionLabelTC_4"));
        questionLabelTC_5.setText(LanguageManager.getString("questionLabelTC_5"));
        questionLabelTC_6.setText(LanguageManager.getString("questionLabelTC_6"));
        questionLabelTC_7.setText(LanguageManager.getString("questionLabelTC_7"));

        task1TC_1RB.setText(LanguageManager.getString("task1TC_1RB"));
        task1TC_2RB.setText(LanguageManager.getString("task1TC_2RB"));
        task1TC_3RB.setText(LanguageManager.getString("task1TC_3RB"));
        task1TC_4RB.setText(LanguageManager.getString("task1TC_4RB"));
        task1TC_5RB.setText(LanguageManager.getString("task1TC_5RB"));
        task2TC_1RB.setText(LanguageManager.getString("task2TC_1RB"));
        task2TC_2RB.setText(LanguageManager.getString("task2TC_2RB"));
        task2TC_3RB.setText(LanguageManager.getString("task2TC_3RB"));
        task3TC_1RB.setText(LanguageManager.getString("task3TC_1RB"));
        task3TC_2RB.setText(LanguageManager.getString("task3TC_2RB"));
        task3TC_3RB.setText(LanguageManager.getString("task3TC_3RB"));
        task4TC_1RB.setText(LanguageManager.getString("task4TC_1RB"));
        task4TC_2RB.setText(LanguageManager.getString("task4TC_2RB"));
        task4TC_3RB.setText(LanguageManager.getString("task4TC_3RB"));
        task5TC_1RB.setText(LanguageManager.getString("task5TC_1RB"));
        task5TC_2RB.setText(LanguageManager.getString("task5TC_2RB"));
        task5TC_3RB.setText(LanguageManager.getString("task5TC_3RB"));
        task6TC_1RB.setText(LanguageManager.getString("task6TC_1RB"));
        task6TC_2RB.setText(LanguageManager.getString("task6TC_2RB"));
        task6TC_3RB.setText(LanguageManager.getString("task6TC_3RB"));
        task6TC_4RB.setText(LanguageManager.getString("task6TC_4RB"));
        task6TC_5RB.setText(LanguageManager.getString("task6TC_5RB"));
        task7TC_1RB.setText(LanguageManager.getString("task7TC_1RB"));
        task7TC_2RB.setText(LanguageManager.getString("task7TC_2RB"));
        task7TC_3RB.setText(LanguageManager.getString("task7TC_3RB"));
        task7TC_4RB.setText(LanguageManager.getString("task7TC_4RB"));
        task7TC_5RB.setText(LanguageManager.getString("task7TC_5RB"));
    }

    public TrafficControlTestWindow() {
        correctAnswers.put("task_1", LanguageManager.getString("task1TC_1RB"));
        correctAnswers.put("task_2", LanguageManager.getString("task2TC_1RB"));
        correctAnswers.put("task_3", LanguageManager.getString("task3TC_2RB"));
        correctAnswers.put("task_4", LanguageManager.getString("task4TC_2RB"));
        correctAnswers.put("task_5", LanguageManager.getString("task5TC_1RB"));
        correctAnswers.put("task_6", LanguageManager.getString("task6TC_5RB"));
        correctAnswers.put("task_7", LanguageManager.getString("task7TC_1RB"));
    }

    public void doneTestButton() {
        if (areAllFieldsFilled()) {
            errorTipLabel.setVisible(false);
        } else {
            errorTipLabel.setVisible(true);
            BasicValues.showWarningAlert((LanguageManager.getString("fillAllFieldsWarning")));
            return;
        }

        int correctCount = 0;

        for (Map.Entry<String, String> entry : correctAnswers.entrySet()) {
            Toggle selectedToggle = switch (entry.getKey()) {
                case "task_1" -> task_1.getSelectedToggle();
                case "task_2" -> task_2.getSelectedToggle();
                case "task_3" -> task_3.getSelectedToggle();
                case "task_4" -> task_4.getSelectedToggle();
                case "task_5" -> task_5.getSelectedToggle();
                case "task_6" -> task_6.getSelectedToggle();
                case "task_7" -> task_7.getSelectedToggle();
                default -> null;
            };
            if (selectedToggle != null) {
                RadioButton selectedAnswer = (RadioButton) selectedToggle;
                if (selectedAnswer.getText().equals(entry.getValue())) {
                    correctCount++;
                }
            }
        }

        System.out.println("Number of correct answers: " + correctCount);
        BasicValues.showInformationDialog(LanguageManager.getString("resultAlert") + correctCount);

        String name_Test = "Нерегульовані перехрестя";
        int max_score = 7;
        DataBase.insertResultIntoDatabase(name_Test, max_score, correctCount);
    }
    private boolean areAllFieldsFilled () {
        List<ToggleGroup> toggleGroups = new ArrayList<>();
        toggleGroups.add(task_1);
        toggleGroups.add(task_2);
        toggleGroups.add(task_3);
        toggleGroups.add(task_4);
        toggleGroups.add(task_5);
        toggleGroups.add(task_6);
        toggleGroups.add(task_7);


        boolean allFieldsFilled = true;
        for (ToggleGroup toggleGroup : toggleGroups) {
            if (toggleGroup.getSelectedToggle() == null) {
                CommonMethods.applyRedBorderToRadioButtons(toggleGroup);
                allFieldsFilled = false;
            } else {
                CommonMethods.clearRedBorderFromRadioButtons(toggleGroup);
            }
        }
        return allFieldsFilled;
    }

    public void returnButton () throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/autoschool/autoschool_javafx/TestsPDR/tests-split.fxml"));
        Parent fxml = loader.load();

        TestsSplitWindow testsSplitWindow = loader.getController();
        testsSplitWindow.setCurrentRoleState(currentRoleState);

        switch (MainWindow.currentRoleState) {
            case 0 -> {
                testsSplitWindow.setStudentMenuWindow(studentMenuWindow);

                studentMenuWindow.contentArea.getChildren().removeAll();
                studentMenuWindow.contentArea.getChildren().setAll(fxml);
            }
            case 1 -> {
                testsSplitWindow.setInstructorMenuWindow(instructorMenuWindow);

                instructorMenuWindow.contentArea.getChildren().removeAll();
                instructorMenuWindow.contentArea.getChildren().setAll(fxml);
            }
        }
    }
}

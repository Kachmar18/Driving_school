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

public class TrainingRideTestWindow implements Localisation {
    public Label trainingRideTestLabel, errorTipLabel;
    public ToggleGroup task_1, task_2, task_3, task_4, task_5, task_6, task_7, task_8, task_9, task_10, task_11, task_12, task_13, task_14;
    public JFXButton doneTestButton, returnButton;

    public Label questionLabelTR_1, questionLabelTR_2, questionLabelTR_3, questionLabelTR_4, questionLabelTR_5, questionLabelTR_6, questionLabelTR_7, questionLabelTR_8, questionLabelTR_9, questionLabelTR_10, questionLabelTR_11, questionLabelTR_12, questionLabelTR_13, questionLabelTR_14;
    public JFXRadioButton task1_allowRB, task1_banRB, task2_allowRB, task2_banRB, task2_3RB, task3_1RB, task3_2RB, task3_3RB, task3_4RB, task4_1RB, task4_2RB, task4_3RB, task4_4RB, task4_5RB,
            task5_1RB, task5_2RB, task5_3RB, task5_4RB, task6_1RB, task6_2RB, task6_3RB, task7_1RB, task7_2RB, task7_3RB, task8_1RB, task8_2RB, task8_3RB, task8_4RB, task8_5RB,
            task9_1RB, task9_2RB, task9_3RB, task10_1RB, task10_2RB, task10_3RB, task10_4RB, task11_1RB, task11_2RB, task12_1RB, task12_2RB, task12_3RB, task12_4RB, task12_5RB,
            task13_1RB, task13_2RB, task13_3RB, task14_allowRB, task14_banRB;

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
        trainingRideTestLabel.setText(LanguageManager.getString("trainingRideTestLabel"));
        errorTipLabel.setText(LanguageManager.getString("errorTipLabel"));
        doneTestButton.setText(LanguageManager.getString("doneTestButton"));
        returnButton.setText(LanguageManager.getString("returnButton"));

        questionLabelTR_1.setText(LanguageManager.getString("questionLabelTR_1"));
        questionLabelTR_2.setText(LanguageManager.getString("questionLabelTR_2"));
        questionLabelTR_3.setText(LanguageManager.getString("questionLabelTR_3"));
        questionLabelTR_4.setText(LanguageManager.getString("questionLabelTR_4"));
        questionLabelTR_5.setText(LanguageManager.getString("questionLabelTR_5"));
        questionLabelTR_6.setText(LanguageManager.getString("questionLabelTR_6"));
        questionLabelTR_7.setText(LanguageManager.getString("questionLabelTR_7"));
        questionLabelTR_8.setText(LanguageManager.getString("questionLabelTR_8"));
        questionLabelTR_9.setText(LanguageManager.getString("questionLabelTR_9"));
        questionLabelTR_10.setText(LanguageManager.getString("questionLabelTR_10"));
        questionLabelTR_11.setText(LanguageManager.getString("questionLabelTR_11"));
        questionLabelTR_12.setText(LanguageManager.getString("questionLabelTR_12"));
        questionLabelTR_13.setText(LanguageManager.getString("questionLabelTR_13"));
        questionLabelTR_14.setText(LanguageManager.getString("questionLabelTR_14"));
        task1_allowRB.setText(LanguageManager.getString("allowRadio"));
        task1_banRB.setText(LanguageManager.getString("banRadio"));
        task2_allowRB.setText(LanguageManager.getString("allowRadio"));
        task2_banRB.setText(LanguageManager.getString("banRadio"));
        task2_3RB.setText(LanguageManager.getString("task2_3RB"));
        task3_1RB.setText(LanguageManager.getString("task3_1RB"));
        task3_2RB.setText(LanguageManager.getString("task3_2RB"));
        task3_3RB.setText(LanguageManager.getString("task3_3RB"));
        task3_4RB.setText(LanguageManager.getString("task3_4RB"));
        task4_1RB.setText(LanguageManager.getString("task4_1RB"));
        task4_2RB.setText(LanguageManager.getString("task4_2RB"));
        task4_3RB.setText(LanguageManager.getString("task4_3RB"));
        task4_4RB.setText(LanguageManager.getString("task4_4RB"));
        task4_5RB.setText(LanguageManager.getString("task4_5RB"));
        task5_1RB.setText(LanguageManager.getString("task5_1RB"));
        task5_2RB.setText(LanguageManager.getString("task5_2RB"));
        task5_3RB.setText(LanguageManager.getString("task5_3RB"));
        task5_4RB.setText(LanguageManager.getString("task5_4RB"));
        task6_1RB.setText(LanguageManager.getString("task6_1RB"));
        task6_2RB.setText(LanguageManager.getString("task6_2RB"));
        task6_3RB.setText(LanguageManager.getString("task6_3RB"));
        task7_1RB.setText(LanguageManager.getString("task7_1RB"));
        task7_2RB.setText(LanguageManager.getString("task7_2RB"));
        task7_3RB.setText(LanguageManager.getString("task7_3RB"));
        task8_1RB.setText(LanguageManager.getString("task8_1RB"));
        task8_2RB.setText(LanguageManager.getString("task8_2RB"));
        task8_3RB.setText(LanguageManager.getString("task8_3RB"));
        task8_4RB.setText(LanguageManager.getString("task8_4RB"));
        task8_5RB.setText(LanguageManager.getString("task8_5RB"));
        task9_1RB.setText(LanguageManager.getString("task9_1RB"));
        task9_2RB.setText(LanguageManager.getString("task9_2RB"));
        task9_3RB.setText(LanguageManager.getString("task9_3RB"));
        task10_1RB.setText(LanguageManager.getString("task10_1RB"));
        task10_2RB.setText(LanguageManager.getString("task10_2RB"));
        task10_3RB.setText(LanguageManager.getString("task10_3RB"));
        task10_4RB.setText(LanguageManager.getString("task10_4RB"));
        task11_1RB.setText(LanguageManager.getString("task11_1RB"));
        task11_2RB.setText(LanguageManager.getString("task11_2RB"));
        task12_1RB.setText(LanguageManager.getString("task12_1RB"));
        task12_2RB.setText(LanguageManager.getString("task12_2RB"));
        task12_3RB.setText(LanguageManager.getString("task12_3RB"));
        task12_4RB.setText(LanguageManager.getString("task12_4RB"));
        task12_5RB.setText(LanguageManager.getString("task12_5RB"));
        task13_1RB.setText(LanguageManager.getString("task13_1RB"));
        task13_2RB.setText(LanguageManager.getString("task13_2RB"));
        task13_3RB.setText(LanguageManager.getString("task13_3RB"));
        task14_allowRB.setText(LanguageManager.getString("allowRadio"));
        task14_banRB.setText(LanguageManager.getString("banRadio"));
    }

    public TrainingRideTestWindow() {
        correctAnswers.put("task_1", LanguageManager.getString("banRadio"));
        correctAnswers.put("task_2", LanguageManager.getString("banRadio"));
        correctAnswers.put("task_3", LanguageManager.getString("task3_2RB"));
        correctAnswers.put("task_4", LanguageManager.getString("task4_3RB"));
        correctAnswers.put("task_5", LanguageManager.getString("task5_3RB"));
        correctAnswers.put("task_6", LanguageManager.getString("task6_1RB"));
        correctAnswers.put("task_7", LanguageManager.getString("task7_3RB"));
        correctAnswers.put("task_8", LanguageManager.getString("task8_5RB"));
        correctAnswers.put("task_9", LanguageManager.getString("task9_1RB"));
        correctAnswers.put("task_10", LanguageManager.getString("task10_4RB"));
        correctAnswers.put("task_11", LanguageManager.getString("task11_2RB"));
        correctAnswers.put("task_12", LanguageManager.getString("task12_2RB"));
        correctAnswers.put("task_13", LanguageManager.getString("task13_1RB"));
        correctAnswers.put("task_14", LanguageManager.getString("banRadio"));
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
                case "task_8" -> task_8.getSelectedToggle();
                case "task_9" -> task_9.getSelectedToggle();
                case "task_10" -> task_10.getSelectedToggle();
                case "task_11" -> task_11.getSelectedToggle();
                case "task_12" -> task_12.getSelectedToggle();
                case "task_13" -> task_13.getSelectedToggle();
                case "task_14" -> task_14.getSelectedToggle();
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

        String name_Test = "Навчальна їзда";
        int max_score = 14;
        DataBase.insertResultIntoDatabase(name_Test, max_score, correctCount);
    }

    private boolean areAllFieldsFilled() {
        List<ToggleGroup> toggleGroups = new ArrayList<>();
        toggleGroups.add(task_1);
        toggleGroups.add(task_2);
        toggleGroups.add(task_3);
        toggleGroups.add(task_4);
        toggleGroups.add(task_5);
        toggleGroups.add(task_6);
        toggleGroups.add(task_7);
        toggleGroups.add(task_8);
        toggleGroups.add(task_9);
        toggleGroups.add(task_10);
        toggleGroups.add(task_11);
        toggleGroups.add(task_12);
        toggleGroups.add(task_13);
        toggleGroups.add(task_14);

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

    public void returnButton() throws IOException {
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

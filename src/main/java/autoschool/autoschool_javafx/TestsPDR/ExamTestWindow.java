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

public class ExamTestWindow implements Localisation {
    public Label examTestLabel, errorTipLabel;
    public ToggleGroup task_1, task_2, task_3, task_4, task_5, task_6, task_7, task_8, task_9, task_10, task_11, task_12, task_13, task_14, task_15, task_16, task_17, task_18, task_19, task_20;
    public JFXButton doneTestButton, returnButton;

    public Label questionLabelEM_1, questionLabelEM_2, questionLabelEM_3, questionLabelEM_4, questionLabelEM_5, questionLabelEM_6, questionLabelEM_7, questionLabelEM_8,
            questionLabelEM_9, questionLabelEM_10, questionLabelEM_11, questionLabelEM_12, questionLabelEM_13, questionLabelEM_14, questionLabelEM_15, questionLabelEM_16,
            questionLabelEM_17, questionLabelEM_18, questionLabelEM_19, questionLabelEM_20;

    private final Map<String, String> correctAnswers = new HashMap<>();
    public JFXRadioButton EMRB_task1_1, EMRB_task1_2, EMRB_task1_3, EMRB_task2_1, EMRB_task2_2, EMRB_task2_3, EMRB_task3_1, EMRB_task3_2, EMRB_task3_3,
            EMRB_task4_1, EMRB_task4_2, EMRB_task4_3, EMRB_task4_4, EMRB_task5_1, EMRB_task5_2, EMRB_task5_3, EMRB_task6_1, EMRB_task6_2, EMRB_task6_3,
            EMRB_task7_1, EMRB_task7_2, EMRB_task7_3, EMRB_task8_1, EMRB_task8_2, EMRB_task8_3, EMRB_task8_4, EMRB_task9_1, EMRB_task9_2, EMRB_task10_1, EMRB_task10_2,
            EMRB_task11_1, EMRB_task11_2, EMRB_task11_3, EMRB_task11_4, EMRB_task12_1, EMRB_task12_2, EMRB_task12_3, EMRB_task12_4, EMRB_task12_5,
            EMRB_task13_1, EMRB_task13_2, EMRB_task13_3, EMRB_task13_4, EMRB_task14_1, EMRB_task14_2, EMRB_task14_3,  EMRB_task15_1, EMRB_task15_2, EMRB_task15_3, EMRB_task15_4,
            EMRB_task16_1, EMRB_task16_2, EMRB_task16_3, EMRB_task17_1, EMRB_task17_2, EMRB_task17_3, EMRB_task18_1, EMRB_task18_2, EMRB_task19_1, EMRB_task19_2, EMRB_task19_3,
            EMRB_task20_1, EMRB_task20_2, EMRB_task20_3;

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
        examTestLabel.setText(LanguageManager.getString("examTestLabel"));
        errorTipLabel.setText(LanguageManager.getString("errorTipLabel"));
        doneTestButton.setText(LanguageManager.getString("doneTestButton"));
        returnButton.setText(LanguageManager.getString("returnButton"));

        questionLabelEM_1.setText(LanguageManager.getString("questionLabelEM_1"));
        questionLabelEM_2.setText(LanguageManager.getString("questionLabelEM_2"));
        questionLabelEM_3.setText(LanguageManager.getString("questionLabelEM_3"));
        questionLabelEM_4.setText(LanguageManager.getString("questionLabelEM_4"));
        questionLabelEM_5.setText(LanguageManager.getString("questionLabelEM_5"));
        questionLabelEM_6.setText(LanguageManager.getString("questionLabelEM_6"));
        questionLabelEM_7.setText(LanguageManager.getString("questionLabelEM_7"));
        questionLabelEM_8.setText(LanguageManager.getString("questionLabelEM_8"));
        questionLabelEM_9.setText(LanguageManager.getString("questionLabelEM_9"));
        questionLabelEM_10.setText(LanguageManager.getString("questionLabelEM_10"));
        questionLabelEM_11.setText(LanguageManager.getString("questionLabelEM_11"));
        questionLabelEM_12.setText(LanguageManager.getString("questionLabelEM_12"));
        questionLabelEM_13.setText(LanguageManager.getString("questionLabelEM_13"));
        questionLabelEM_14.setText(LanguageManager.getString("questionLabelEM_14"));
        questionLabelEM_15.setText(LanguageManager.getString("questionLabelEM_15"));
        questionLabelEM_16.setText(LanguageManager.getString("questionLabelEM_16"));
        questionLabelEM_17.setText(LanguageManager.getString("questionLabelEM_17"));
        questionLabelEM_18.setText(LanguageManager.getString("questionLabelEM_18"));
        questionLabelEM_19.setText(LanguageManager.getString("questionLabelEM_19"));
        questionLabelEM_20.setText(LanguageManager.getString("questionLabelEM_20"));

        EMRB_task1_1.setText(LanguageManager.getString("EMRB_task1_1"));
        EMRB_task1_2.setText(LanguageManager.getString("EMRB_task1_2"));
        EMRB_task1_3.setText(LanguageManager.getString("EMRB_task1_3"));
        EMRB_task2_1.setText(LanguageManager.getString("EMRB_task2_1"));
        EMRB_task2_2.setText(LanguageManager.getString("EMRB_task2_2"));
        EMRB_task2_3.setText(LanguageManager.getString("EMRB_task2_3"));
        EMRB_task3_1.setText(LanguageManager.getString("EMRB_task3_1"));
        EMRB_task3_2.setText(LanguageManager.getString("EMRB_task3_2"));
        EMRB_task3_3.setText(LanguageManager.getString("EMRB_task3_3"));
        EMRB_task4_1.setText(LanguageManager.getString("EMRB_task4_1"));
        EMRB_task4_2.setText(LanguageManager.getString("EMRB_task4_2"));
        EMRB_task4_3.setText(LanguageManager.getString("EMRB_task4_3"));
        EMRB_task4_4.setText(LanguageManager.getString("EMRB_task4_4"));
        EMRB_task5_1.setText(LanguageManager.getString("EMRB_task5_1"));
        EMRB_task5_2.setText(LanguageManager.getString("EMRB_task5_2"));
        EMRB_task5_3.setText(LanguageManager.getString("EMRB_task5_3"));
        EMRB_task6_1.setText(LanguageManager.getString("EMRB_task6_1"));
        EMRB_task6_2.setText(LanguageManager.getString("EMRB_task6_2"));
        EMRB_task6_3.setText(LanguageManager.getString("EMRB_task6_3"));
        EMRB_task7_1.setText(LanguageManager.getString("EMRB_task7_1"));
        EMRB_task7_2.setText(LanguageManager.getString("EMRB_task7_2"));
        EMRB_task7_3.setText(LanguageManager.getString("EMRB_task7_3"));
        EMRB_task8_1.setText(LanguageManager.getString("EMRB_task8_1"));
        EMRB_task8_2.setText(LanguageManager.getString("EMRB_task8_2"));
        EMRB_task8_3.setText(LanguageManager.getString("EMRB_task8_3"));
        EMRB_task8_4.setText(LanguageManager.getString("EMRB_task8_4"));
        EMRB_task9_1.setText(LanguageManager.getString("EMRB_task9_1"));
        EMRB_task9_2.setText(LanguageManager.getString("EMRB_task9_2"));
        EMRB_task10_1.setText(LanguageManager.getString("EMRB_task10_1"));
        EMRB_task10_2.setText(LanguageManager.getString("EMRB_task10_2"));

        EMRB_task11_1.setText(LanguageManager.getString("EMRB_task11_1"));
        EMRB_task11_2.setText(LanguageManager.getString("EMRB_task11_2"));
        EMRB_task11_3.setText(LanguageManager.getString("EMRB_task11_3"));
        EMRB_task11_4.setText(LanguageManager.getString("EMRB_task11_4"));
        EMRB_task12_1.setText(LanguageManager.getString("EMRB_task12_1"));
        EMRB_task12_2.setText(LanguageManager.getString("EMRB_task12_2"));
        EMRB_task12_3.setText(LanguageManager.getString("EMRB_task12_3"));
        EMRB_task12_4.setText(LanguageManager.getString("EMRB_task12_4"));
        EMRB_task12_5.setText(LanguageManager.getString("EMRB_task12_5"));
        EMRB_task13_1.setText(LanguageManager.getString("EMRB_task13_1"));
        EMRB_task13_2.setText(LanguageManager.getString("EMRB_task13_2"));
        EMRB_task13_3.setText(LanguageManager.getString("EMRB_task13_3"));
        EMRB_task13_4.setText(LanguageManager.getString("EMRB_task13_4"));
        EMRB_task14_1.setText(LanguageManager.getString("EMRB_task14_1"));
        EMRB_task14_2.setText(LanguageManager.getString("EMRB_task14_2"));
        EMRB_task14_3.setText(LanguageManager.getString("EMRB_task14_3"));
        EMRB_task15_1.setText(LanguageManager.getString("EMRB_task15_1"));
        EMRB_task15_2.setText(LanguageManager.getString("EMRB_task15_2"));
        EMRB_task15_3.setText(LanguageManager.getString("EMRB_task15_3"));
        EMRB_task15_4.setText(LanguageManager.getString("EMRB_task15_4"));
        EMRB_task16_1.setText(LanguageManager.getString("EMRB_task16_1"));
        EMRB_task16_2.setText(LanguageManager.getString("EMRB_task16_2"));
        EMRB_task16_3.setText(LanguageManager.getString("EMRB_task16_3"));
        EMRB_task17_1.setText(LanguageManager.getString("EMRB_task17_1"));
        EMRB_task17_2.setText(LanguageManager.getString("EMRB_task17_2"));
        EMRB_task17_3.setText(LanguageManager.getString("EMRB_task17_3"));
        EMRB_task18_1.setText(LanguageManager.getString("EMRB_task18_1"));
        EMRB_task18_2.setText(LanguageManager.getString("EMRB_task18_2"));
        EMRB_task19_1.setText(LanguageManager.getString("EMRB_task19_1"));
        EMRB_task19_2.setText(LanguageManager.getString("EMRB_task19_2"));
        EMRB_task19_3.setText(LanguageManager.getString("EMRB_task19_3"));
        EMRB_task20_1.setText(LanguageManager.getString("EMRB_task20_1"));
        EMRB_task20_2.setText(LanguageManager.getString("EMRB_task20_2"));
        EMRB_task20_3.setText(LanguageManager.getString("EMRB_task20_3"));
    }

    public ExamTestWindow() {
        correctAnswers.put("task_1", LanguageManager.getString("EMRB_task1_3"));
        correctAnswers.put("task_2", LanguageManager.getString("EMRB_task2_3"));
        correctAnswers.put("task_3", LanguageManager.getString("EMRB_task3_3"));
        correctAnswers.put("task_4", LanguageManager.getString("EMRB_task4_4"));
        correctAnswers.put("task_5", LanguageManager.getString("EMRB_task5_3"));
        correctAnswers.put("task_6", LanguageManager.getString("EMRB_task6_3"));
        correctAnswers.put("task_7", LanguageManager.getString("EMRB_task7_3"));
        correctAnswers.put("task_8", LanguageManager.getString("EMRB_task8_2"));
        correctAnswers.put("task_9", LanguageManager.getString("EMRB_task9_2"));
        correctAnswers.put("task_10", LanguageManager.getString("EMRB_task10_2"));
        correctAnswers.put("task_11", LanguageManager.getString("EMRB_task11_1"));
        correctAnswers.put("task_12", LanguageManager.getString("EMRB_task12_2"));
        correctAnswers.put("task_13", LanguageManager.getString("EMRB_task13_4"));
        correctAnswers.put("task_14", LanguageManager.getString("EMRB_task14_2"));
        correctAnswers.put("task_15", LanguageManager.getString("EMRB_task15_4"));
        correctAnswers.put("task_16", LanguageManager.getString("EMRB_task16_2"));
        correctAnswers.put("task_17", LanguageManager.getString("EMRB_task17_1"));
        correctAnswers.put("task_18", LanguageManager.getString("EMRB_task18_1"));
        correctAnswers.put("task_19", LanguageManager.getString("EMRB_task19_1"));
        correctAnswers.put("task_20", LanguageManager.getString("EMRB_task20_1"));
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
                case "task_15" -> task_15.getSelectedToggle();
                case "task_16" -> task_16.getSelectedToggle();
                case "task_17" -> task_17.getSelectedToggle();
                case "task_18" -> task_18.getSelectedToggle();
                case "task_19" -> task_19.getSelectedToggle();
                case "task_20" -> task_20.getSelectedToggle();
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

        String name_Test = "Іспит";
        int max_score = 20;
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
        toggleGroups.add(task_15);
        toggleGroups.add(task_16);
        toggleGroups.add(task_17);
        toggleGroups.add(task_18);
        toggleGroups.add(task_19);
        toggleGroups.add(task_20);

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

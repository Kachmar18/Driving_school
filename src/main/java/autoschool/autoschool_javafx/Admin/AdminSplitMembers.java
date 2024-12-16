package autoschool.autoschool_javafx.Admin;

import autoschool.autoschool_javafx.LanguageManager;
import autoschool.autoschool_javafx.Localisation;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public class AdminSplitMembers implements Localisation {
    public JFXButton instructorsInfoOptionButton, studentsInfoOptionButton;
    private AdminMenuWindow adminMenuWindow;

    public static int selectMember;

    public void setAdminMenuWindow(AdminMenuWindow adminMenuWindow) {this.adminMenuWindow = adminMenuWindow;}
    public void setCurrentLanguage() {
        translate();
    }
    public void translate() {
        instructorsInfoOptionButton.setText(LanguageManager.getString("instructorsInfoOptionButton"));
        studentsInfoOptionButton.setText(LanguageManager.getString("amountTheoryColumn"));
    }

    public void initialize() {
    }

    public void handleOptionButtonClicked(ActionEvent event) {
        if (event.getSource() == instructorsInfoOptionButton) {
            selectMember = 1; // інструктор
        } else if (event.getSource() == studentsInfoOptionButton) {
            selectMember = 2; // студент
        }
        try {
            goToSelectedMember();
        } catch (IOException e) {
            e.printStackTrace(); // обробка помилок при переході на вибраного члена
        }
    }

    public void goToSelectedMember() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/autoschool/autoschool_javafx/Admin/admin-members.fxml"));
        Parent fxml = loader.load();

        AdminHandleMembers adminHandleMembers = loader.getController();
        adminHandleMembers.setAdminMenuWindow(adminMenuWindow);
        adminHandleMembers.setCurrentLanguage();

        switch (selectMember){
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

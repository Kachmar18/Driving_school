package autoschool.autoschool_javafx;

import com.jfoenix.controls.JFXButton;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainWindow extends Application implements Localisation {
    public JFXButton enterSystemButton, switchLanguageButton, exitProgramButton;
    public Label mainTitleLabel, roleLabel;
    public ImageView roleIcon, switchLanguageIcon;

    public static int currentRoleState = 0; // * Початковий стан ролі

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-window.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle(BasicValues.NAME);
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream(BasicValues.ICON))));
        primaryStage.setScene(new Scene(root, BasicValues.WINDOW_WIDTH, BasicValues.WINDOW_HEIGHT));

        //primaryStage.setFullScreen(true);

        primaryStage.setResizable(false); // * Відключаємо можливість масштабування вікна

        LanguageManager.init("ua");
        LanguageManager.registerWindow(this);

        primaryStage.show();
    }

    public void setCurrentRoleState(int roleState) {
        currentRoleState = roleState;
        translate();
    }

    public void handleRoleIconClick() {
        currentRoleState = (currentRoleState + 1) % 3; // 3 стани
        translate();
    }

    public void switchLanguageButton() {
        String newLanguage = "ua";
        if (LanguageManager.getCurrentLanguage().equals("ua")) {
            newLanguage = "en";
        }
        LanguageManager.init(newLanguage);
        translate();
        updateLanguageIcon();
    }

    public void updateLanguageIcon() {
        String iconPath = switch (LanguageManager.getCurrentLanguage()) {
            case "ua" -> BasicValues.UKRAINE_FLAG_ICON;
            case "en" -> BasicValues.ENGLAND_FLAG_ICON;
            default -> throw new IllegalStateException("Unexpected value: " + LanguageManager.getCurrentLanguage());
        };
        switchLanguageIcon.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(iconPath))));
    }

    public void translate() {
        mainTitleLabel.setText(LanguageManager.getString("mainTitleLabel"));
        enterSystemButton.setText(LanguageManager.getString("enterSystemButton"));
        switchLanguageButton.setText(LanguageManager.getString("switchLanguageButton"));
        exitProgramButton.setText(LanguageManager.getString("exitProgramButton"));

        CommonMethods.updateRoleLabelAndIcon( roleLabel, roleIcon, this.getClass());
    }

    public void enterSystemButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/autoschool/autoschool_javafx/login.fxml"));
        Parent root = loader.load();

        LoginWindow loginWindow = loader.getController();
        loginWindow.setCurrentRoleState(currentRoleState);
        loginWindow.setCurrentLanguage();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void exitProgramButton() {
        System.exit(0);
        Platform.exit();
    }
}

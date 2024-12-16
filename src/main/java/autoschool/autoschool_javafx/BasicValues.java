package autoschool.autoschool_javafx;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class BasicValues{
    public static final int WINDOW_WIDTH = 1400;
    public static final int WINDOW_HEIGHT = 750;

    public static final String WEBSITE_TESTS_PDR = "https://pdr-online.com.ua/testi/";

    public static final String NAME = "АвтоПро";
    public static final String ICON = "/autoschool/autoschool_javafx/Icons/Icon2.png";

    public static final String STUDENT_ICON = "/autoschool/autoschool_javafx/Icons/Student_icon.png";
    public static final String INSTRUCTOR_ICON = "/autoschool/autoschool_javafx/Icons/Instructor_icon.png";
    public static final String ADMIN_ICON = "/autoschool/autoschool_javafx/Icons/Admin_icon.png";

    public static final String UKRAINE_FLAG_ICON = "/autoschool/autoschool_javafx/Icons/UkraineFlag_icon.png";
    public static final String ENGLAND_FLAG_ICON =  "/autoschool/autoschool_javafx/Icons/EnglandFlag_icon.png";

    public static final String SHOW_PASSWORD_ICON =  "/autoschool/autoschool_javafx/Icons/show_icon.png";
    public static final String HIDE_PASSWORD_ICON =  "/autoschool/autoschool_javafx/Icons/hide_icon.png";

    public static final String EMAIL_FORMAT = "[a-zA-Z0-9]+@[a-zA-Z.-]+\\.[a-zA-Z]{2,}";
    public static final String PHONE_FORMAT = "\\+380\\d{9}";

    public static final String HIGHLIGHT_BORDERS = "-fx-border-color: red;";


    public static final String LeftICON =  "/autoschool/autoschool_javafx/Icons/Icons_Record/turn_arrow_icon.png";
    public static final String EDIT_RECORD_ICON =  "/autoschool/autoschool_javafx/Icons/Icons_Record/editRecord_icon.png";
    public static final String DONE_RECORD_ICON =  "/autoschool/autoschool_javafx/Icons/Icons_Record/done_icon.png";
    public static final String DELETE_RECORD_ICON =  "/autoschool/autoschool_javafx/Icons/Icons_Record/deleteRecord_icon.png";


    public static void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(LanguageManager.getString("errorAlert"));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showWarningAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(LanguageManager.getString("warningAlert"));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static boolean showInformationDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(LanguageManager.getString("informationDialog"));
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public static boolean showConfirmationDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(LanguageManager.getString("confirmationDialog"));
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}

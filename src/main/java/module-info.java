module autoschool.autoschool_javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;
    requires com.jfoenix;
    requires java.desktop;


    opens autoschool.autoschool_javafx to javafx.fxml;
    exports autoschool.autoschool_javafx;
    exports autoschool.autoschool_javafx.Admin;
    opens autoschool.autoschool_javafx.Admin to javafx.fxml;
    exports autoschool.autoschool_javafx.Student;
    opens autoschool.autoschool_javafx.Student to javafx.fxml;
    exports autoschool.autoschool_javafx.Instructor;
    opens autoschool.autoschool_javafx.Instructor to javafx.fxml;
    exports autoschool.autoschool_javafx.TestsPDR;
    opens autoschool.autoschool_javafx.TestsPDR to javafx.fxml;
}
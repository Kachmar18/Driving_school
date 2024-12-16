package autoschool.autoschool_javafx;

import autoschool.autoschool_javafx.Instructor.InstructorMenuWindow;
import autoschool.autoschool_javafx.Student.StudentMenuWindow;
import javafx.scene.control.TextField;

import java.sql.*;

public class DataBase {
    public static final String DATABASEURL = "jdbc:mysql://localhost:3306/автошкола";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "";

    // ^ перевірка чи підключена програма до БД
    public static boolean isDatabaseConnected() {
        try {
            Connection connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD);
            connection.close(); // If successful, close the connection
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // * СТВОРЕННЯ НОВОГО АКАУНТУ
    public static final String sql_insertStudentAccountToDatabase = "INSERT INTO студент(`Ім'я`, `По-батькові`, `Прізвище`, `Дата_народження`, `Номер_телефону`, `E-mail`, `Стать`, `Категорія`, `Пароль_входу`) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String sql_insertInstructorAccountToDatabase = "INSERT INTO `Інструктор` (`Ім'я`, `По-батькові`, `Прізвище`, `Дата_народження`, `Номер_телефону`, `E-mail`, `Стать`, `Категорія`, `Пароль_входу`)" +
            "SELECT ?, ?, ?, ?, ?, ?, ?, ?, ? " +
            "FROM `Спец_вхід` WHERE `Пароль_інструктор` = ?; ";
    public static final String sql_insertAdminAccountToDatabase = "INSERT INTO `адміністратор`(`Ім'я`, `По-батькові`, `Прізвище`, `Дата_народження`, `Номер_телефону`, `E-mail`, `Стать`, `Пароль_входу`) " +
            "SELECT ?, ?, ?, ?, ?, ?, ?, ? " +
            "FROM `Спец_вхід` WHERE `Пароль_адміністратор` = ? ";


    // * ПЕРЕВІРКА ІСНУВАННЯ АКАУНТУ ПРИ СТВОРЕННІ
    public static final String sql_checkExistingStudentAccountInDatabase = "SELECT COUNT(*) FROM `студент` WHERE `Номер_телефону` = ? OR `E-mail` = ? ";
    public static final String sql_checkExistingInstructorAccountInDatabase = "SELECT COUNT(*) FROM `інструктор` WHERE `Номер_телефону` = ? OR `E-mail` = ? ";
    public static final String sql_checkExistingAdminAccountInDatabase = "SELECT COUNT(*) FROM `адміністратор` WHERE `Номер_телефону` = ? OR `E-mail` = ? ";

    // ^ Перевірити, чи акаунт вже існує в базі даних
    public static boolean isAccountAlreadyRegistered(Connection connection, String sql_statement, TextField phoneTextField, TextField emailTextField) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql_statement)) {
            statement.setString(1, phoneTextField.getText());
            statement.setString(2, emailTextField.getText());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0; // If count is greater than 0, the student already exists
                }
            }
        }
        return false;
    }

    // * ПЕРЕВІРКА СПЕЦІАЛЬНОГО ПАРОЛЮ
    public static final String sql_checkSpecialInstructorPassword = "SELECT COUNT(*) FROM `спец_вхід` WHERE `Пароль_інструктор` = ?";
    public static final String sql_checkSpecialAdminPassword = "SELECT COUNT(*) FROM `спец_вхід` WHERE `Пароль_адміністратор` = ? ";

    // ^ Перевірити, чи правильний спеціальний пароль
    public static boolean isSpecialPasswordCorrect(Connection connection, String sql_statement, TextField specialPasswordTextField) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql_statement)) {
            statement.setString(1, specialPasswordTextField.getText());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0; // If count is greater than 0, the special password is correct
                }
            }
        }
        return false;
    }

    // * ПЕРЕВІРКА ІСНУВАННЯ АКАУНТУ ПРИ ВХОДІ
    public static final String sql_loginStudentAccount = "SELECT * FROM `Студент` WHERE (`E-mail` = ? AND `Пароль_входу` = ?) OR (`E-mail` = ? AND `Прізвище`= ? AND `Ім'я` = ?); ";
    public static final String sql_loginInstructorAccount = "SELECT * FROM `інструктор` WHERE (`E-mail` = ? AND `Пароль_входу` = ?) OR (`E-mail` = ? AND `Прізвище`= ? AND `Ім'я` = ?); ";
    public static final String sql_loginAdminAccount = "SELECT * FROM `адміністратор` WHERE (`E-mail` = ? AND `Пароль_входу` = ?) OR (`E-mail` = ? AND `Прізвище`= ? AND `Ім'я` = ?); ";
    public static String[] loginAccount(Connection connection, String sql_statement, TextField email, TextField password, TextField lastName, TextField firstName, String idColumnName){
        try (PreparedStatement statement = connection.prepareStatement(sql_statement)) {
            statement.setString(1, email.getText());
            statement.setString(2, password.getText());
            statement.setString(3, email.getText());
            statement.setString(4, lastName.getText());
            statement.setString(5, firstName.getText());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String id = resultSet.getString(idColumnName);
                    String name = resultSet.getString("Ім'я");
                    String surname = resultSet.getString("Прізвище");
                    return new String[]{id, name, surname};
                } else {
                    BasicValues.showErrorAlert(LanguageManager.getString("loginFailedError"));
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null; // Handle the SQL exception, show an error message, or log the exception
        }
    }

    // * ВИДАЛЕННЯ АКАУНТУ
    public static final String sql_deleteStudentAccount = "DELETE FROM `студент` WHERE `id_студент` = ?  AND `E-mail` = ? AND `Пароль_входу` = ?; ";
    public static final String sql_deleteInstructorAccount = "DELETE FROM `інструктор` WHERE `Id_інструктор` = ? AND `E-mail` = ? AND `Пароль_входу` = ?; ; ";
    public static final String sql_deleteAdminAccount = "DELETE FROM `адміністратор` WHERE `id_адміністратор` = ? AND `E-mail` = ? AND `Пароль_входу` = ?;";

    // * РЕДАГУВАННЯ ПРОФІЛЮ
    public static final String sql_selectInformationStudentAccount = "SELECT `Ім'я`, `По-батькові`, `Прізвище`, `Дата_народження`, `Номер_телефону`, `E-mail`, `Стать`, `Категорія`, `Пароль_входу` FROM `студент` WHERE `id_студент` = ?";
    public static final String sql_editProfileStudentAccount = "UPDATE `студент` SET `Ім'я`= ?,`По-батькові`= ?,`Прізвище`= ?,`Дата_народження`= ?,`Номер_телефону`= ?,`E-mail`= ?,`Стать`= ?,`Категорія`= ?,`Пароль_входу`= ? WHERE `id_студент` = ?";
    public static final String sql_selectInformationInstructorAccount = "SELECT `Ім'я`, `По-батькові`, `Прізвище`, `Дата_народження`, `Номер_телефону`, `E-mail`, `Стать`, `Категорія`, `Пароль_входу` FROM `інструктор` WHERE `Id_інструктор` = ?";
    public static final String sql_editProfileInstructorAccount = "UPDATE `інструктор` SET `Ім'я`= ?,`По-батькові`= ?,`Прізвище`= ?,`Дата_народження`= ?,`Номер_телефону`= ?,`E-mail`= ?,`Стать`= ?, `Категорія`= ?, `Пароль_входу`= ? WHERE `Id_інструктор` = ?";
    public static final String sql_selectInformationAdminAccount = "SELECT `Ім'я`, `По-батькові`, `Прізвище`, `Дата_народження`, `Номер_телефону`, `E-mail`, `Стать`, `Пароль_входу` FROM `адміністратор` WHERE `id_адміністратор` = ?";
    public static final String sql_editProfileAdminAccount = "UPDATE `адміністратор` SET `Ім'я`= ?,`По-батькові`= ?,`Прізвище`= ?,`Дата_народження`= ?,`Номер_телефону`= ?,`E-mail`= ?,`Стать`= ?,`Пароль_входу`= ? WHERE `id_адміністратор` = ?";


    public static void insertResultIntoDatabase(String name, int max_score, int score) {
        try (Connection connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD)) {
            String sql;
            switch (MainWindow.currentRoleState) {
                case 0 -> {
                    sql = "INSERT INTO `тести_теорія`(`Назва`, `Максимум_балів`, `Результати`, `id_студент`, `Id_інструктор`) " +
                            "VALUES (?, ?, ?, ?, NULL)";
                    try (PreparedStatement statement = connection.prepareStatement(sql)) {
                        statement.setString(1, name);
                        statement.setInt(2, max_score);
                        statement.setInt(3, score);
                        statement.setInt(4, StudentMenuWindow.id_student);
                        statement.executeUpdate();
                    }
                }
                case 1 -> {
                    sql = "INSERT INTO `тести_теорія`(`Назва`, `Максимум_балів`, `Результати`, `id_студент`, `Id_інструктор`) " +
                            "VALUES (?, ?, ?, NULL ,?)";
                    try (PreparedStatement statement = connection.prepareStatement(sql)) {
                        statement.setString(1, name);
                        statement.setInt(2, max_score);
                        statement.setInt(3, score);
                        statement.setInt(4, InstructorMenuWindow.id_instructor);
                        statement.executeUpdate();
                    }
                }
            }

            System.out.println("Result inserted into the database successfully.");
        } catch (SQLException e) {
            System.err.println("Error inserting result into the database: " + e.getMessage());
        }
    }

}

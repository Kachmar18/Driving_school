package autoschool.autoschool_javafx.Student;

import autoschool.autoschool_javafx.BasicValues;
import autoschool.autoschool_javafx.DataBase;
import autoschool.autoschool_javafx.LanguageManager;
import autoschool.autoschool_javafx.Localisation;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;

public class StudentSchedule implements Localisation {
    public Label scheduleTheoryLabel, schedulePracticeLabel;
    public TableView<FullCourseData> scheduleTheoryTableView;
    public TableColumn<FullCourseData, String> nameTheoryColumn, categoryTheoryColumn, dayTheoryColumn, hourTheoryColumn, addressTheoryColumn;
    public TableView<PracticeData> schedulePracticeTableView;
    public TableColumn<PracticeData, String> dayPracticeColumn, hourPracticeColumn, routePracticeColumn, durationPracticeColumn, statusPracticeColumn, instructorPracticeColumn, phonePracticeColumn;

    public void setStudentMenuWindow(StudentMenuWindow studentMenuWindow) {
    }
    public void setCurrentLanguage() {
        translate();
    }
    public void translate() {
        scheduleTheoryLabel.setText(LanguageManager.getString("scheduleTheoryLabel"));
        nameTheoryColumn.setText(LanguageManager.getString("nameColumn"));
        categoryTheoryColumn.setText(LanguageManager.getString("categoryColumn"));
        dayTheoryColumn.setText(LanguageManager.getString("dayColumn"));
        hourTheoryColumn.setText(LanguageManager.getString("hourColumn"));
        addressTheoryColumn.setText(LanguageManager.getString("addressColumn"));

        schedulePracticeLabel.setText(LanguageManager.getString("schedulePracticeLabel"));
        dayPracticeColumn.setText(LanguageManager.getString("dayColumn"));
        hourPracticeColumn.setText(LanguageManager.getString("hourColumn"));
        routePracticeColumn.setText(LanguageManager.getString("routeColumn"));
        durationPracticeColumn.setText(LanguageManager.getString("durationPracticeColumn"));
        statusPracticeColumn.setText(LanguageManager.getString("statusColumn"));
        instructorPracticeColumn.setText(LanguageManager.getString("instructorRoleLabel"));
        phonePracticeColumn.setText(LanguageManager.getString("phonePracticeColumn"));
    }

    public void initialize() {
        loadTheoryData();
        loadPracticeData();
    }

    private void loadTheoryData() {
        try {
            Connection connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD);
            if (!DataBase.isDatabaseConnected()) { // Check if the connection to the database is successful
                BasicValues.showErrorAlert(LanguageManager.getString("dbConnectionError"));
                return;
            }

            String sql = """
                    SELECT Курси.Назва, Курси.Категорія, Локація.Місто, Локація.Вулиця, Локація.Будинок, Розклад.День, Розклад.Година
                    FROM Студент
                    LEFT JOIN Курси ON Студент.додатковий_курс = Курси.id_курси OR Студент.id_курси = Курси.id_курси
                    LEFT JOIN Локація ON Курси.id_локація = Локація.id_локація
                    LEFT JOIN Розклад ON Курси.id_розклад = Розклад.id_розклад
                    WHERE Студент.id_студент = ?;
                    """;

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, String.valueOf(StudentMenuWindow.id_student));
            ResultSet resultSet = preparedStatement.executeQuery();

            ObservableList<FullCourseData> data = FXCollections.observableArrayList();

            while (resultSet.next()) {
                FullCourseData scheduleTheory = new FullCourseData(
                        resultSet.getString("Назва"),
                        resultSet.getString("Категорія"),
                        resultSet.getString("День"),
                        resultSet.getString("Година"),
                        resultSet.getString("Місто"),
                        resultSet.getString("Вулиця"),
                        resultSet.getString("Будинок")
                );
                data.add(scheduleTheory);
            }

            nameTheoryColumn.setCellValueFactory(new PropertyValueFactory<>("name")); // Set the cell value factories for each TableColumn
            categoryTheoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
            dayTheoryColumn.setCellValueFactory(new PropertyValueFactory<>("day"));
            hourTheoryColumn.setCellValueFactory(new PropertyValueFactory<>("hour"));
            addressTheoryColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

            scheduleTheoryTableView.setItems(data); // Set the data to the TableView

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static class FullCourseData {
        private final SimpleStringProperty name, category;
        private final SimpleStringProperty day, hour;
        private final SimpleStringProperty address;

        public FullCourseData(String name, String category, String day, String hour, String city, String street, String house) {
            this.name = new SimpleStringProperty(name);
            this.category = new SimpleStringProperty(category);
            this.day = new SimpleStringProperty(day);
            this.hour = new SimpleStringProperty(hour);
            //this.address = new SimpleStringProperty(city + ", " + "вул. " + street + ", " + "буд. "+ house);
            this.address = new SimpleStringProperty(city + ", " + LanguageManager.getString("streetShortForm") + street + ", " + LanguageManager.getString("buildingShortForm")+ house);
        }

        public String getName() {
            return name.get();
        }
        public String getCategory() {
            return category.get();
        }
        public String getDay() {
            return day.get();
        }
        public String getHour() {
            return hour.get();
        }
        public String getAddress() {
            return address.get();
        }
    }

    private void loadPracticeData() {
        try {
            Connection connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD);
            if (!DataBase.isDatabaseConnected()) {
                BasicValues.showErrorAlert(LanguageManager.getString("dbConnectionError"));
                return;
            }

            String sql = """
                    SELECT Практичні_заняття.Дата, Практичні_заняття.Час, Практичні_заняття.Маршрут, Практичні_заняття.`Тривалість, год`, Практичні_заняття.Статус,\s
                           Інструктор.`Ім'я`, Інструктор.Прізвище, Інструктор.Номер_телефону
                    FROM Студент
                    LEFT JOIN Практичні_заняття ON Студент.id_студент = Практичні_заняття.id_студент
                    LEFT JOIN Інструктор ON Практичні_заняття.Id_інструктор = Інструктор.Id_інструктор
                    WHERE Студент.id_студент = ?;
                    """;

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, String.valueOf(StudentMenuWindow.id_student));
            ResultSet resultSet = preparedStatement.executeQuery();

            ObservableList<PracticeData> data = FXCollections.observableArrayList();

            while (resultSet.next()) {
                String instructorName = resultSet.getString("Ім'я");
                String instructorSurname = resultSet.getString("Прізвище");
                String instructorFullName = (instructorName != null && instructorSurname != null) ? instructorName + " " + instructorSurname : "";

                PracticeData practiceData = new PracticeData(
                        resultSet.getString("Дата"),
                        resultSet.getString("Час"),
                        resultSet.getString("Маршрут"),
                        resultSet.getString("Тривалість, год"),
                        resultSet.getString("Статус"),
                        instructorFullName,
                        resultSet.getString("Номер_телефону")
                );
                data.add(practiceData);
            }


            dayPracticeColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
            hourPracticeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
            routePracticeColumn.setCellValueFactory(new PropertyValueFactory<>("route"));
            durationPracticeColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));
            statusPracticeColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
            instructorPracticeColumn.setCellValueFactory(new PropertyValueFactory<>("instructor"));
            phonePracticeColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

            schedulePracticeTableView.setItems(data);

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static class PracticeData {
        private final SimpleStringProperty date, time, route, duration, status, instructor, phone;

        public PracticeData(String date, String time, String route, String duration, String status, String instructor, String phone) {
            this.date = new SimpleStringProperty(date);
            this.time = new SimpleStringProperty(time);
            this.route = new SimpleStringProperty(route);
            this.duration = new SimpleStringProperty(duration);
            this.status = new SimpleStringProperty(status);
            this.instructor = new SimpleStringProperty(instructor);
            this.phone = new SimpleStringProperty(phone);
        }

        public String getDate() {
            return date.get();
        }
        public String getTime() {
            return time.get();
        }
        public String getRoute() {
            return route.get();
        }
        public String getDuration() {
            return duration.get();
        }
        public String getStatus() {
            return status.get();
        }
        public String getInstructor() {
            return instructor.get();
        }
        public String getPhone() {
            return phone.get();
        }
    }
}

package autoschool.autoschool_javafx.Instructor;

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

public class InstructorTheorySchedule implements Localisation {
    public Label scheduleTheoryLabel;
    public TableView<TheoryCourseSchedule> scheduleTheoryTableView;
    public TableColumn<TheoryCourseSchedule, String> nameTheoryColumn, categoryTheoryColumn, timelineTheoryColumn, addressTheoryColumn, amountTheoryColumn;

    public void setInstructorMenuWindow(InstructorMenuWindow instructorMenuWindow) {
    }
    public void setCurrentLanguage() {
        translate();
    }
    public void translate() {
        scheduleTheoryLabel.setText(LanguageManager.getString("scheduleTheoryLabel"));
        nameTheoryColumn.setText(LanguageManager.getString("nameColumn"));
        categoryTheoryColumn.setText(LanguageManager.getString("categoryColumn"));
        timelineTheoryColumn.setText(LanguageManager.getString("timelineColumn"));
        addressTheoryColumn.setText(LanguageManager.getString("addressColumn"));
        amountTheoryColumn.setText(LanguageManager.getString("amountTheoryColumn"));
    }

    public void initialize() {
        loadTheorySchedule();
    }

    private void loadTheorySchedule() {
        try {
            Connection connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD);
            if (!DataBase.isDatabaseConnected()) { // Check if the connection to the database is successful
                BasicValues.showErrorAlert(LanguageManager.getString("dbConnectionError"));
                return;
            }

            String sql = """
                    SELECT\s
                        Курси.Назва,\s
                        Курси.Категорія,\s
                        Локація.Місто,\s
                        Локація.Вулиця,\s
                        Локація.Будинок,\s
                        Розклад.День,\s
                        Розклад.Година,
                        COUNT(DISTINCT Студент.id_студент) + COUNT(DISTINCT Студент_додатковий.додатковий_курс) AS "Кількість студентів"
                    FROM\s
                        Інструктор
                    LEFT JOIN\s
                        Курси ON Інструктор.`Id_інструктор` = Курси.`Id_інструктор`
                    LEFT JOIN\s
                        Локація ON Курси.id_локація = Локація.id_локація
                    LEFT JOIN\s
                        Розклад ON Курси.id_розклад = Розклад.id_розклад
                    LEFT JOIN\s
                        Студент ON Курси.id_курси = Студент.id_курси
                    LEFT JOIN
                        Студент AS Студент_додатковий ON Курси.id_курси = Студент_додатковий.додатковий_курс
                    WHERE\s
                        Інструктор.`Id_інструктор` = ?
                    GROUP BY\s
                        Курси.Назва,\s
                        Курси.Категорія,\s
                        Локація.Місто,\s
                        Локація.Вулиця,\s
                        Локація.Будинок,\s
                        Розклад.День,\s
                        Розклад.Година;            
                    """;

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, String.valueOf(InstructorMenuWindow.id_instructor));
            ResultSet resultSet = preparedStatement.executeQuery();

            ObservableList<TheoryCourseSchedule> data = FXCollections.observableArrayList();

            while (resultSet.next()) {
                TheoryCourseSchedule theoryCourseSchedule = new TheoryCourseSchedule(
                        resultSet.getString("Назва"),
                        resultSet.getString("Категорія"),
                        resultSet.getString("День") + " " + resultSet.getString("Година"),
                        resultSet.getString("Місто"),
                        resultSet.getString("Вулиця"),
                        resultSet.getString("Будинок"),
                        resultSet.getString("Кількість студентів")
                );
                data.add(theoryCourseSchedule);
            }

            nameTheoryColumn.setCellValueFactory(new PropertyValueFactory<>("name")); // Set the cell value factories for each TableColumn
            categoryTheoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
            timelineTheoryColumn.setCellValueFactory(new PropertyValueFactory<>("timeline"));
            addressTheoryColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
            amountTheoryColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

            scheduleTheoryTableView.setItems(data); // Set the data to the TableView

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static class TheoryCourseSchedule {
        private final SimpleStringProperty name, category;
        private final SimpleStringProperty timeline;
        private final SimpleStringProperty address, amount;

        public TheoryCourseSchedule(String name, String category, String timeline, String city, String street, String house, String amount) {
            this.name = new SimpleStringProperty(name);
            this.category = new SimpleStringProperty(category);
            this.timeline = new SimpleStringProperty(timeline);
//            this.address = new SimpleStringProperty(city + ", " + "вул. " + street + ", " + "буд. "+ house);
            this.address = new SimpleStringProperty(city + ", " + LanguageManager.getString("streetShortForm") + street + ", " + LanguageManager.getString("buildingShortForm")+ house);
            this.amount = new SimpleStringProperty(amount);

        }

        public String getName() {
            return name.get();
        }
        public String getCategory() {
            return category.get();
        }
        public String getTimeline() {
            return timeline.get();
        }
        public String getAddress() {
            return address.get();
        }
        public String getAmount() {
            return amount.get();
        }
    }
}

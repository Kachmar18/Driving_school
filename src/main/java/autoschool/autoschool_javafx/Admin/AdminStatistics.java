package autoschool.autoschool_javafx.Admin;

import autoschool.autoschool_javafx.BasicValues;
import autoschool.autoschool_javafx.DataBase;
import autoschool.autoschool_javafx.LanguageManager;
import autoschool.autoschool_javafx.Localisation;
import javafx.animation.FillTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.*;
import java.util.Objects;

public class AdminStatistics extends Application implements Localisation {
    public Label statisticsLabel;
    public ImageView endBarIcon, nextBarIcon;
    public PieChart pieChart;

    public BarChart<String, Number> barChart;

    public LineChart<String, Number> lineChart;

    public Circle dot1, dot2, dot3;


    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/autoschool/autoschool_javafx/Admin/admin-statistics.fxml")));

        primaryStage.setTitle(BasicValues.NAME);
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream(BasicValues.ICON))));
        primaryStage.setScene(new Scene(root, BasicValues.WINDOW_WIDTH, BasicValues.WINDOW_HEIGHT));

        primaryStage.setResizable(false); // * Відключаємо можливість масштабування вікна

        primaryStage.show();
    }

    public void setAdminMenuWindow(AdminMenuWindow adminMenuWindow) {
    }

    public void setCurrentLanguage() {
        translate();
    }

    public void translate() {
        statisticsLabel.setText(LanguageManager.getString("statisticsMenuButton"));

        pieChart.setTitle((LanguageManager.getString("pieChartTestTitle")));
        barChart.setTitle((LanguageManager.getString("barChartCourseLoadTitle")));
        lineChart.setTitle((LanguageManager.getString("lineChartPracticeAmountTitle")));
    }

    public void initialize() {
        loadDataPieChartTests();

        updateProgressCircles("pieChart");

        loadDataBarChartCourses();

        loadDataLineChartPractice();
    }

    private void loadDataPieChartTests() {
        try {
            Connection connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD);
            String query = "SELECT Назва AS Назва_тесту, COUNT(*) AS Кількість_проходжень FROM Тести_теорія GROUP BY Назва";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            while (resultSet.next()) {
                String testName = resultSet.getString("Назва_тесту");
                int count = resultSet.getInt("Кількість_проходжень");
                PieChart.Data slice = new PieChart.Data(testName, count);
                pieChartData.add(slice);
            }

            pieChart.setData(pieChartData);

            addMousePieChartHoverEvent();

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addMousePieChartHoverEvent() {
        for (PieChart.Data data : pieChart.getData()) {
            Tooltip tooltip = new Tooltip();
            tooltip.setStyle("-fx-font-size: 14");
            data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent event) -> {
                int count = (int) data.getPieValue();
                tooltip.setText(LanguageManager.getString("toolTipPassNumber") + " " + count);
                tooltip.show(pieChart, event.getScreenX() + 10, event.getScreenY() + 10); // Показати tooltip неподалік від курсора мишки
            });
            data.getNode().addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent event) -> tooltip.hide());
        }
    }
    private void loadDataBarChartCourses() {
        String query = """
                SELECT\s
                    Курси.id_курси AS id_курсу,
                    Курси.Назва AS Назва_курсу,
                    COUNT(DISTINCT Студент_основний.id_студент) + COUNT(DISTINCT Студент_додатковий.id_студент) AS Кількість_студентів
                FROM\s
                    Курси
                LEFT JOIN\s
                    Студент AS Студент_основний ON Курси.id_курси = Студент_основний.id_курси
                LEFT JOIN\s
                    Студент AS Студент_додатковий ON Курси.id_курси = Студент_додатковий.додатковий_курс
                GROUP BY\s
                    Курси.id_курси, Курси.Назва;
                """;
        try (Connection con = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD);
             PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            while (rs.next()) {
                String courseName = rs.getString("Назва_курсу");
                int studentCount = rs.getInt("Кількість_студентів");
                series.getData().add(new XYChart.Data<>(courseName, studentCount));
            }

            barChart.getData().add(series);

            ObservableList<XYChart.Data<String, Number>> dataList = series.getData();
            for (XYChart.Data<String, Number> data : dataList) {
                String color = String.format("#%06x", (int) (Math.random() * 0xffffff));
                data.getNode().setStyle("-fx-bar-fill: " + color + ";");
            }

            addMouseBarChartHoverEvent();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addMouseBarChartHoverEvent() {
        for (XYChart.Series<String, Number> series : barChart.getData()) {
            for (XYChart.Data<String, Number> data : series.getData()) {
                Tooltip tooltip = new Tooltip();
                tooltip.setStyle("-fx-font-size: 14");
                data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent event) -> {
                    int count = (int) data.getYValue();
                    tooltip.setText(LanguageManager.getString("toolTipStudentAmount") + " " + count);
                    tooltip.show(barChart, event.getScreenX() + 10, event.getScreenY() + 10); // Показати tooltip неподалік від курсора мишки
                });
                data.getNode().addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent event) -> tooltip.hide());
            }
        }
    }

    private void loadDataLineChartPractice() {
        try (Connection connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD);
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery("SELECT Інструктор.`Ім'я`, Інструктор.Прізвище, COUNT(Практичні_заняття.id_практика) AS Кількість_занять FROM Інструктор LEFT JOIN Практичні_заняття ON Інструктор.Id_інструктор = Практичні_заняття.id_інструктор GROUP BY Інструктор.Id_інструктор");

            XYChart.Series<String, Number> series = new XYChart.Series<>();

            while (resultSet.next()) {
                String instructorName = resultSet.getString("Ім'я") + " " + resultSet.getString("Прізвище");
                int numberOfLessons = resultSet.getInt("Кількість_занять");
                series.getData().add(new XYChart.Data<>(instructorName, numberOfLessons));
            }
            lineChart.getData().add(series);

            resultSet.close();

            addMouseLineChartHoverEvent(series);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addMouseLineChartHoverEvent(XYChart.Series<String, Number> series) {
        for (XYChart.Data<String, Number> data : series.getData()) {
            Tooltip tooltip = new Tooltip();
            tooltip.setStyle("-fx-font-size: 14");
            data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent event) -> {
                int count = (int) data.getYValue();
                tooltip.setText(LanguageManager.getString("toolTipPracticeAmount") + " " + count);
                tooltip.show(lineChart, event.getScreenX() + 10, event.getScreenY() + 10); // Показати tooltip неподалік від курсора мишки
            });
            data.getNode().addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent event) -> tooltip.hide());
        }
    }

    public void showNextChart() {
        if (pieChart.isVisible()) {
            animateChartTransition(pieChart, barChart, true);
            updateProgressCircles("barChart");
        } else if (barChart.isVisible()) {
            animateChartTransition(barChart, lineChart, true);
            updateProgressCircles("lineChart");
        } else if (lineChart.isVisible()) {
            animateChartTransition(lineChart, pieChart, true);
            updateProgressCircles("pieChart");
        }
    }
    public void showPreviousChart() {
        if (pieChart.isVisible()) {
            animateChartTransition(pieChart, lineChart, false);
            updateProgressCircles("lineChart");
        } else if (barChart.isVisible()) {
            animateChartTransition(barChart, pieChart, false);
            updateProgressCircles("pieChart");
        } else if (lineChart.isVisible()) {
            animateChartTransition(lineChart, barChart, false);
            updateProgressCircles("barChart");
        }
    }

    private void animateChartTransition(Node currentNode, Node nextNode, boolean moveRight) {
        TranslateTransition moveOut = new TranslateTransition(Duration.seconds(0.35), currentNode);
        moveOut.setByX(moveRight ? -1400 : 1400); // Переміщення вліво або вправо в залежності від напрямку
        moveOut.setOnFinished(event -> {
            currentNode.setVisible(false);
            currentNode.setTranslateX(0); // Повертаємо до початкового положення
            nextNode.setTranslateX(moveRight ? 1400 : -1400); // Початкове положення для наступної діаграми
            nextNode.setVisible(true);
            TranslateTransition moveIn = new TranslateTransition(Duration.seconds(0.35), nextNode);
            moveIn.setByX(moveRight ? -1400 : 1400); // Рухаємо діаграму вліво або вправо для відображення
            moveIn.play();
        });
        moveOut.play();
    }

    private void updateProgressCircles(String chartName) {
        Color greyColor = Color.GREY;
        Color greenColor = Color.web("#005B99");

        FillTransition transition1 = new FillTransition(Duration.seconds(0.5), dot1);
        transition1.setToValue(greyColor);

        FillTransition transition2 = new FillTransition(Duration.seconds(0.5), dot2);
        transition2.setToValue(greyColor);

        FillTransition transition3 = new FillTransition(Duration.seconds(0.5), dot3);
        transition3.setToValue(greyColor);

        switch (chartName) {
            case "pieChart" -> transition1.setToValue(greenColor);
            case "barChart" -> transition2.setToValue(greenColor);
            case "lineChart" -> transition3.setToValue(greenColor);
            default -> {
            }
        }

        transition1.play();
        transition2.play();
        transition3.play();
    }

}
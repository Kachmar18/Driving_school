package autoschool.autoschool_javafx.Admin;

import autoschool.autoschool_javafx.BasicValues;
import autoschool.autoschool_javafx.DataBase;
import autoschool.autoschool_javafx.LanguageManager;
import autoschool.autoschool_javafx.Localisation;
import com.jfoenix.controls.JFXButton;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.*;
import java.util.Objects;

public class AdminHandleMembers implements Localisation {
    public JFXButton addNewMemberButton;
    public TextField keyWordTextField;
    public TableView<FullData> membersTableView;
    public TableColumn<FullData, String> nameColumn, dateBirthColumn, numberColumn, emailColumn, sexColumn, categoryColumn, operateCourseColumn;
    public Label membersInfoLabel;
    private AdminMenuWindow adminMenuWindow;

    private String sql, updateSql;

    public void setAdminMenuWindow(AdminMenuWindow adminMenuWindow) {this.adminMenuWindow = adminMenuWindow;}
    public void setCurrentLanguage() {
        translate();
    }
    public void translate() {
        membersInfoLabel.setText(LanguageManager.getString("instructorsInfoLabel"));
        keyWordTextField.setPromptText(LanguageManager.getString("keyWordPrompt"));


        nameColumn.setText(LanguageManager.getString("nameMemberColumn"));
        dateBirthColumn.setText(LanguageManager.getString("dateBirthColumn"));
        numberColumn.setText(LanguageManager.getString("phonePracticeColumn"));
        emailColumn.setText(LanguageManager.getString("emailColumn"));
        sexColumn.setText(LanguageManager.getString("sexColumn"));
        categoryColumn.setText(LanguageManager.getString("categoryColumn"));
    }

    public void initialize() {
        keyWordTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            loadInfo(); // Reload data on text change
        });

        operateCourseColumn.setCellFactory(createOperationsCellFactory());

        loadInfo();
    }

    private void loadInfo() {
        try {
            Connection connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD);
            if (!DataBase.isDatabaseConnected()) { // Check if the connection to the database is successful
                BasicValues.showErrorAlert(LanguageManager.getString("dbConnectionError"));
                return;
            }

            System.out.println("Value of selectMember in AdminSplit " + AdminSplitMembers.selectMember);

            switch (AdminSplitMembers.selectMember){
                case 1 -> {
                    sql = "SELECT `Id_інструктор`, `Ім'я`, `По-батькові`, `Прізвище`, `Дата_народження`, `Номер_телефону`, `E-mail`, `Стать`, `Категорія` FROM `інструктор`";
                    updateSql = "DELETE FROM `інструктор` WHERE `Id_інструктор` = ?";
                }
                case 2 -> {
                    sql = "SELECT `id_студент`, `Ім'я`, `По-батькові`, `Прізвище`, `Дата_народження`, `Номер_телефону`, `E-mail`, `Стать`, `Категорія` FROM `студент`";
                    updateSql = "DELETE FROM `студент` WHERE `id_студент` = ?";
                }
            }

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            ObservableList<FullData> data = FXCollections.observableArrayList();

            while (resultSet.next()) {
                String id = null;
                switch (AdminSplitMembers.selectMember){
                    case 1 -> id = resultSet.getString("Id_інструктор");
                    case 2 -> id = resultSet.getString("id_студент");
                }

                FullData infoMember = new FullData(
                        id,
                        resultSet.getString("Ім'я") + " " + resultSet.getString("По-батькові")+ " " + resultSet.getString("Прізвище"),
                        resultSet.getString("Дата_народження"),
                        resultSet.getString("Номер_телефону"),
                        resultSet.getString("E-mail"),
                        resultSet.getString("Стать"),
                        resultSet.getString("Категорія")
                );
                data.add(infoMember);
            }

            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name")); // Set the cell value factories for each TableColumn
            dateBirthColumn.setCellValueFactory(new PropertyValueFactory<>("dateBirth"));
            numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
            emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
            sexColumn.setCellValueFactory(new PropertyValueFactory<>("sex"));
            categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

            String keyword = keyWordTextField.getText().toLowerCase(); // Get the keyword from the TextField
            ObservableList<FullData> filteredData = data.filtered(instructorData ->
                    instructorData.getName().toLowerCase().contains(keyword) ||
                            instructorData.getDateBirth().toLowerCase().contains(keyword) ||
                            instructorData.getNumber().toLowerCase().contains(keyword) ||
                            instructorData.getEmail().toLowerCase().contains(keyword) ||
                            instructorData.getSex().toLowerCase().contains(keyword) ||
                            instructorData.getCategory().toLowerCase().contains(keyword)
            );

            membersTableView.setItems(data); // Set the data to the TableView
            membersTableView.setItems(filteredData);

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static class FullData {
        private final SimpleStringProperty id_member;
        private final SimpleStringProperty name, dateBirth, number, email, sex, category;
        public FullData(String id_member, String name, String dateBirth, String number, String email, String sex, String category) {
            this.id_member = new SimpleStringProperty(id_member);
            this.name = new SimpleStringProperty(name);
            this.dateBirth = new SimpleStringProperty(dateBirth);
            this.number = new SimpleStringProperty(number);
            this.email = new SimpleStringProperty(email);
            this.sex = new SimpleStringProperty(sex);
            this.category = new SimpleStringProperty(category);
        }
        public String getId_member() {
            return id_member.get();
        }
        public String getName() {
            return name.get();
        }
        public String getDateBirth() {
            return dateBirth.get();
        }
        public String getNumber() {
            return number.get();
        }
        public String getEmail() {
            return email.get();
        }
        public String getSex() {
            return sex.get();
        }
        public String getCategory() {
            return category.get();
        }
    }

    public Callback<TableColumn<FullData, String>, TableCell<FullData, String>> createOperationsCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<FullData, String> call(TableColumn<FullData, String> param) {
                return new TableCell<>() {
                    final ImageView editIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(BasicValues.EDIT_RECORD_ICON))));
                    final ImageView deleteIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(BasicValues.DELETE_RECORD_ICON))));
                    {
                        editIcon.setFitWidth(27);
                        editIcon.setFitHeight(27);

                        deleteIcon.setFitWidth(27);
                        deleteIcon.setFitHeight(27);
                    }

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox iconsContainer = new HBox(10);
                            iconsContainer.getChildren().addAll(editIcon, deleteIcon);

                            editIcon.setOnMouseClicked(event -> {
                                try {
                                    handleEditAction(getTableRow().getIndex());
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            });

                            deleteIcon.setOnMouseClicked(event -> handleDeleteAction(getTableRow().getIndex()));

                            setGraphic(iconsContainer);
                        }
                    }
                };
            }
        };
    }

    private void handleEditAction(int rowIndex) throws IOException {
        System.out.println("Edit action for row " + rowIndex); // Perform edit action for the specified row index

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/autoschool/autoschool_javafx/admin/admin-new-member.fxml"));
        Parent fxml = loader.load();

        AdminAddMember adminAddMember = loader.getController();
        adminAddMember.setCurrentLanguage();
        adminAddMember.setAdminMenuWindow(adminMenuWindow);

        adminAddMember.editMemebrsTitleText.setText(LanguageManager.getString("editRecordTitleText"));

        FullData selectedMember = membersTableView.getItems().get(rowIndex);
        adminAddMember.setEditMemberData(selectedMember, selectedMember.getId_member());

        adminMenuWindow.contentArea.getChildren().removeAll();
        adminMenuWindow.contentArea.getChildren().setAll(fxml);
    }

    private void handleDeleteAction(int rowIndex) {
        try {
            FullData selectedMember = membersTableView.getItems().get(rowIndex);
            String memberId = selectedMember.getId_member();
            BasicValues.showInformationDialog(LanguageManager.getString("additionalInfoDeleteConfirmation"));

            if (BasicValues.showConfirmationDialog(LanguageManager.getString("deleteConfirmation"))) {
                Connection connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD);

                try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
                    updateStatement.setString(1, memberId);
                    updateStatement.executeUpdate();
                }
                connection.close();

                switch (AdminSplitMembers.selectMember){
                    case 1 -> BasicValues.showInformationDialog(LanguageManager.getString("deleteInstructorRecordSuccess"));

                    case 2 -> BasicValues.showInformationDialog(LanguageManager.getString("deleteStudentRecordSuccess"));
                }

                System.out.println("Updated status to 'Завершено' for practice with ID: " + memberId);

                loadInfo();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addNewMemberButton() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/autoschool/autoschool_javafx/admin/admin-new-member.fxml"));
        Parent fxml = loader.load();

        AdminAddMember adminAddMember = loader.getController();
        adminAddMember.setCurrentLanguage();
        adminAddMember.setAdminMenuWindow(adminMenuWindow);

        switch (AdminSplitMembers.selectMember){
            case 1 -> adminAddMember.editMemebrsTitleText.setText(LanguageManager.getString("addNewInstructorLabel"));

            case 2 -> adminAddMember.editMemebrsTitleText.setText(LanguageManager.getString("addNewStudentLabel"));
        }


        adminMenuWindow.contentArea.getChildren().removeAll();
        adminMenuWindow.contentArea.getChildren().setAll(fxml);
    }
}

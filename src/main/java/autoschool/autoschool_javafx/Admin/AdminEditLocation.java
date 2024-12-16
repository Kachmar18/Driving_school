package autoschool.autoschool_javafx.Admin;

import autoschool.autoschool_javafx.*;
import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class AdminEditLocation implements Localisation {
    public Text editRecordTitleText;
    public ComboBox<String> locationInfoCombobox;
    public TextField cityTextField, streetTextField,houseTextField;
    public Label locationLabel, cityLabel, streetLabel, houseLabel, errorTipLabel;
    public JFXButton deleteLocationButton, createLocationButton, saveInfoButton, returnButton;

    public String city, street, house;
    public int locationId;

    private AdminMenuWindow adminMenuWindow;

    public void setAdminMenuWindow(AdminMenuWindow adminMenuWindow) {
        this.adminMenuWindow = adminMenuWindow;
    }
    public void setCurrentLanguage() {
        translate();
    }
    public void translate() {
        editRecordTitleText.setText(LanguageManager.getString("editRecordTitleText"));
        locationLabel.setText(LanguageManager.getString("locationLabel"));
        cityLabel.setText(LanguageManager.getString("cityColumn"));
        streetLabel.setText(LanguageManager.getString("streetColumn"));
        houseLabel.setText(LanguageManager.getString("houseColumn"));
        errorTipLabel.setText(LanguageManager.getString("errorTipLabel"));

        deleteLocationButton.setText(LanguageManager.getString("deleteRecordButton"));
        createLocationButton.setText(LanguageManager.getString("createRecordButton"));
        saveInfoButton.setText(LanguageManager.getString("editAccount"));
        returnButton.setText(LanguageManager.getString("returnButton"));

        locationInfoCombobox.setPromptText(LanguageManager.getString("locationComboBoxPrompt"));
    }

    public void initialize() {
        populateLocationComboBox(locationInfoCombobox);

        locationInfoCombobox.setValue(LanguageManager.getString("addNewLocationText"));

        CommonMethods.applyNameLimit(cityTextField);
        CommonMethods.applyNameLimit(streetTextField);

        saveInfoButton.setDisable(true);
        deleteLocationButton.setDisable(true);
        createLocationButton.setDisable(false);
        cityTextField.setDisable(false);
        streetTextField.setDisable(false);
        houseTextField.setDisable(false);

        locationInfoCombobox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                if(newValue.equals(LanguageManager.getString("addNewLocationText"))) {
                    clearFields(); // Очистити поля, якщо обрано "Додати нову локацію"

                    saveInfoButton.setDisable(true);
                    deleteLocationButton.setDisable(true);
                } else {
                    String[] locationData = newValue.split(", ");
                    city = locationData[0];
                    String[] streetData = locationData[1].split(LanguageManager.getString("streetShortForm"));
                    street = streetData[1];
                    String[] houseData = locationData[2].split(LanguageManager.getString("buildingShortForm"));
                    house = houseData[1];

                    cityTextField.setText(city);
                    streetTextField.setText(street);
                    houseTextField.setText(house);

                    Connection connection;
                    try {
                        connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD);
                        locationId = AdminNewCourse.getLocationId(connection, cityTextField.getText(), streetTextField.getText(), houseTextField.getText());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                    saveInfoButton.setDisable(false);
                    deleteLocationButton.setDisable(false);
                    createLocationButton.setDisable(false);
                    cityTextField.setDisable(false);
                    streetTextField.setDisable(false);
                    houseTextField.setDisable(false);
                }
            }
        });
    }

    private void clearFields() {
        cityTextField.clear();
        streetTextField.clear();
        houseTextField.clear();

        saveInfoButton.setDisable(true);
        deleteLocationButton.setDisable(true);

        city = null;
        street = null;
        house = null;
    }

    public void populateLocationComboBox(ComboBox<String> comboBox) {
        ObservableList<String> locationList = FXCollections.observableArrayList();

        try (Connection connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT `id_локація`, `Місто`, `Вулиця`, `Будинок` FROM `локація`");
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                String locationFullData = resultSet.getString("Місто") + ", " + LanguageManager.getString("streetShortForm") +
                        resultSet.getString("Вулиця") + ", " + LanguageManager.getString("buildingShortForm") + resultSet.getString("Будинок");

                locationList.add(locationFullData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        locationList.add(LanguageManager.getString("addNewLocationText"));
        comboBox.setItems(locationList);
    }

    public void deleteLocationButton(){
        if (areAllFieldsFilled()) {
            errorTipLabel.setVisible(false);

            deleteLocationById(locationId);
        } else {
            errorTipLabel.setVisible(true);
            BasicValues.showWarningAlert((LanguageManager.getString("fillAllFieldsWarning")));
        }
    }
    public void deleteLocationById(int locationId) {
        if (areAllFieldsFilled()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(LanguageManager.getString("confirmationDialog"));
            alert.setHeaderText(LanguageManager.getString("deleteConformation"));
            alert.setContentText(LanguageManager.getString("additionalInfoDeleteConfirmation") + "\n" +
                    LanguageManager.getString("deleteLocationConfirmation") + "\n" + LanguageManager.getString("cityColumn")+": "
                    + city + "\n" + LanguageManager.getString("streetColumn")+": " + street + "\n" +LanguageManager.getString("houseColumn")+ ": " + house);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try (Connection connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD);
                     PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM `локація` WHERE `id_локація` = ?")) {
                    preparedStatement.setInt(1, locationId);
                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        populateLocationComboBox(locationInfoCombobox); // Refresh the combo box after deletion
                        BasicValues.showInformationDialog(LanguageManager.getString("deleteLocationSuccess"));
                        clearFields(); // Clear fields after successful deletion
                        locationInfoCombobox.getSelectionModel().select(LanguageManager.getString("addNewLocationText")); // Select "Додати нову локацію" in ComboBox
                        saveInfoButton.setDisable(true);
                    } else {
                        BasicValues.showErrorAlert(LanguageManager.getString("deleteLocationRecordFailedError"));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    BasicValues.showInformationDialog(LanguageManager.getString("deleteLocationRecordFailedError") + " " + e.getMessage());
                }
            }
        } else {
            BasicValues.showWarningAlert((LanguageManager.getString("fillAllFieldsWarning")));
        }
    }

    public void createLocationButton() {
        if (areAllFieldsFilled()) {
            errorTipLabel.setVisible(false);

            city = cityTextField.getText();
            street = streetTextField.getText();
            house = houseTextField.getText();

            insertNewLocation(city, street, house);
        } else {
            errorTipLabel.setVisible(true);
            BasicValues.showWarningAlert((LanguageManager.getString("fillAllFieldsWarning")));
        }
    }
    public void insertNewLocation(String city, String street, String house) {
        try (Connection connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `локація`(`Місто`, `Вулиця`, `Будинок`) VALUES (?, ?, ?)")) {
            if (isLocationAlreadyRegistered(connection, city, street, house)) {
                BasicValues.showInformationDialog(LanguageManager.getString("alreadyExistLocationInfo"));
                return;
            }
            preparedStatement.setString(1, city);
            preparedStatement.setString(2, street);
            preparedStatement.setString(3, house);

            int rowsAffected = preparedStatement.executeUpdate(); // Виконати запит на вставку даних

            if (rowsAffected > 0) {
                BasicValues.showInformationDialog(LanguageManager.getString("locationInsertedSuccessfully"));
                populateLocationComboBox(locationInfoCombobox);
                cityTextField.setText(city);
                streetTextField.setText(street);
                houseTextField.setText(house);
            } else {
                BasicValues.showInformationDialog(LanguageManager.getString("locationInsertionFailed"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            BasicValues.showInformationDialog(LanguageManager.getString("locationInsertionFailed") + e.getMessage());
        }
    }

    private boolean isLocationAlreadyRegistered(Connection connection, String city, String street, String house) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM `локація` WHERE `Місто` = ? And `Вулиця` = ? And `Будинок` = ?")) {
            preparedStatement.setString(1, city);
            preparedStatement.setString(2, street);
            preparedStatement.setString(3, house);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        }
        return false;
    }

    public void saveInfoButton() {
        if (areAllFieldsFilled()) {
            errorTipLabel.setVisible(false);

            city = cityTextField.getText();
            street = streetTextField.getText();
            house = houseTextField.getText();

            updateLocation(city, street, house, locationId);
        } else {
            errorTipLabel.setVisible(true);
            BasicValues.showWarningAlert((LanguageManager.getString("fillAllFieldsWarning")));
        }
    }
    public void updateLocation(String city, String street, String house, int locationId) {
        try (Connection connection = DriverManager.getConnection(DataBase.DATABASEURL, DataBase.USERNAME, DataBase.PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `локація` SET `Місто`=?, `Вулиця`=?, `Будинок`=? WHERE `id_локація`= ?")) {
            if (isLocationAlreadyRegistered(connection, city, street, house)) {
                BasicValues.showInformationDialog(LanguageManager.getString("alreadyExistLocationInfo"));
                return;
            }
            preparedStatement.setString(1, city);
            preparedStatement.setString(2, street);
            preparedStatement.setString(3, house);
            preparedStatement.setInt(4, locationId);

            int rowsAffected = preparedStatement.executeUpdate(); // Execute the update query

            if (rowsAffected > 0) {
                BasicValues.showInformationDialog(LanguageManager.getString("updateLocationRecordInformation"));
                populateLocationComboBox(locationInfoCombobox); // Refresh the combo box after update
            } else {
                BasicValues.showInformationDialog(LanguageManager.getString("updateLocationRecordFailedError"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            BasicValues.showInformationDialog(LanguageManager.getString("updateLocationRecordFailedError")+ " " + e.getMessage());
        }
    }

    private boolean areAllFieldsFilled() {                                             // * Перевірка заповненості полів
        TextField[] textFields = {cityTextField, streetTextField,houseTextField};

        boolean allFieldsFilled = true;
        for (TextField textField : textFields) { // Перевірка, чи всі текстові поля заповнені
            if (textField.getText().trim().isEmpty()) {
                textField.setStyle(BasicValues.HIGHLIGHT_BORDERS);
                allFieldsFilled = false;
            } else {
                textField.setStyle(""); // Якщо поле заповнено, знімаємо стиль CSS
            }
        }

        String city = cityTextField.getText().trim();
        boolean isCityValid = CommonMethods.isValidName(city);
        cityTextField.setStyle(!isCityValid ? BasicValues.HIGHLIGHT_BORDERS : "");
        allFieldsFilled &= isCityValid;

        String street = streetTextField.getText().trim();
        boolean isStreetValid = CommonMethods.isValidName(street);
        streetTextField.setStyle(!isStreetValid ? BasicValues.HIGHLIGHT_BORDERS : "");
        allFieldsFilled &= isStreetValid;

        return allFieldsFilled;
    }

    public void returnButton() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/autoschool/autoschool_javafx/admin/admin-new-course.fxml"));
        Parent fxml = loader.load();

        AdminNewCourse adminNewCourse = loader.getController();
        adminNewCourse.setCurrentLanguage();
        adminNewCourse.setAdminMenuWindow(adminMenuWindow);

        adminMenuWindow.contentArea.getChildren().removeAll();
        adminMenuWindow.contentArea.getChildren().setAll(fxml);
    }
}

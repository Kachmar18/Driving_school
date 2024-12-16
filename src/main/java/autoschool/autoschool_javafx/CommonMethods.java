package autoschool.autoschool_javafx;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.time.LocalDate;
import java.util.Objects;

public class CommonMethods {
    // ^ Зміна ролі та іконки
    public static void updateRoleLabelAndIcon(Label roleLabel, ImageView roleIcon, Class<?> clazz) {
        switch (MainWindow.currentRoleState) {
            case 0 -> {
                roleLabel.setText(LanguageManager.getString("studentRoleLabel"));
                roleIcon.setImage(new Image(Objects.requireNonNull(clazz.getResourceAsStream(BasicValues.STUDENT_ICON))));
            }
            case 1 -> {
                roleLabel.setText(LanguageManager.getString("instructorRoleLabel"));
                roleIcon.setImage(new Image(Objects.requireNonNull(clazz.getResourceAsStream(BasicValues.INSTRUCTOR_ICON))));
            }
            case 2 -> {
                roleLabel.setText(LanguageManager.getString("adminRoleLabel"));
                roleIcon.setImage(new Image(Objects.requireNonNull(clazz.getResourceAsStream(BasicValues.ADMIN_ICON))));
            }
        }
    }

    // ^ Видимість пароля
    private static boolean passwordVisible = false;
    private static Timeline showPasswordTimeline;
    public static void showHidePassword(TextField passwordTextField, ImageView passwordIcon, Class<?> clazz){
        if (passwordVisible) {  // Code for when the password is hidden
            passwordTextField.setText(encrypt(passwordTextField.getPromptText()));
            passwordTextField.setPromptText("");
            passwordTextField.setDisable(false);
            passwordTextField.getStyleClass().remove("text-field-password");
            passwordVisible = false;

            passwordIcon.setImage(new Image(Objects.requireNonNull(clazz.getResourceAsStream(BasicValues.HIDE_PASSWORD_ICON))));
        } else {  // Code for when the password is visible
            passwordTextField.setPromptText(passwordTextField.getText());
            passwordTextField.setText("");
            passwordTextField.setDisable(true);
            passwordTextField.getStyleClass().add("text-field-password");
            passwordVisible = true;
            passwordIcon.setImage(new Image(Objects.requireNonNull(clazz.getResourceAsStream(BasicValues.SHOW_PASSWORD_ICON))));

            if (showPasswordTimeline != null) {
                showPasswordTimeline.stop();
            }

            showPasswordTimeline = new Timeline(new KeyFrame(
                    Duration.seconds(2),
                    event -> {  // Code to execute after 2 seconds
                        passwordTextField.setText(encrypt(passwordTextField.getPromptText()));
                        passwordTextField.setPromptText("");
                        passwordTextField.setDisable(false);
                        passwordTextField.getStyleClass().remove("text-field-password");
                        passwordVisible = false;

                        passwordIcon.setImage(new Image(Objects.requireNonNull(clazz.getResourceAsStream(BasicValues.HIDE_PASSWORD_ICON))));
                    }
            ));
            showPasswordTimeline.play();
        }
    }

    public static String encrypt(String password) {
        return password; // * Метод для зашифрування пароля
    }

    // ^ Фільтр обмеження вводу у полях - Перша літера велика, тільки одне слово, лише літери
    public static void applySingleWordLimit(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches("[a-zA-Zа-яА-ЯіІ]*") && isSingleWord(newValue)) {
                textField.setText(capitalizeFirstLetter(newValue));
            } else {
                textField.setText(oldValue); // Повернутися до попереднього валідного значення
            }
        });
    }

//    public static void applyFirstNameLimit(TextField textField) {
//        textField.textProperty().addListener((observable, oldValue, newValue) -> {
//            if (newValue.matches("[a-zA-Zа-яА-ЯіІ\\s-]*")) {
//                if (newValue.contains("-")) {
//                    String filteredValue = newValue.replaceAll("\\s+", "");
//                    textField.setText(capitalizeFirstLetter(filteredValue));
//                } else {
//                    String filteredValue = newValue.replaceAll("\\s+", "-");
//                    textField.setText(capitalizeFirstLetter(filteredValue));
//                }
//            } else {
//                textField.setText(oldValue);
//            }
//        });
//    }

    // ^ Фільтр обмеження вводу у полях - лише літери,  тільки одне слово/подвійне через тире, з великої літери
    public static void applyNameLimit(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches("[a-zA-Zа-яА-ЯіІ\\s-]*")) {
                if (newValue.contains("-")) {
                    String filteredValue = newValue.replaceAll("\\s+", "");
                    textField.setText(capitalizeWords(filteredValue));
                } else {
                    String filteredValue = newValue.replaceAll("\\s+", "-");
                    textField.setText(capitalizeWords(filteredValue));
                }
            } else {
                textField.setText(oldValue);
            }
        });
    }

    public static String capitalizeWords(String text) {
        StringBuilder sb = new StringBuilder();
        boolean capitalizeNext = true;
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                if (capitalizeNext) {
                    sb.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                } else {
                    sb.append(Character.toLowerCase(c));
                }
            } else if (c == '-') {
                sb.append(c);
                capitalizeNext = true;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static boolean isSingleWord(String text) {
        return text.trim().split("\\s+").length == 1;
    }

    public static String capitalizeFirstLetter(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    // ^ Перевірка, чи у полі лише літери, одне слово/подвійне через тире, з великої літери
    public static boolean isValidName(String name) {
        if (name.isEmpty()) {
            return false;
        }
        boolean isSingleWord = name.split("\\s+").length == 1;
        boolean isDoubleSurname = name.matches("[A-Za-z]+-[A-Za-z]+");
        return (isSingleWord && !name.endsWith("-") && !name.startsWith("-")) || isDoubleSurname;
    }




    // ^ Фільтр обмеження вводу у полях - "+380" - початкові та незмінні, тільки цифри, не більше 13 символів
    public static void applyPhoneFormat(TextField textField) {
        textField.setText("+380");
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\+380\\d*") || newValue.length() > 13) {
                textField.setText(oldValue); // If the new value doesn't match the format or exceeds 12 characters, revert to the old value
            }
        });
    }

    // ^ Фільтр обмеження вводу у полях - "+380" - початкові та незмінні, тільки цифри, не більше 13 символів -- вже з введеним номером телефону
    public static void applyPhoneExistedFormat(TextField textField) {
        textField.setText("+380");
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\+380\\d*") || newValue.length() > 13) {
                textField.setText(oldValue); // If the new value doesn't match the format or exceeds 13 characters, revert to the old value
            }
        });
    }

    // ^ Фільтр обмеження вводу у полі часу -  тільки цифри, не більше 4 символів , після другої цифри -- двокрапка
    public static void timePickerRestrictions(TextField timePickerTextField) {
        timePickerTextField.textProperty().addListener((observable, oldValue, newValue) -> {
        newValue = newValue.replaceAll("[^\\d]", ""); // Allow only numeric input

        if (newValue.length() > 4) { // Limit to four digits
            newValue = newValue.substring(0, 4);
        }

        if (newValue.length() >= 2 && !":".equals(newValue.substring(1, 2))) { // Insert a colon after the second digit
            newValue = newValue.substring(0, 2) + ":" + newValue.substring(2);
        }

        if (newValue.length() >= 1) { // Restrict the first digit to 0-2
            int firstDigit = Character.getNumericValue(newValue.charAt(0));
            if (firstDigit > 2) {
                newValue = "2" + newValue.substring(1);
            }
        }

        if (newValue.length() >= 2) { // Restrict the second digit to 0-4
            int secondDigit = Character.getNumericValue(newValue.charAt(1));
            if (secondDigit > 4) {
                newValue = newValue.substring(0, 1) + "4" + newValue.substring(2);
            }
        }

        if (newValue.length() >= 4) { // Restrict the fourth digit to 0-9
            int fourthDigit = Character.getNumericValue(newValue.charAt(3));
            if (fourthDigit > 5) {
                newValue = newValue.substring(0, 3) + "5";
            }
        }

        timePickerTextField.replaceText(0, timePickerTextField.getLength(), newValue); // Set the modified text
        });
    }

    // ^ Фільтр списку категорій у combo-box
    public static void categoryComboBoxList(ComboBox<String> categoryComboBox) {
        categoryComboBox.getItems().clear();

        categoryComboBox.getItems().add("А");
        categoryComboBox.getItems().add("B");
        categoryComboBox.getItems().add("C");
        categoryComboBox.getItems().add("D");

        categoryComboBox.setDisable(false); // Зробимо вибір доступним для користувача
    }

    // ^ Фільтр списку тривалості у combo-box
    public static void durationComboBoxList(ComboBox<String> categoryComboBox) {
        categoryComboBox.getItems().clear();

        categoryComboBox.getItems().add("0,5");
        categoryComboBox.getItems().add("1");
        categoryComboBox.getItems().add("1,5");
        categoryComboBox.getItems().add("2");
        categoryComboBox.getItems().add("2,5");
        categoryComboBox.getItems().add("3");
        categoryComboBox.getItems().add("3,5");
        categoryComboBox.getItems().add("4");

        categoryComboBox.setDisable(false); // Зробимо вибір доступним для користувача
    }

    public static void statusComboBoxList(ComboBox<String> categoryComboBox) {
        categoryComboBox.getItems().clear();

//        categoryComboBox.getItems().add(LanguageManager.getString("plannedStatus"));
//        categoryComboBox.getItems().add(LanguageManager.getString("inProgressStatus"));
//        categoryComboBox.getItems().add(LanguageManager.getString("doneStatus"));
//        categoryComboBox.getItems().add(LanguageManager.getString("cancelledStatus"));
//        categoryComboBox.getItems().add(LanguageManager.getString("delayedStatus"));

        categoryComboBox.getItems().add("Заплановано");
        categoryComboBox.getItems().add("В процесі");
        categoryComboBox.getItems().add("Завершено");
        categoryComboBox.getItems().add("Скасовано");
        categoryComboBox.getItems().add("Відтерміновано");

        categoryComboBox.setDisable(false); // Зробимо вибір доступним для користувача
    }

    public static void dayComboBoxList(ComboBox<String> categoryComboBox) {
        categoryComboBox.getItems().clear();

//        categoryComboBox.getItems().add(LanguageManager.getString("mondayDay"));
//        categoryComboBox.getItems().add(LanguageManager.getString("tuesdayDay"));
//        categoryComboBox.getItems().add(LanguageManager.getString("wednesdayDay"));
//        categoryComboBox.getItems().add(LanguageManager.getString("thursdayDay"));
//        categoryComboBox.getItems().add(LanguageManager.getString("fridayDay"));
//        categoryComboBox.getItems().add(LanguageManager.getString("saturdayDay"));
//        categoryComboBox.getItems().add(LanguageManager.getString("sundayDay"));

        categoryComboBox.getItems().add("Понеділок");
        categoryComboBox.getItems().add("Вівторок");
        categoryComboBox.getItems().add("Середа");
        categoryComboBox.getItems().add("Четвер");
        categoryComboBox.getItems().add("П'ятниця");
        categoryComboBox.getItems().add("Субота");
        categoryComboBox.getItems().add("Неділя");

        categoryComboBox.setDisable(false); // Зробимо вибір доступним для користувача
    }

    public static void setupDatePicker(DatePicker datePicker) {
        LocalDate earliestDate = LocalDate.now().minusYears(16);

        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date != null) {
                    LocalDate currentDate = LocalDate.now();
                    LocalDate minDate = currentDate.minusYears(16);
                    if (date.isAfter(minDate) || date.isEqual(minDate)) {
                        setDisable(true);
                    }
                }
            }
        });

        datePicker.setOnMouseClicked(event -> {
            if (datePicker.getValue() == null) {
                datePicker.setValue(earliestDate);
            }
        });
    }

    public static void applyRedBorderToRadioButtons(ToggleGroup toggleGroup) {
        toggleGroup.getToggles().forEach(toggle -> {
            RadioButton radioButton = (RadioButton) toggle;
            radioButton.setStyle(BasicValues.HIGHLIGHT_BORDERS);
        });
    }

    public static void clearRedBorderFromRadioButtons(ToggleGroup toggleGroup) {
        toggleGroup.getToggles().forEach(toggle -> {
            RadioButton radioButton = (RadioButton) toggle;
            radioButton.setStyle("");
        });
    }


}

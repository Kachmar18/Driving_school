package autoschool.autoschool_javafx;

import java.util.*;

public class LanguageManager {
    private static ResourceBundle bundle;
    private static final List<Localisation> registeredWindows = new ArrayList<>();

    public static void init(String language) {
        try {
            Locale locale = new Locale(language);
            bundle = ResourceBundle.getBundle("autoschool.autoschool_javafx.language", locale);
        } catch (MissingResourceException e) {
            System.err.println("Missing resource bundle for language: " + language);
            e.printStackTrace();
        }
    }

    public static String getString(String key) {
        return bundle.getString(key);
    }

    public static String getCurrentLanguage() {
        return bundle.getLocale().getLanguage();
    }

    public static void registerWindow(Localisation window) {
        registeredWindows.add(window);
    }
}

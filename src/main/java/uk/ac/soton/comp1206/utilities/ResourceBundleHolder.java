package uk.ac.soton.comp1206.utilities;

import java.util.Locale;
import java.util.ResourceBundle;

public class ResourceBundleHolder {
  private static String language = "en";
  private static Locale locale = new Locale(language);
  private static ResourceBundle resourceBundle = ResourceBundle.getBundle("text", locale);

  public static ResourceBundle getResourceBundle() {
    return resourceBundle;
  }

  public static void setLanguage(String language) {
    ResourceBundleHolder.language = language;
    ResourceBundleHolder.locale = new Locale(language);
    ResourceBundleHolder.resourceBundle = ResourceBundle.getBundle("text", locale);
  }

  public static String getLanguage() {
    return language;
  }
}


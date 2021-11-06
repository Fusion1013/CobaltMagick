package se.fusion1013.plugin.cobalt.locale;

import java.util.Map;

public interface Locale {
    String getLocaleName();

    String getTranslatorName();

    Map<String, String> getDefaultLocaleString();
}

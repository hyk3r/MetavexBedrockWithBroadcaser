package hu.metavex.broadcaster.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Properties;

public class TranslationManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(TranslationManager.class);
    private static final Properties MESSAGES = new Properties();

    static {
        try (InputStream input = TranslationManager.class.getClassLoader().getResourceAsStream("messages_hu.properties")) {
            if (input == null) {
                LOGGER.error("Sorry, unable to find messages_hu.properties");
            } else {
                MESSAGES.load(new InputStreamReader(input, StandardCharsets.UTF_8));
            }
        } catch (IOException ex) {
            LOGGER.error("Error loading translation file: " + ex.getMessage());
        }
    }

    public static String get(String key, Object... args) {
        String pattern = MESSAGES.getProperty(key);
        if (pattern == null) {
            return "!" + key + "!";
        }
        return MessageFormat.format(pattern, args);
    }
}

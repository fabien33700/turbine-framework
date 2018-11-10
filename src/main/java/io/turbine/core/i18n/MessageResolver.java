package io.turbine.core.i18n;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import static io.turbine.core.utils.Strings.format;
import static java.util.Objects.requireNonNull;

public class MessageResolver {

    private static final boolean RAISE_NOT_EXISTING_KEY = false;
    private static final Logger logger = LoggerFactory.getLogger(MessageResolver.class);
    private static final String MESSAGES_PATH = "";
    private static MessageResolver instance = null;

    public static MessageResolver getInstance() {
        if (instance == null) {
            instance = new MessageResolver();
        }
        return instance;
    }

    private Map<Locale, Properties> localizedMessages = new HashMap<>();

    private MessageResolver() {
        try {
            getLocalizedMessages(Locale.getDefault());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Properties getLocalizedMessages(Locale locale) {
        try {
            return unsafeGetLocalizedMessages(locale);
        } catch (Exception e) {
            logger.error("{}", e);
            return new Properties();
        }
    }

    private Properties unsafeGetLocalizedMessages(Locale locale) throws Exception {
        requireNonNull(locale);

        if (!localizedMessages.containsKey(locale)) {
            Properties messages = new Properties();
            String filename = getMessagesFilename(locale);
            try {
                messages.load(getClass().getClassLoader().getResourceAsStream(filename));
            } catch (Exception ex) {
                if (locale.equals(Locale.getDefault())) {
                    messages = unsafeGetLocalizedMessages(Locale.ENGLISH);
                    logger.warn("No messages file " + filename + " found. Using messages.properties as fallback.");
                } else if (locale.equals(Locale.ENGLISH)) {
                    throw new Exception("You must provide at least a default messages.properties file for " +
                        "application messages internationalization.");
                } else {
                    return localizedMessages.get(Locale.getDefault());
                }
            }
            localizedMessages.put(locale, messages);
            return messages;
        } else {
            return localizedMessages.get(locale);
        }
    }

    private String getMessagesFilename(final Locale locale) {
        if ("en".equals(locale.getLanguage())) {
            return MESSAGES_PATH + "messages.properties";
        } else {
            return MESSAGES_PATH + "messages." + locale.getLanguage() + ".properties";
        }
    }

    public String getMessage(final String key, final Object... args) {
        return getMessage(Locale.getDefault(), key, args);
    }

    public String getMessage(final Locale locale, final String key, final Object... args) {
        requireNonNull(locale);
        if (key == null)
            return null;

        Properties messages = getLocalizedMessages(locale);
        if (!messages.containsKey(key)) {
            if (RAISE_NOT_EXISTING_KEY) {
                throw new IllegalArgumentException("No message found with key '" + key + "' for locale " + locale.getLanguage());
            } else {
                return format(key, args);
            }
        }

        return format(messages.getProperty(key), args);
    }
}

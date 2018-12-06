package io.turbine.core.i18n;

import io.turbine.core.configuration.Reader;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static io.turbine.core.utils.Utils.fromInputStream;
import static io.turbine.core.utils.Utils.Strings.format;
import static java.util.Objects.requireNonNull;

public class MessageResolver {

    private static final boolean RAISE_NOT_EXISTING_KEY = false;
    private static final Logger logger = LoggerFactory.getLogger(MessageResolver.class);
    private static final String MESSAGES_PATH = "";
    private static MessageResolver instance = null;
    private final Reader reader;

    public static MessageResolver getInstance() {
        if (instance == null) {
            instance = new MessageResolver();
        }
        return instance;
    }

    private Map<Locale, JsonObject> localizedMessages = new HashMap<>();

    private MessageResolver() {
        JsonObject messages = new JsonObject();
        try {
            messages = getLocalizedMessages(Locale.getDefault());

        } catch (Exception e) {
            e.printStackTrace();
        }
        reader = new Reader(messages);
    }

    private JsonObject getLocalizedMessages(Locale locale) {
        try {
            return unsafeGetLocalizedMessages(locale);
        } catch (Exception e) {
            logger.error("{}", e);
            return new JsonObject();
        }
    }

    private JsonObject unsafeGetLocalizedMessages(Locale locale) throws Exception {
        requireNonNull(locale);

        if (!localizedMessages.containsKey(locale)) {
            JsonObject messages;
            String filename = getMessagesFilename(locale);
            try {
                final InputStream is = new FileInputStream(Paths.get(filename).toFile());
                messages = new JsonObject(fromInputStream(is));
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

        JsonObject messages = getLocalizedMessages(locale);
        if (!messages.containsKey(key)) {
            if (RAISE_NOT_EXISTING_KEY) {
                throw new IllegalArgumentException("No message found with key '" + key + "' for locale " + locale.getLanguage());
            } else {
                return format(key, args);
            }
        }

        return format(reader.read(key), args);
    }
}

package io.turbine.core.i18n;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static io.turbine.core.utils.Strings.format;
import static java.util.Objects.requireNonNull;

/**
 * A service  to handle internationalization by retrieving appropriate messages
 * from properties file with a key and the current or a specified Locale.
 *
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public class MessageResolverImpl implements MessageResolver {

    /**
     * The class logger
     */
    private static final Logger logger = LoggerFactory.getLogger(MessageResolver.class);

    /**
     * The map that stores messages properties instance for each supported locale.
     */
    private Map<Locale, Properties> localizedMessages = new HashMap<>();

    public MessageResolverImpl() {
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
            return "messages.properties";
        } else {
            return "messages." + locale.getLanguage() + ".properties";
        }
    }

    @Override
    public String getMessage(final String key, final Object... args) {
        return getMessage(Locale.getDefault(), key, args);
    }

    @Override
    public String getMessage(final Locale locale, final String key, final Object... args) {
        requireNonNull(locale);
        if (key == null)
            return null;

        Properties messages = getLocalizedMessages(locale);
        if (!messages.containsKey(key)) {
            /*if (RAISE_NOT_EXISTING_KEY) {
                throw new IllegalArgumentException("No message found with key '" + key + "' for locale " + locale.getLanguage());
            } else {*/
                return format(key, args);
           // }
        }

        return format(messages.getProperty(key), args);
    }

    @Override
    public Set<Locale> getSupportedLocales() {
        return null;
    }

    @Override
    public Locale defaultLocale() {
        return null;
    }
}

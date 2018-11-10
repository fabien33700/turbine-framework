package io.turbine.core.i18n;

import java.util.Locale;
import java.util.Set;

/**
 * Describes the behavior for a service that can dispatch translated
 * messages from properties file with a key and the current or a specified Locale.
 *
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public interface MessageResolver {
    /**
     * Resolve the message for the given key, replacing all placeholders
     * with given arguments.
     * @param key The key of the message
     * @param args The arguments to include in the message
     * @return The resolved message
     */
    String getMessage(String key, Object... args);

    /**
     * Resolve the message for the given key, in the specified locale
     * replacing all placeholder with given arguments.
     * @param locale The locale in which resolve the message
     * @param key The key of the message
     * @param args The arguments to include in the message
     * @return The resolved message
     */
    String getMessage(Locale locale, String key, Object... args);

    /**
     * Returns all locales successfully load by the MessageResolver.
     * @return A set of Locale
     */
    Set<Locale> getSupportedLocales();

    /**
     * Defines the locale selected as a fallback if getMessage() is called
     * wwith an unsupported locale or if the associated MessageProvider failed.
     * @return A Locale instance
     */
    Locale defaultLocale();
}

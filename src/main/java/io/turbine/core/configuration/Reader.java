package io.turbine.core.configuration;

import io.vertx.core.json.JsonObject;

import java.util.NoSuchElementException;

/**
 * A utility class to read Json configuration with dot-separated access path.
 *
 * Instead of recursively getting nested JsonObject from whole configuration,
 * developer specifies path like "web.router.path" to retrieve 'path' value from
 * JsonObject with key 'router' in JsonObject with key 'web', which prevents him
 * to call :
 *
 *  <code>configuration.getJsonObject("web").getJsonObject("router").getValue("path")</code>
 *
 * and especially to deal with <code>null</code> value if the <code>JsonObject</code> hierarchy, like this :
 *
 *  <code>
 *      String defaultRoute = "/customer/xxx";
 *      String path = null;
 *      JsonObject web = configuration.getJsonObject("web");
 *      if (web != null) {
 *          JsonObject router = web.getJsonObject("router");
 *          if (router != null) {
 *              path = router.getValue("path");
 *          }
 *      }
 *
 *      if (path == null) {
 *          path = defaultRoute;
 *      }
 *  </code>
 *
 *  Instead, (s)he will be able to use <code>reader.read("web.router.path", "/customer/xxx");</code>
 *
 *  IMPORTANT ! Given that we use dot (.) to separate each node of the path, the reader is NOT compatible
 *  with configuration JsonObject that contains property keys containing themselves the dot character (.)
 *
 *  e.g. A property with key 'ssl.tls.certificates'
 *
 * @see JsonObject
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public class Reader {

    /**
     * The global configuration
     */
    private final JsonObject configuration;

    /**
     * Reader constructor.
     * @param configuration The configuration to inject to the reader
     */
    public Reader(final JsonObject configuration) {
        this.configuration = configuration;
    }

    /**
     * Read a property from the configuration.
     * If the property does not exist, it returns the default value.
     * @param path A dot-separated access path to the property
     * @param defaultValue The default value if no property was found for the given key
     * @param <T> The type of the value to read (inferred with the given default value)
     * @return The property value
     */
    @SuppressWarnings("unchecked")
    public <T> T read(final String path, T defaultValue) {
        try {
            return (T) readObject(path, defaultValue);
        } catch (ClassCastException ex) {
            throw new UnsupportedOperationException("The read property type is not compatible with default value one.");
        }
    }

    /**
     * Read a property from the configuration.
     * If the proeprty does not exist, it throws an exception.
     * @param path A dot-separated access path to the property
     * @param <T> The type of the value to read (inferred with the given default value)
     * @throws NoSuchElementException Thrown if no property was found for the given path
     * @return The property value
     */
    @SuppressWarnings("unchecked")
    public <T> T read(String path) throws NoSuchElementException {
        try {
            T value = (T) readObject(path, null);
            if (value == null) {
                throw new NoSuchElementException("There is no property with path '" + path + "'.");
            }
            return value;
        } catch (ClassCastException ex) {
            throw new UnsupportedOperationException("The read property type is not compatible with default value one.");
        }
    }

    /**
     * Internal non-generic property reading method.
     * @param path A dot-separated access path to the property
     * @param defaultValue The default value if no property was found for the given key
     * @return The property value
     */
    private Object readObject(String path, Object defaultValue) {
        // Splitting access path in parts
        String[] parts = path.split("\\.");

        // At the beginning, we point the global configuration root
        JsonObject currentNode = configuration;

        // For each path parts except the last
        for (int i = 0; i < parts.length - 1; i++) {
            final String part = parts[i];
            // The current node became the nested JsonObject with key matching to the current path part
            currentNode = currentNode.getJsonObject(part);

            // If no node was found, we returns the default value
            if (currentNode == null) {
                return defaultValue;
            }
        }
        // For the last part, we retrieve the value
        return currentNode.getValue(parts[parts.length-1], defaultValue);
    }
}

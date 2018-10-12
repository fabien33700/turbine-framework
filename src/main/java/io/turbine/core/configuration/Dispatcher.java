package io.turbine.core.configuration;

import io.vertx.core.json.JsonObject;

import java.util.Set;

import static io.turbine.core.utils.GeneralUtils.getClassHierarchy;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toSet;

/**
 * The configuration dispatcher provides scoped configuration fragments
 * from the global configuration file to subclasses of BaseVerticle.
 *
 * In the configuration file, top-level tuples are provided to the matching class,
 * according to the \@Configuration annotation.
 *
 * The Configuration annotation docstrings describe the matching rules between
 * classes and configuration fragments.
 *
 * @see Configuration
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public class Dispatcher {

    /**
     * The dispatcher instance.
     * TODO: We use singleton for the moment, going to use DI in future (creation of a Dispatcher interface)
     */
    private static Dispatcher instance = null;

    /**
     * Private constructor to prevent instanciation from outside (Singleton).
     */
    private Dispatcher() {}

    /**
     * Get the instance of the Dispatcher, creating it before if it isn't.
     * @return The dispatcher instance
     */
    public static Dispatcher getInstance() {
        if (instance == null) {
            instance = new Dispatcher();
        }
        return instance;
    }

    /**
     * Dispatch the right configuration fragment.
     * @param baseConfig The base configuration JsonObject, usually provide by the Verticle launcher, or Vert.x
     * @param clazz The class to supply the configuration
     * @return A JsonObject of the supplied configuration fragment. If no matching fragment was found,
     *   it returns an empty JsonObject ( = {} in JSON)
     */
    public JsonObject dispatch(JsonObject baseConfig, Class<?> clazz) {
        requireNonNull(baseConfig);

        // The resulting configuration fragment
        JsonObject subConfig = new JsonObject();

        // Iterating over the class configuration hierarchy
        for (String configKey : getConfigurationHierarchy(clazz)) {
            // If the base configuration contains the current key ...
            if (baseConfig.containsKey(configKey)) {
                // ... merging this fragment with the result
                subConfig = baseConfig.getJsonObject(configKey).mergeIn(subConfig);
            }
        }

        return subConfig;
    }

    /**
     * Build the configuration hierarchy, which means the set of configuration keys
     * that target a base class. The targeting rules are more precisely detailled in the configuration
     * class documentation.
     * @param baseClass The class for which retrieve the hierarchy
     * @return The class' configuration hierarchy
     */
    private Set<String> getConfigurationHierarchy(Class<?> baseClass) {
        return
            // retrieving the inheritance hierarchy of the class
            getClassHierarchy(baseClass).stream()
                // keeping classes annotated with @Configuration
                .filter(clazz -> clazz.isAnnotationPresent(Configuration.class))
                // getting the key from their annotation
                .map(this::getConfigurationKey)
                .collect(toSet());
    }

    /**
     * Retrieves the configuration key of a class.
     * @param clazz The class from which retrieve the key
     * @return The configuration key, or the class name if empty
     */
    private String getConfigurationKey(Class<?> clazz) {
        String key = clazz.getAnnotation(Configuration.class).key();
        return key.isEmpty() ? clazz.getSimpleName() : key;
    }
}

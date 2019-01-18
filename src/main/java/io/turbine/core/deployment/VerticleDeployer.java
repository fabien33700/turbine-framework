package io.turbine.core.deployment;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.functions.BiConsumer;
import io.turbine.core.configuration.Dispatcher;
import io.turbine.core.deployment.annotations.Inject;
import io.turbine.core.deployment.annotations.Injectable;
import io.turbine.core.errors.exceptions.verticles.ConfigurationException;
import io.turbine.core.verticles.behaviors.Verticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static io.reactivex.Completable.concat;
import static io.reactivex.Completable.fromSingle;
import static io.turbine.core.utils.Utils.Reflection.setFieldValue;
import static io.turbine.core.utils.Utils.fromInputStream;
import static io.turbine.core.utils.Utils.orElse;

/**
 * A singleton class to deploy verticles.
 * It can be used statically by Turbine utility class or by Verticles themselves
 * to deploy some children verticles.
 *
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public final class VerticleDeployer {

    /**
     * The single instance of the deployer
     */
    private static VerticleDeployer instance;

    /**
     * The default configuration file path
     */
    private static final String DEFAULT_CONFIG_PATH = "configuration.json";

    /**
     * The logger
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * The current Vert.x instance
     */
    private final Vertx vertx = Vertx.vertx();

    /**
     * A configuration dispatcher
     */
    private final Dispatcher dispatcher = new Dispatcher();

    /**
     * The arguments given to the application (in CLI mode)
     */
    private final String[] args;

    private final Map<Class<? extends Verticle>, Verticle> singletons = new ConcurrentHashMap<>();

    /**
     * A singleton instance getter for the deployer.
     *
     * @param args The arguments given to the application
     * @return The unique VerticleDeployer instance
     */
    static VerticleDeployer getDeployer(String[] args) {
        if (instance == null) {
            instance = new VerticleDeployer(args);
        }
        return instance;
    }

    /**
     * A singleton instance getter for the deployer.
     *
     * @return The unique VerticleDeployer instance
     */
    static VerticleDeployer getDeployer() {
        return getDeployer(new String[]{});
    }

    /**
     * Private constructor (used for singleton)
     *
     * @param args The arguments given to the application
     */
    private VerticleDeployer(String[] args) {
        this.args = args;
    }

    /**
     * Deploy a verticle for each verticle classes in arguments.
     *
     * @param classes An array of verticle classes
     */
    public void deployVerticles(Class<?>[] classes) {
        for (Class<?> clazz : classes)
            deployVerticle(clazz);
    }

    /**
     * Deploy a verticle from its class.
     *
     * @param clazz The verticle class
     */
    @SuppressWarnings("unchecked")
    private void deployVerticle(Class<?> clazz) {
        if (!Verticle.class.isAssignableFrom(clazz)) {
            throw new RuntimeException("Unable to deploy verticle " + clazz.getName());
        }

        logger.info("Deploying verticle {}", clazz.getName());

        deployVerticle((Class<? extends Verticle>) clazz, null)
                .doOnError(t -> logger.error("Unable to deploy verticle " + clazz.getName(), t))
                .subscribe();
    }

    private boolean isInjectableVerticle(Class<?> clazz) {
        return Verticle.class.isAssignableFrom(clazz) && clazz.isAnnotationPresent(Injectable.class);
    }

    private boolean isSingletonVerticle(Class<? extends Verticle> verticleClass) {
        return isInjectableVerticle(verticleClass)
                && verticleClass.getAnnotation(Injectable.class).singleton();
    }

    /**
     * Create or retrieve a verticle from the injection container (if the verticle class is defined
     * as singleton with the @Injectable annotation).
     * @param <V> The type of the Verticle
     * @param factory The verticle factory
     * @return A verticle instance
     * @throws Exception A problem occured during the creation/retrieving of the instance
     */
    @SuppressWarnings("unchecked")
    private <V extends Verticle> V createVerticle(Factory<V> factory) throws Exception {
        final V verticle = factory.create();
        final Class<V> vType = (Class<V>) verticle.getClass();
        if (isInjectableVerticle(vType) && isSingletonVerticle(vType)) {
            // There is no singleton instance for this class yet, instanciating it
            if (!singletons.containsKey(vType)) {
                singletons.put(vType, verticle);
            }
            return (V) singletons.get(vType);
        }

        // In other cases (not Injectable, not singleton), use the factory to create the instance.
        return verticle;
    }

    /**
     * Deploy a verticle from its class, with given configuration.
     * Verticle is instanciated from the given verticle factory.
     *
     * @param verticleClass The verticle class
     * @param config        The verticle base configuration
     * @param <V>           The type of the Verticle
     * @return A single of freshly-deployed Verticle instance
     */
    public final <V extends Verticle>
    Single<V> deployVerticle(Class<V> verticleClass, JsonObject config) {
        return deployVerticle(verticleClass::newInstance, config);
    }

    /**
     * Instanciate, resolve dependencies and deploy into Vert.x a verticle
     * from its class with given configuration.
     * Verticle is instanciated from the given verticle factory.
     *
     * @param factory       The factory to instanciate the verticle
     * @param config        The verticle base configuration
     * @param <V>           The type of the Verticle
     * @return A single of freshly-deployed Verticle instance
     */
    @SuppressWarnings("unchecked")
    public final <V extends Verticle>
    Single<V> deployVerticle(Factory<V> factory, JsonObject config) {
        // Verticle instanciation/retrieving from the container
        V verticle;
        try {
            verticle = createVerticle(factory);
        } catch (Exception ex) {
            return Single.error(ex);
        }
        Class<V> verticleType = (Class<V>) verticle.getClass();

        // Getting the configuration wrapped in deployment options.
        DeploymentOptions options = getConfigurationOptions(verticleType, config);

        // For each field injection, we'll add a completable that will fire when the
        // nested injecion process for the dependency will finish
        List<Completable> injected = new ArrayList<>();

        // Injecting each field
        for (Field field : verticleType.getDeclaredFields()) {
            // Field to inject must have @Inject annotation
            if (field.isAnnotationPresent(Inject.class)) {
                // The target field type must be a Verticle and @Injectable
                if (!isInjectableVerticle(field.getType())) {
                    return Single.error(new IllegalArgumentException(field.getType().getName() + " cannot be injected in a new "
                            + verticleType.getName() + " instance because it is neither @Injectable nor a Verticle."));
                }

                // Injector = the function that take the parent verticle and inject to it the child verticle (dependency)
                BiConsumer<V, Verticle> injector =
                        (v, dependency) -> setFieldValue(verticle, field, dependency);

                // Deploying the dependency (recursive call)
                Single<Verticle> dependency = deployVerticle((Class<Verticle>) field.getType(), null)
                        .map(dep -> {
                            // When the deployment is finished, we call the injector to put the dependency
                            // reference into the upper verticle
                            injector.accept(verticle, dep);
                            // Reversely set the dependency parent
                            dep.setParent(verticle);

                            // Retro injecting parent to suitable field : if the dependency verticle class
                            // has a field which type matches to the upper verticle type, we bound the parent reference to it.
                            Optional<Field> parentField = Stream.of(field.getType().getDeclaredFields())
                                    .filter(f -> f.getType().equals(verticleType))
                                    .findFirst();

                            if (parentField.isPresent()) {
                                setFieldValue(dep, parentField.get(), verticle);
                            }
                            return dep;
                        });
                injected.add(fromSingle(dependency));
            }
        }
        // Create a custom Single from an emitter
        return Single.create(emitter ->
                // When all dependencies fields injection for the verticle are terminated ...
                // (nested call to this method)
                concat(injected).subscribe(() ->
                // ... we deploy the upper verticle itself
                vertx.deployVerticle(() -> verticle, options, async -> {
                    if (async.failed()) {
                        // raising the error cause
                        emitter.onError(async.cause());
                    } else {
                        // emitting the successfully deployed verticle
                        emitter.onSuccess(verticle);
                    }
                })));
    }

    private <V extends Verticle>
    DeploymentOptions getConfigurationOptions(Class<V> verticleClass,
                                              JsonObject baseConfiguration) {
        JsonObject config = orElse(baseConfiguration, new JsonObject());

        try {
            // Dispatch the verticle configuration according to its class
            // and merging it with additionnal configuration
            config = dispatcher.dispatch(readConfiguration(), verticleClass)
                    .mergeIn(config);
        } catch (ConfigurationException ex) {
            logger.warn("Error reading configuration", ex);
        }

        DeploymentOptions options = new DeploymentOptions();
        options.setConfig(config);

        return options;
    }


    /**
     * Try to read the configuration from the specified configuration file.
     *
     * @return A JsonObject containing the read configuration
     * @throws ConfigurationException An error occured while reading/parsing the configuration file
     * @see VerticleDeployer#getConfigurationPath()
     */
    private JsonObject readConfiguration() throws ConfigurationException {
        String configPath = getConfigurationPath();

        if (configPath != null) {
            try {
                final InputStream is = new FileInputStream(Paths.get(configPath).toFile());
                return new JsonObject(fromInputStream(is));
            } catch (FileNotFoundException e) {
                throw new ConfigurationException(e);
            }
        } else {
            throw new ConfigurationException("No specified configuration file.");
        }
    }

    /**
     * Try to resolve the configuration filepath from the CLI arguments,
     * returning DEFAULT_CONFIG_PATH otherwise.
     *
     * @return The configuration filepath
     */
    private String getConfigurationPath() {
        final CommandLineParser parser = new DefaultParser();
        final Options options = new Options()
                .addOption("c", "conf", true, "Specify a JSON file for configuration.");

        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption('c')) {
                return cmd.getOptionValue('c');
            }
        } catch (ParseException ex) {
            logger.error("Could not launch the application.", ex);
        }
        return DEFAULT_CONFIG_PATH;
    }

}

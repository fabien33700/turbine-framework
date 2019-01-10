package io.turbine.core.deployment;

import io.reactivex.Single;
import io.turbine.core.configuration.Dispatcher;
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
import java.nio.file.Paths;

import static io.turbine.core.deployment.VerticleFactory.factory;
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

    /**
     * A singleton instance getter for the deployer.
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
     * @return The unique VerticleDeployer instance
     */
    static VerticleDeployer getDeployer() {
        return getDeployer(new String[] {});
    }

    /**
     * Private constructor (used for singleton)
     * @param args The arguments given to the application
     */
    private VerticleDeployer(String[] args) {
        this.args = args;
    }

    /**
     * Deploy a verticle for each verticle classes in arguments.
     * @param classes An array of verticle classes
     */
    public void deployVerticles(Class<?>[] classes) {
        for (Class<?> clazz : classes)
            deployVerticle(clazz);
    }

    /**
     * Deploy a verticle from its class.
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

    /**
     * Deploy a verticle from its class, with given configuration.
     * Verticle is instanciated from the default constructor of its class.
     * @param verticleClass The class of the verticle to deploy
     * @param config The verticle base configuration
     * @param <V> The type of the Verticle
     * @return A single of freshly-deployed Verticle instance
     */
    public final <V extends Verticle>
    Single<V> deployVerticle(Class<V> verticleClass, JsonObject config) {
        return deployVerticle(factory(verticleClass), config);
    }

    /**
     * Deploy a verticle from its class, with given configuration.
     * Verticle is instanciated from the given verticle factory.
     * @param factory The factory used to create the Verticle instance
     * @param config The verticle base configuration
     * @param <V> The type of the Verticle
     * @return A single of freshly-deployed Verticle instance
     */
    public final <V extends Verticle>
    Single<V> deployVerticle(VerticleFactory<V> factory, JsonObject config) {
        V verticle;
        try {
            verticle = factory.create();
        } catch (Exception ex) {
            // The factory could not instanciate a correct verticle instance
            Throwable t = new RuntimeException("The factory " + factory +
                    " failed to instanciate a verticle.", ex);
            return Single.error(t);
        }

        config = orElse(config, new JsonObject());

        try {
            // Dispatch the verticle configuration according to its class
            // and merging it with additionnal configuration
            config = dispatcher.dispatch(readConfiguration(), verticle.getClass())
                    .mergeIn(config);
        } catch (ConfigurationException ex) {
            logger.warn("Error reading configuration", ex);
        }

        DeploymentOptions options = new DeploymentOptions();
        options.setConfig(config);

        // Create the single from the Vert.x deployment result
        return Single.create(emitter ->
            vertx.deployVerticle(() -> verticle, options, async -> {
                if (async.failed()) {
                    // raising the error cause
                    emitter.onError(async.cause());
                } else {
                    // emitting the freshly-deployed verticle
                    emitter.onSuccess(verticle);
                }
            }
        ));
    }

    /**
     * Try to read the configuration from the specified configuration file.
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

package io.turbine.core.deployment;

import io.turbine.core.errors.exceptions.verticles.ConfigurationException;
import io.turbine.core.verticles.BaseVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Map;

import static io.turbine.core.deployment.VerticleFactory.fromClass;
import static io.turbine.core.utils.Utils.fromInputStream;
import static io.vertx.core.Vertx.vertx;
import static java.util.Objects.requireNonNull;

public final class VerticleDeployer {

    private static final String DEFAULT_CONFIG_PATH = "configuration.json";

    private static VerticleDeployer INSTANCE = null;

    private VerticleDeployer(String[] args) {
        requireNonNull(args, "application argument");
        this.args = args;
    }

    public static VerticleDeployer getInstance() {
        return INSTANCE;
    }

    public static VerticleDeployer getInstance(String[] args) {
        if (INSTANCE == null) {
            INSTANCE = new VerticleDeployer(args);
        }
        return INSTANCE;
    }

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final String[] args;

    /*public void deployVerticles(Class<?>[] classes) {
        for (Class<?> clazz : classes)
            deployVerticle(clazz);
    }

    @SuppressWarnings("unchecked")
    private BaseVerticle deployVerticle(Class<?> clazz) {
        if (!BaseVerticle.class.isAssignableFrom(clazz)) {
            throw new RuntimeException("Unable to deploy verticle " + clazz.getName());
        }

        return deployVerticle(fromClass((Class<BaseVerticle>) clazz));
    }

    public <V extends BaseVerticle> V deployVerticle(VerticleFactory<V> factory) {
        return deployVerticle(factory, new JsonObject());
    }

    public <V extends BaseVerticle> V deployVerticle(VerticleFactory<V> factory, JsonObject parameters) {
        logger.info("Deploying verticle {}", factory.verticleName());

        DeploymentOptions options = new DeploymentOptions();
        try {
            options.setConfig(readConfiguration());
        } catch (ConfigurationException ex) {
            options.setConfig(new JsonObject());
            logger.warn("Error reading configuration", ex);
        }

        V verticle = factory.get();
        vertx().getDelegate().deployVerticle(() -> verticle, options);

        return verticle;

        /*final Vertx vertx = vertx();

        vertx.getDelegate().deployVerticle(factory::get, options, asyncResult ->
        {
            final String deploymentID = asyncResult.result();
            if (parameters != null && !parameters.isEmpty()) {
                vertx.eventBus().send("/" + deploymentID, parameters);
            }
            logger.info("Deployed verticle {} with ID {}", factory.verticleName(), deploymentID);
        });
    }*/


    @SuppressWarnings("unchecked")
    public final <V extends BaseVerticle> void deployVerticle(Class<?>... classes) {
        for (Class<?> clazz : classes) {
            if (BaseVerticle.class.isAssignableFrom(clazz)) {
                deployVerticle((Class<V>) clazz);
            }
        }
    }


    public final <V extends BaseVerticle> void deployVerticle(Class<V> verticleClass) {
        deployVerticle(fromClass(verticleClass), new JsonObject().getMap());
    }

    public final <V extends BaseVerticle>
    V deployVerticle(VerticleFactory<V> verticleFactory, Map<String, Object> parameters) {
        logger.info("Deploying verticle {}", verticleFactory.verticleName());

        DeploymentOptions options = new DeploymentOptions();
        try {
            options.setConfig(readConfiguration());
        } catch (ConfigurationException ex) {
            options.setConfig(new JsonObject());
            logger.warn("Error reading configuration", ex);
        }

        V verticle = verticleFactory.get();
        verticle.inject(parameters);

        vertx().deployVerticle(() -> verticle, options);
        return verticle;
    }

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

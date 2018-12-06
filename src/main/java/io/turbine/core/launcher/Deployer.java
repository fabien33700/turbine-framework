package io.turbine.core.launcher;

import io.turbine.core.errors.exceptions.verticles.ConfigurationException;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static io.turbine.core.utils.Utils.fromInputStream;
import static io.vertx.reactivex.core.Vertx.vertx;

public final class Deployer implements Runnable {

    private static final String DEFAULT_CONFIG_PATH = "configuration.json";

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final String[] args;
    private final Class<?>[] classes;

    protected Deployer(String[] args, Class<?>... classes) {
        this.args = args;
        this.classes = classes;
    }

    public void run() {
        deployVerticles();
    }

    private void deployVerticles() {
        Stream.of(classes)
            .filter(this::checkVerticle)
            .forEach(this::deployVerticle);
    }

    private void deployVerticle(Class<?> clazz) {
        logger.info("Deploying verticle " + clazz.getName());

        DeploymentOptions options = new DeploymentOptions();
        try {
            options.setConfig(readConfiguration());
        } catch (ConfigurationException ex) {
            options.setConfig(new JsonObject());
            logger.warn("Error reading configuration", ex);
        }
        vertx().deployVerticle(clazz.getName(), options);
    }

    private boolean checkVerticle(Class<?> verticleClass) {
        return AbstractVerticle.class.isAssignableFrom(verticleClass);
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

package io.turbine.core.launcher;

import io.turbine.core.logging.Logger;
import io.turbine.core.logging.LoggerFactory;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import org.apache.commons.cli.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static io.turbine.core.utils.GeneralUtils.fromInputStream;
import static io.vertx.reactivex.core.Vertx.vertx;

public abstract class VerticleLauncher implements Runnable {

    private Verticles verticles = new Verticles();

    protected class Verticles {
        private List<Class<?>> classes = new ArrayList<>();

        public Verticles deploy(Class<?> verticleClass) {
            classes.add(verticleClass);
            return this;
        }

        private List<Class<?>> toDeploy() {
            return classes;
        }
    }

    private static final String DEFAULT_CONFIG_PATH = "default.configuration.json";

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private static JsonObject config = null;

    private final String[] args;

    protected VerticleLauncher(String[] args) {
        this.args = args;
        if (config == null)
            config = readConfiguration();
    }

    public void run() {
        deployVerticles();
    }

    private void deployVerticles() {
        toDeploy(verticles);
        verticles.toDeploy().stream()
            .filter(this::checkVerticle)
            .forEach(this::deployVerticle);
    }

    private void deployVerticle(Class<?> clazz) {
        logger.info("Deploying verticle " + clazz.getName());

        DeploymentOptions options = new DeploymentOptions();
        options.setConfig(config);
        vertx().deployVerticle(clazz.getName(), options);
    }

    private boolean checkVerticle(Class<?> verticleClass) {
        return AbstractVerticle.class.isAssignableFrom(verticleClass);
    }

    //protected abstract Class<?>[] toDeploy();
    protected abstract void toDeploy(Verticles verticles);

    private JsonObject readConfiguration() {
        String configPath = getConfigurationPath();
        if (configPath != null) {
            try {
                final InputStream is = new FileInputStream(Paths.get(configPath).toFile());
                return new JsonObject(fromInputStream(is));
            } catch (FileNotFoundException e) {
                logger.warn("The specified configuration file {} was not found. " +
                    "Using default configuration instead.", configPath);
            }
        } else {
            logger.warn("No specified configuration file. " +
                "Using default configuration instead.");
        }
        return readDefaultConfiguration();
    }

    private JsonObject readDefaultConfiguration() {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(DEFAULT_CONFIG_PATH);
            return new JsonObject(fromInputStream(is));
        } catch (Exception ex) {
            logger.error("Fatal ! Unable to read default configuration in {}", DEFAULT_CONFIG_PATH, ex);
            return new JsonObject();
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
        return null;
    }

}

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

import static io.turbine.core.utils.Utils.fromInputStream;
import static io.turbine.core.utils.Utils.orElse;

public final class VerticleDeployer {

    private static VerticleDeployer instance;



    private static final String DEFAULT_CONFIG_PATH = "configuration.json";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Vertx vertx = Vertx.vertx();

    private final Dispatcher dispatcher = new Dispatcher();

    private final String[] args;

    static VerticleDeployer getDeployer(String[] args) {
        if (instance == null) {
            instance = new VerticleDeployer(args);
        }
        return instance;
    }

    static VerticleDeployer getDeployer() {
        return getDeployer(new String[] {});
    }

    private VerticleDeployer(String[] args) {
        this.args = args;
    }

    public void deployVerticles(Class<?>[] classes) {
        for (Class<?> clazz : classes)
            deployVerticle(clazz);
    }

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


    /*public <V extends BaseVerticle> V deployVerticle(VerticleFactory<V> factory, JsonObject parameters) {
        logger.info("Deploying verticle {}", factory.name());

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
            logger.info("Deployed verticle {} with ID {}", factory.name(), deploymentID);
        });
    }*/

/*
    @SuppressWarnings("unchecked")
    public final <V extends BaseVerticle> void deployVerticle(Class<?>... classes) {
        for (Class<?> clazz : classes) {
            if (BaseVerticle.class.isAssignableFrom(clazz)) {
                deployVerticle((Class<V>) clazz);
            }
        }
    }


    public final <V extends BaseVerticle> void deployVerticle(Class<V> verticleClass) {
        deployVerticle(from(verticleClass), new JsonObject().getMap());
    }*/

    public final <V extends Verticle>
    Single<V> deployVerticle(Class<V> verticleClass, JsonObject config) {
        return deployVerticle(verticleClass::newInstance, verticleClass, config);
    }

    public final <V extends Verticle>
    Single<V> deployVerticle(VerticleFactory<V> factory, Class<V> verticleClass, JsonObject config) {
        config = orElse(config, new JsonObject());

        try {
            config = dispatcher.dispatch(readConfiguration(), verticleClass)
                    .mergeIn(config);
        } catch (ConfigurationException ex) {
            logger.warn("Error reading configuration", ex);
        }

        DeploymentOptions options = new DeploymentOptions();
        options.setConfig(config);

        try {
            V verticle = factory.create();
            return Single.create(emitter ->
                vertx.deployVerticle(() -> verticle, options, async -> {
                    if (async.failed()) {
                        emitter.onError(async.cause());
                    } else {
                        emitter.onSuccess(verticle);
                    }
                }
            ));
        } catch (Exception ex) {
            Throwable t = new RuntimeException("Could not deploy " + verticleClass.getSimpleName()
                    + " verticle.", ex);
            return Single.error(t);
        }
    }

    /*public final <V extends BaseVerticle>
    V deployVerticle(VerticleFactory<V> verticleFactory, Map<String, Object> parameters) {
        logger.info("Deploying verticle {}", verticleFactory.name());

        DeploymentOptions options = new DeploymentOptions();
        try {
            options.setConfig(readConfiguration());
        } catch (ConfigurationException ex) {
            options.setConfig(new JsonObject());
            logger.warn("Error reading configuration", ex);
        }

        V verticle = verticleFactory.get();
        vertx().deployVerticle(() -> verticle, options, (id) -> verticle.inject(parameters));
        return verticle;
    }*/

   /* public <V extends BaseVerticle>
    Single <V> deployVerticle(VerticleFactory<V> verticleFactory, Map<String, Object> parameters) {
        logger.info("Deploying verticle {}", verticleFactory.name());

        DeploymentOptions options = new DeploymentOptions();
        try {
            options.setConfig(readConfiguration());
        } catch (ConfigurationException ex) {
            options.setConfig(new JsonObject());
            logger.warn("Error reading configuration", ex);
        }

        Future<V> future =
        V verticle = verticleFactory.get();
        vertx().deployVerticle(() -> verticle, options, (id) -> verticle.inject(parameters));
        return Single.fromFuture(future);
    }*/



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

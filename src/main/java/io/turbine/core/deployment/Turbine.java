package io.turbine.core.deployment;

public abstract class Turbine {

    public static void runApplication(String[] args, Class<?>... classes) {
        VerticleDeployer.getInstance(args).deployVerticle(classes);
    }

    public static void runApplication(Class<?>... classes) {
        runApplication(new String[]{}, classes);
    }
}

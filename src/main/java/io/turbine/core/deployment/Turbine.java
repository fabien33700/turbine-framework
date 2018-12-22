package io.turbine.core.deployment;

public abstract class Turbine {

    private static VerticleDeployer deployer;

    public static void runApplication(String[] args, Class<?>... classes) {
        if (deployer == null) {
            deployer = VerticleDeployer.getDeployer(args);
        }
        deployer.deployVerticles(classes);
    }

    public static void runApplication(Class<?>... classes) {
        runApplication(new String[]{}, classes);
    }

    public static VerticleDeployer getDeployer() {
        return VerticleDeployer.getDeployer();
    }
}

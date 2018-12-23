package io.turbine.core.deployment;

/**
 * An utility class that contains helper methods to run verticles with Turbine.
 * It can be considered as Turbine framework's entry point.
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public abstract class Turbine {

    /**
     * The verticle deployer singleton instance
     */
    private static VerticleDeployer deployer;

    /**
     * Run the application.
     * @param args The application CLI arguments
     * @param classes The verticles classes' to deploy
     */
    public static void runApplication(String[] args, Class<?>... classes) {
        if (deployer == null) {
            deployer = VerticleDeployer.getDeployer(args);
        }
        deployer.deployVerticles(classes);
    }

    /**
     * Run the application.
     * @param classes The verticles classes' to deploy
     */
    public static void runApplication(Class<?>... classes) {
        runApplication(new String[]{}, classes);
    }

    /**
     * Singleton instance getter.
     * @return The unique deployer instance
     */
    public static VerticleDeployer getDeployer() {
        return VerticleDeployer.getDeployer();
    }
}

package io.turbine.core.launcher;

public abstract class Turbine {

    public static void runApplication(String[] args, Class<?>... classes) {
        new Deployer(args, classes).run();
    }

    public static void runApplication(Class<?>... classes) {
        runApplication(new String[]{}, classes);
    }
}

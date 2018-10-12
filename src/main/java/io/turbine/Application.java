package io.turbine;

import io.turbine.core.launcher.VerticleLauncher;

public class Application extends VerticleLauncher {
    protected Application(String[] args) {
        super(args);
    }

    @Override
    public void toDeploy(Verticles verticles) {
        verticles.deploy(MyVerticle.class);
    }

    public static void main(String[] args) {
        new Application(args).run();
    }
}

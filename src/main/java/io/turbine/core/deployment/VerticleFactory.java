package io.turbine.core.deployment;

import io.turbine.core.verticles.behaviors.Verticle;

/*public interface VerticleFactory<V extends Verticle> extends Holder<V> {

    String name();

    static <V extends Verticle> VerticleFactory<V>
    from(String name, Supplier<V> supplier) {
        return new VerticleFactory<V>() {
            @Override
            public String name() {
                return name;
            }

            private Holder<V> verticleHolder = Holders.from(supplier);

            @Override
            public V get() {
                return verticleHolder.get();
            }
        };
    }

    static <V extends Verticle> VerticleFactory<V>
    from(Class<V> verticleClass) {
        requireNonNull(verticleClass, "verticle class");
        if (!Verticle.class.isAssignableFrom(verticleClass)) {
            throw new IllegalArgumentException("Verticle class must be inherited from Verticle");
        }
        return from(verticleClass.getName(), () -> {
            try {
                return verticleClass.newInstance();
            } catch (Exception ex) {
                throw new RuntimeException("Cannot create a verticle from class "
                        + verticleClass.getName(), ex);
            }
        });
    }

}*/

public interface VerticleFactory<V extends Verticle>  {
    V create() throws Exception;
}

package io.turbine.core.verticles.behaviors;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.turbine.core.deployment.VerticleFactory;
import io.turbine.core.utils.Composite;
import io.vertx.core.json.JsonObject;

public interface Verticle extends io.vertx.core.Verticle, Composite<Verticle> {

   /*default <V extends BaseVerticle> V deployVerticle(Class<V> verticleClass) {
        return deployVerticle(verticleClass, new HashMap<>());
    }

    default <V extends BaseVerticle> V deployVerticle(Supplier<V> verticleSupplier) {
        return deployVerticle(verticleSupplier, new HashMap<>());
    }*/

    Completable rxStart();

    Completable rxStop();

    <V extends Verticle> Single<V> deployVerticle(Class<V> verticleClass, JsonObject config);

    <V extends Verticle>
    Single<V> deployVerticle(VerticleFactory<V> factory, Class<V> verticleClass, JsonObject config);


    // <V extends BaseVerticle> Single<V> deployVerticle(Supplier<V> verticleSupplier, Map<String, Object> parameters);

    //void inject(Map<String, Object> params);
}

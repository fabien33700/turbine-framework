package io.turbine.core.verticles.lifecycle;

import io.reactivex.Completable;
import io.turbine.core.errors.exceptions.verticles.InitializationException;
import io.turbine.core.verticles.BaseVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class InitializationChain {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final List<Completable> chain;

    private final WeakReference<? extends BaseVerticle> verticleRef;

    public InitializationChain(BaseVerticle verticle) {
        this.verticleRef = new WeakReference<>(verticle);
        this.chain = new LinkedList<>();
    }

    public InitializationChain append(Completable completable) {
        chain.add(completable);
        return this;
    }

    public Completable toCompletable() {
        BaseVerticle verticle = requireNonNull(verticleRef.get());
        return chain.stream()
                .reduce(Completable::andThen)
                .orElseThrow(() -> new InitializationException(
                        new IllegalStateException("Could not aggregate verticle hierarchy initialization completables"),
                        requireNonNull(verticle)
                ))
                .doOnComplete(() -> logger.info("Verticle {} is READY !",
                        verticle.getClass().getSimpleName()));
    }
}

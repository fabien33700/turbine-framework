package io.turbine.core.web.router;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;
import io.turbine.core.logging.Logger;
import io.turbine.core.logging.LoggerFactory;
import io.vertx.core.Handler;
import io.vertx.reactivex.ext.web.Route;
import io.vertx.reactivex.ext.web.RoutingContext;

public final class ReactiveRoute {

    private final Subject<RoutingContext> subject = BehaviorSubject.create();
    private Handler<RoutingContext> delegateHandler = null;

    ReactiveRoute(Route delegate) {
        delegate.handler(rc -> {
            subject.onNext(rc);
            if (delegateHandler != null)
                delegateHandler.handle(rc);
        });
    }

    public ReactiveRoute handler(Handler<RoutingContext> requestHandler) {
        this.delegateHandler = requestHandler;
        return this;
    }

    public Observable<RoutingContext> toObservable() {
        return subject;
    }

    public Flowable<RoutingContext> toFlowable(BackpressureStrategy strategy) {
        return subject.toFlowable(strategy);
    }
}

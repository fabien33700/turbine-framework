package io.turbine.core.verticles.support;

import io.reactivex.Completable;

public interface Startable {
    Completable rxStart();
}

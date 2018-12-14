package io.turbine.core.utils;

import io.reactivex.Completable;

public interface Startable {
    Completable rxStart();
}

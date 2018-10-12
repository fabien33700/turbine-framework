package io.turbine.core.utils.functional;

import io.reactivex.functions.Function;

@FunctionalInterface
public interface UnaryOperator<T> extends Function<T, T> { }

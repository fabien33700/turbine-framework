package io.turbine.core.utils.functional;

import io.reactivex.functions.Function;

import java.util.stream.Stream;

public class FnUtils {
    public static <T, R> java.util.function.Function<T, Stream<R>>
    safelyApply(Function<T, R> function) {
        return t -> {
            try {
                return Stream.of(function.apply(t));
            } catch (Exception ex) {
                return Stream.empty();
            }
        };
    }
}
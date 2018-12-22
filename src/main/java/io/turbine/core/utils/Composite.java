package io.turbine.core.utils;

import java.util.List;
import java.util.Optional;

public interface Composite<T extends Composite<T>> {

    List<T> getChildren();

    Optional<T> getParent();

    void setParent(T parent);

    default boolean hasParent() {
        return getParent().isPresent();
    }
}

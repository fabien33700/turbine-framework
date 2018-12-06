package io.turbine.core.ws.codec;

public interface Codec<T> {
    String encode(final T target) throws Exception;
    T decode(String source) throws Exception;
}

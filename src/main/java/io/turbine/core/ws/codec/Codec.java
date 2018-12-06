package io.turbine.core.ws.codec;

import io.vertx.reactivex.core.buffer.Buffer;

public interface Codec<T> {
    Buffer encode(final T object) throws Exception;
    T decode(Buffer buffer) throws Exception;
}

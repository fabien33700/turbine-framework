package io.turbine.core.ws;

import io.turbine.core.json.JsonSerializable;

import java.time.Instant;

public interface Message<S, B> extends JsonSerializable {
    Instant sentAt();
    B body();
    S sender();
}

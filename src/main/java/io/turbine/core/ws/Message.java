package io.turbine.core.ws;

import java.time.Instant;
import java.util.Optional;

public interface Message<S, B> {
    Instant sentAt();
    B body();
    S sender();

    void respond(B responseBody);
}

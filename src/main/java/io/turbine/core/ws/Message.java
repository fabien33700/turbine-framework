package io.turbine.core.ws;

import io.turbine.core.json.JsonSerializable;

import java.time.Instant;

/**
 * Defines the structure of a Message.
 *
* @param <S> The type of the object that identifies the message (S)ender
 * @param <B> The type of the message (B)ody
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public interface Message<S, B> extends JsonSerializable {
    /**
     * The moment the message had been received.
     * @return An Instant
     */
    Instant sentAt();

    /**
     * Returns the message body.
     * @return the body
     */
    B body();

    /**
     * Returns the message sender
     * @return the sender
     */
    S sender();
}

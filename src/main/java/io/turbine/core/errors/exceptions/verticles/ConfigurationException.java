package io.turbine.core.errors.exceptions.verticles;

public class ConfigurationException extends RuntimeException {

    private static final String EXCEPTION_MESSAGE = " Using empty configuration instead.";

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        if (getCause() != null) {
            return getCause().getMessage() + EXCEPTION_MESSAGE;
        }
        return super.getMessage() + EXCEPTION_MESSAGE;
    }
}

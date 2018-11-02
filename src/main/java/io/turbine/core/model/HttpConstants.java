package io.turbine.core.model;

public final class HttpConstants {

    public static class ContentTypes {
        public static final String APPLICATION_JSON = "application/json";
    }

    public static class ResponseStatus {
        public static final int BAD_REQUEST = 400;
        public static final int INTERNAL_SERVER_ERROR = 500;
    }

    public static class ResponseHeaders {
        public static final String CONTENT_TYPE = "Content-Type";
    }
}

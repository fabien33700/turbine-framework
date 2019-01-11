package io.turbine.core.web.router;

public final class Response<T> {
    private final T body;

    private final int statusCode;

    public Response(T body, int statusCode) {
        this.body = body;
        this.statusCode = statusCode;
    }

    public T body() {
        return body;
    }

    public int statusCode() {
        return statusCode;
    }

    /*** Factory methods ***/
    public static <T> Response<T> ok(T body) {
        return new Response<>(body, 200);
    }

    public static <T> Response<T> created(T body) {
        return new Response<>(body, 201);
    }

    public static <T> Response<T> accepted(T body) {
        return new Response<>(body, 202);
    }

    public static <T> Response<T> noContent() {
        return new Response<>(null, 204);
    }

    public static <T> Response<T> partialContent(T body) {
        return new Response<>(body, 206);
    }

    public static <T> Response<T> badRequest(T body) {
        return new Response<>(body, 400);
    }

    public static <T> Response<T> unauthorized(T body) {
        return new Response<>(body, 401);
    }

    public static <T> Response<T> forbidden(T body) {
        return new Response<>(body, 403);
    }

    public static <T> Response<T> notFound(T body) {
        return new Response<>(body, 404);
    }

    public static <T> Response<T> methodNotAllowed(T body) {
        return new Response<>(body, 405);
    }

    public static <T> Response<T> notAcceptable(T body) {
        return new Response<>(body, 406);
    }

    public static <T> Response<T> requestTimeout(T body) {
        return new Response<>(body, 408);
    }

    public static <T> Response<T> conflict(T body) {
        return new Response<>(body, 409);
    }

    public static <T> Response<T> gone(T body) {
        return new Response<>(body, 410);
    }

    public static <T> Response<T> unsupportedMediaType(T body) {
        return new Response<>(body, 415);
    }

    public static <T> Response<T> tooManyRequests(T body) {
        return new Response<>(body, 429);
    }

    public static <T> Response<T> internalServerError(T body) {
        return new Response<>(body, 500);
    }

    public static <T> Response<T> notImplemented(T body) {
        return new Response<>(body, 501);
    }

    public static <T> Response<T> serviceUnavailable(T body) {
        return new Response<>(body, 503);
    }
}

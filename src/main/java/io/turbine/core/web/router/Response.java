package io.turbine.core.web.router;

public final class Response<R> {
    private final R responseBody;

    private final int statusCode;

    public Response(R responseBody, int statusCode) {
        this.responseBody = responseBody;
        this.statusCode = statusCode;
    }

    public R body() {
        return responseBody;
    }

    public int statusCode() {
        return statusCode;
    }

    /*** Factory methods ***/
    public static <R> Response<R> ok(R responseBody) {
        return new Response<>(responseBody, 200);
    }

    public static <R> Response<R> created(R responseBody) {
        return new Response<>(responseBody, 201);
    }

    public static <R> Response<R> accepted(R responseBody) {
        return new Response<>(responseBody, 202);
    }

    public static <R> Response<R> noContent() {
        return new Response<>(null, 204);
    }

    public static <R> Response<R> partialContent(R responseBody) {
        return new Response<>(responseBody, 206);
    }

    public static <R> Response<R> badRequest(R responseBody) {
        return new Response<>(responseBody, 400);
    }

    public static <R> Response<R> unauthorized(R responseBody) {
        return new Response<>(responseBody, 401);
    }

    public static <R> Response<R> forbidden(R responseBody) {
        return new Response<>(responseBody, 403);
    }

    public static <R> Response<R> notFound(R responseBody) {
        return new Response<>(responseBody, 404);
    }

    public static <R> Response<R> methodNotAllowed(R responseBody) {
        return new Response<>(responseBody, 405);
    }

    public static <R> Response<R> notAcceptable(R responseBody) {
        return new Response<>(responseBody, 406);
    }

    public static <R> Response<R> requestTimeout(R responseBody) {
        return new Response<>(responseBody, 408);
    }

    public static <R> Response<R> conflict(R responseBody) {
        return new Response<>(responseBody, 409);
    }

    public static <R> Response<R> gone(R responseBody) {
        return new Response<>(responseBody, 410);
    }

    public static <R> Response<R> unsupportedMediaType(R responseBody) {
        return new Response<>(responseBody, 415);
    }

    public static <R> Response<R> tooManyRequests(R responseBody) {
        return new Response<>(responseBody, 429);
    }

    public static <R> Response<R> internalServerError(R responseBody) {
        return new Response<>(responseBody, 500);
    }

    public static <R> Response<R> notImplemented(R responseBody) {
        return new Response<>(responseBody, 501);
    }

    public static <R> Response<R> serviceUnavailable(R responseBody) {
        return new Response<>(responseBody, 503);
    }
}

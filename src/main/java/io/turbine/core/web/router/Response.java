package io.turbine.core.web.router;

public final class Response {
    private final Object body;

    private final int statusCode;

    public Response(Object body, int statusCode) {
        this.body = body;
        this.statusCode = statusCode;
    }

    public Object body() {
        return body;
    }

    public int statusCode() {
        return statusCode;
    }

    /*** Factory methods ***/
    public static Response ok(Object body) {
        return new Response(body, 200);
    }

    public static Response created(Object body) {
        return new Response(body, 201);
    }

    public static Response accepted(Object body) {
        return new Response(body, 202);
    }

    public static Response noContent() {
        return new Response(null, 204);
    }

    public static Response partialContent(Object body) {
        return new Response(body, 206);
    }

    public static Response badRequest(Object body) {
        return new Response(body, 400);
    }

    public static Response unauthorized(Object body) {
        return new Response(body, 401);
    }

    public static Response forbidden(Object body) {
        return new Response(body, 403);
    }

    public static Response notFound(Object body) {
        return new Response(body, 404);
    }

    public static Response methodNotAllowed(Object body) {
        return new Response(body, 405);
    }

    public static Response notAcceptable(Object body) {
        return new Response(body, 406);
    }

    public static Response requestTimeout(Object body) {
        return new Response(body, 408);
    }

    public static Response conflict(Object body) {
        return new Response(body, 409);
    }

    public static Response gone(Object body) {
        return new Response(body, 410);
    }

    public static Response unsupportedMediaType(Object body) {
        return new Response(body, 415);
    }

    public static Response tooManyRequests(Object body) {
        return new Response(body, 429);
    }

    public static Response internalServerError(Object body) {
        return new Response(body, 500);
    }

    public static Response notImplemented(Object body) {
        return new Response(body, 501);
    }

    public static Response serviceUnavailable(Object body) {
        return new Response(body, 503);
    }
}

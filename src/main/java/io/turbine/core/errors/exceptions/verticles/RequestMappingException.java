package io.turbine.core.errors.exceptions.verticles;

import io.turbine.core.web.mapping.RequestHandling;

import java.lang.reflect.Method;

import static java.lang.String.format;

public class RequestMappingException extends RuntimeException {
    private static final String GENERIC_MESSAGE = "Bad request mapping definition of the method %s() to the route %s %s in verticle %s : ";

    public RequestMappingException(Class<?> clazz, Method method, RequestHandling requestHandling, Throwable cause)  {
        super(format(GENERIC_MESSAGE,
                method.getName(), requestHandling.method(), requestHandling.path(),
                clazz.getSimpleName()), cause);
    }
}

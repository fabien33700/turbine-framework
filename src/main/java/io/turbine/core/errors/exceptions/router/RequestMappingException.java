package io.turbine.core.errors.exceptions.router;

import io.turbine.core.web.mapping.RequestHandling;

import java.lang.reflect.Method;

import static io.turbine.core.utils.Utils.Strings.format;

/**
 * This exception can be thrown by a Web verticle during the process of request mapping
 * between a route definition and a corresponding handler.
 *
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public final class RequestMappingException extends RuntimeException {
    private static final String GENERIC_MESSAGE = "Bad request mapping definition of the method {}() " +
            "to the route {} {} in verticle {} : ";

    public RequestMappingException(Class<?> clazz, Method method, RequestHandling requestHandling, Throwable cause)  {
        super(format(GENERIC_MESSAGE,
                method.getName(), requestHandling.method(), requestHandling.path(),
                clazz.getSimpleName()), cause);
    }
}

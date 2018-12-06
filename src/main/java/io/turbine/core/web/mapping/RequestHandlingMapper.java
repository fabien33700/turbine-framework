package io.turbine.core.web.mapping;

import io.reactivex.Single;
import io.turbine.core.errors.exceptions.router.RequestMappingException;
import io.turbine.core.verticles.BaseWebVerticle;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

public class RequestHandlingMapper {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandlingMapper.class);

    public static Map<RequestHandling, Method>
    findMappings(BaseWebVerticle verticle, boolean throwIfErrors) throws RequestMappingException
    {
        Class<?> vClass = verticle.getClass();
        Map<RequestHandling, Method> mappings = new LinkedHashMap<>();

        try {
            for (Method method : vClass.getMethods()) {
                if (method.isAnnotationPresent(RequestHandling.class)) {
                    RequestHandling rqh = method.getAnnotation(RequestHandling.class);
                    try {
                        if (!method.getReturnType().isAssignableFrom(Single.class)) {
                            throw new IllegalArgumentException("The request handling method " + method.getName() +
                                    "() must return a Single<Response>.");
                        }
                        Class<?>[] parameterTypes = method.getParameterTypes();
                        if (parameterTypes.length == 0 || !parameterTypes[0].isAssignableFrom(RoutingContext.class)) {
                            throw new IllegalArgumentException("The request handling method " + method.getName() +
                                    "() must have at least its first argument of type RoutingContext.");
                        }

                        mappings.put(rqh, method);
                    } catch (RuntimeException cause) {
                        throw new RequestMappingException(vClass, method, rqh, cause);
                    }
                }
            }
        } catch (RequestMappingException ex) {
            if (throwIfErrors)
                throw ex;
            else
                logger.warn(ex.getMessage());
        }
        return mappings;
    }
}

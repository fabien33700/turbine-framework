package io.turbine.core.web.mapping;

import io.reactivex.Single;
import io.turbine.core.errors.exceptions.router.RequestMappingException;
import io.turbine.core.verticles.BaseWebVerticle;
import io.turbine.core.web.HttpConstants;
import io.turbine.core.web.mapping.annotations.Bearer;
import io.turbine.core.web.mapping.annotations.QueryString;
import io.turbine.core.web.mapping.annotations.RequestBody;
import io.turbine.core.web.mapping.annotations.RouteParam;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static io.turbine.core.utils.Utils.Web.parseQueryString;
import static io.turbine.core.utils.Utils.parsePrimitiveValueFromString;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class RequestHandlingHelper {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandlingHelper.class);

    private final static String BEARER = "Bearer ";

    public static Map<RequestHandling, Method>
    findMappings(BaseWebVerticle verticle, boolean throwIfErrors) throws RequestMappingException {
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

    public static Object[] interpolateParams(Method method, RoutingContext rc) {
        List<Object> finalParams = new ArrayList<>();

        for (int i = 0; i < method.getParameterCount(); i++) {
            Parameter parameter = method.getParameters()[i];
            // Ignoring the first parameter that must be the RoutingContext
            if (!parameter.getType().equals(RoutingContext.class)) {
                Object value = null;
                // @Bearer annotation : type must be string
                if (parameter.getType().equals(String.class) && parameter.isAnnotationPresent(Bearer.class)) {
                    String auth = rc.request().getHeader(HttpConstants.RequestHeaders.AUTHORIZATION);
                    if (!isEmpty(auth) && auth.startsWith(BEARER))
                        value = auth.substring(BEARER.length()).trim();

                } else {
                    Class<? extends Annotation> annoClass = null;
                    String name = null;

                    if (parameter.isAnnotationPresent(RouteParam.class)) {
                        annoClass = RouteParam.class;
                        name = parameter.getAnnotation(RouteParam.class).value();
                    } else if (parameter.isAnnotationPresent(QueryString.class)) {
                        annoClass = QueryString.class;
                        name = parameter.getAnnotation(QueryString.class).value();
                    } else if (parameter.isAnnotationPresent(RequestBody.class)) {
                        annoClass = RequestBody.class;
                        name = parameter.getAnnotation(RequestBody.class).value();
                    }

                    // The parameter is annotated by one of the 3 annotations
                    if (annoClass != null) {
                        if (isEmpty(name)) {
                            if (parameter.isNamePresent()) {
                                name = parameter.getName();
                            } else {
                                // The annotation does not hold the key name and it is
                                // impossible to extract the parameter name using reflection
                                logger.warn("Could not determine key name from the parameter #{} (type {}) of " +
                                                "method {} annotated with @{}. Parameter will be ignored.",
                                        i, parameter.getType(), method.getName(), annoClass.getSimpleName());
                                // We must keep the same name of arguments for the method next call
                                finalParams.add(null);
                                continue;
                            }
                        }
                    }

                    if (parameter.isAnnotationPresent(RouteParam.class))
                        value = parsePrimitiveValueFromString(rc.pathParam(name));

                    else if (parameter.isAnnotationPresent(QueryString.class))
                        value = parseQueryString(rc.request().query()).get(name);

                    else if (parameter.isAnnotationPresent(RequestBody.class)) {
                        String content = (String) rc.getBodyAsJson().getMap().get(name);
                        value = parsePrimitiveValueFromString(content);
                    }
                }

                finalParams.add(value);
            }
        }

        finalParams.add(0, rc);
        return finalParams.toArray();
    }

}

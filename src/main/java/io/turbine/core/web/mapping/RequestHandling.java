package io.turbine.core.web.mapping;

import io.reactivex.BackpressureStrategy;
import io.vertx.core.http.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequestHandling {
    String path() default "/";
    HttpMethod method() default HttpMethod.GET;
    BackpressureStrategy strategy() default BackpressureStrategy.DROP;
}

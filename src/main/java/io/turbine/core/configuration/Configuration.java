package io.turbine.core.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation used to attach a configuration key to a verticle class.
 * The configuration dispatcher will provide to the verticle the Json object
 * which has the key specified by this annotation's key() property.
 *
 * If the key property is empty, the dispatcher takes in account the class name
 * as the default configuration key.
 *
 * In the case that several configuration keys match with a same class, the dispatcher
 * will merge them before providing.
 *
 * Note that the dispatcher takes in account class hierarchy. If a parent class
 * has a configuration key, all its subclasses marked with @Configuration annotation
 * will have it too.
 *
 * e.g. An example with a configuration.json file
 *
 * <code>{
 *     "MyVerticle": {
 *         "port": 9100,
 *         "max-idle-time": 500,
 *         ...
 *     },
 *
 *     "Verticles": {
 *         "ssl": true
 *     }
 * }</code>
 *
 * The class defined like this :
 *
 *      <code>\@Configuration
 *      public class MyVerticle extends BaseVerticle { //... }
 *      </code>
 *
 *  will receive the configuration object with parameters port = 9100 and
 *  max-idle-time = 500.
 *
 *  If the class has a different name of the key, you must specify the key in the annotation
 *
 *      <code>\@Configuration(key = "MyVerticle")
 *      public class MyOtherVerticle extends BaseVerticle { // ... }</code>
 *
 * @see Dispatcher
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Configuration {
    /**
     * The configuration key to specify to the dispatcher for providing
     * matching configuration to the Verticle.
     *
     * Default empty value is supported by the dispatcher, it will take
     * the class name as key.
     *
     * @return The configuration key
     */
    String key() default "";
}

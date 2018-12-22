package io.turbine.core.utils;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.functions.Action;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.*;
import java.lang.reflect.ParameterizedType;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Stream.of;

public class Utils {

    public static class Reactive {
        public interface SingleSupplier<V> extends Supplier<Single<V>> {}
        public interface CompletableSupplier extends Supplier<Completable> {}

        public static Completable completable(Action... actions) {
            return of(actions)
                    .map(Completable::fromAction)
                    .reduce(Completable::concatWith)
                    .orElse(Completable.complete());
        }

        public static Completable completable(Completable... completables) {
            return of(completables)
                    .reduce(Completable::concatWith)
                    .orElse(Completable.complete());
        }

        public static Completable completable(CompletableSupplier... completables) {
            return of(completables)
                    .map(Supplier::get)
                    .filter(Objects::nonNull)
                    .reduce(Completable::concatWith)
                    .orElse(Completable.complete());
        }

        public static Completable completable(Single<?>... singles) {
            return of(singles)
                    .map(Completable::fromSingle)
                    .reduce(Completable::concatWith)
                    .orElse(Completable.complete());
        }

        public static Completable completable(SingleSupplier<?>... singles) {
            return of(singles)
                    .map(Supplier::get)
                    .filter(Objects::nonNull)
                    .map(Completable::fromSingle)
                    .reduce(Completable::concatWith)
                    .orElse(Completable.complete());
        }
    }

    public static String fromInputStream(final InputStream is) {
        if (is == null)
            return null;

        return new BufferedReader(new InputStreamReader(is))
            .lines()
            .parallel()
            .collect(Collectors.joining("\n"));

    }

    public static class Reflection {

        private static final Class<?>[] PRIMITIVES_WRAPPER_AND_TYPES = new Class<?>[] {
                Boolean.class, Boolean.TYPE,
                Byte.class, Byte.TYPE,
                Character.class, Character.TYPE,
                Double.class, Double.TYPE,
                Float.class, Float.TYPE,
                Integer.class, Integer.TYPE,
                Long.class, Long.TYPE,
                Short.class, Short.TYPE
        };

        public static List<Class<?>> getClassHierarchy(Class<?> baseClass) {
            List<Class<?>> hierarchy = new ArrayList<>();
            Class<?> cursor = baseClass;
            while (cursor != null) {
                hierarchy.add(cursor);
                cursor = cursor.getSuperclass();
            }
            java.util.Collections.reverse(hierarchy);
            return hierarchy;
        }

        public static Class<?> getGenericTypeOf(Class<?> sourceType) {
            return (Class<?>) ((ParameterizedType) sourceType.getGenericSuperclass())
                            .getActualTypeArguments()[0];
        }

        public static boolean isPrimitiveOrWrapper(Object response) {
            final Class<?> clazz = response.getClass();
            return clazz.isPrimitive() ||
                    asList(PRIMITIVES_WRAPPER_AND_TYPES).contains(clazz);
        }
    }

    public static class Collections {
        public static <E, C extends Collection<E>> C fromIterable(Iterable<E> items, Supplier<C> factory) {
            C collection = factory.get();
            items.forEach(collection::add);

            return collection;
        }

        public static <E> List<E> fromIterable(Iterable<E> items) {
            return fromIterable(items, ArrayList::new);
        }
    }

    public static class MapBuilder {
        public interface Tuple<K, V> {
            K key();
            V value();
        }
        final static class TupleImpl<K, V> implements Tuple<K, V> {
            private final K key;
            private final V value;

            TupleImpl(K key, V value) {
                this.key = key;
                this.value = value;
            }

            @Override
            public K key() {
                return key;
            }

            @Override
            public V value() {
                return value;
            }
        }
        public static <K, V> Map<K, V> mapOf(K key, V value) {
            return mapOf(tuple(key, value));
        }
        @SafeVarargs
        public static <K, V> Map<K, V> mapOf(Tuple<K, V> ... tuples) {
             return of(tuples).collect(toMap(Tuple::key, Tuple::value));
        }
        public static <K, V> Tuple<K, V> tuple(K key, V value) {
            return new TupleImpl<>(key, value);
        }
    }

    public static <E> E orElse(E expression, E defaultValue) {
        return expression == null ? defaultValue : expression;
    }

    public static <E, P> P orElseGet(E expression, Function<E, P> propertyAccessor, P defaultValue) {
        return expression == null ? defaultValue : propertyAccessor.apply(expression);
    }

    public static <E> E orElse(Supplier<E> provider, E defaultValue) {
        return orElse(provider.get(), defaultValue);
    }

    public static final class Dates {
        private static final String ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

        private static final DateTimeFormatter ISO_8601_FORMATTER = DateTimeFormatter
                .ofPattern(ISO_8601)
                .withLocale(Locale.getDefault())
                .withZone(ZoneId.systemDefault());

        public static String formatDateIso3601(TemporalAccessor temporal) {
            return ISO_8601_FORMATTER.format(temporal);
        }
    }

    public static Object parsePrimitiveValueFromString(String input) {
        return parsePrimitiveValueFromString(input, false);
    }

    public static Object parsePrimitiveValueFromString(String input, boolean useStrictTyping) {
        // null value case
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        input = input.trim();
        // input is a parsable number
        if (NumberUtils.isParsable(input)) {

            // input numer is decimal
            if (input.contains(".")) {
                double doubleVal = Double.parseDouble(input);
                // try parsing it to float if it is in bounds
                if (doubleVal >= Float.MIN_VALUE &&
                    doubleVal <= Float.MAX_VALUE) {
                    return (float) doubleVal;
                }
                return doubleVal;
            } else {
                long longVal = Long.parseLong(input);
                // try parsing it to byte if it is in bounds
                // (only if useStrictTyping is true)
                if (useStrictTyping &&
                    longVal >= Byte.MIN_VALUE &&
                    longVal <= Byte.MAX_VALUE)
                {
                    return (byte) longVal;
                }
                // try parsing it to short if it is in bounds
                // (only if useStrictTyping is true)
                if (useStrictTyping &&
                        longVal >= Short.MIN_VALUE &&
                        longVal <= Short.MAX_VALUE)
                {
                    return (short) longVal;
                }
                // try parsing it to int if it is in bounds
                if (longVal >= Integer.MIN_VALUE &&
                    longVal <= Integer.MAX_VALUE) {
                    return (int) longVal;
                }
                return longVal;
            }
        } else {
            // try parsing it to char
            // (only if useStrictTyping is true)
            if (useStrictTyping && input.length() == 1)
                return input.charAt(0);

            switch (input.toLowerCase()) {
                case "false":
                    return false;
                case "true":
                    return true;
                default:
                    return input;
            }
        }
    }

    public static final class Strings extends org.apache.commons.lang3.StringUtils {

        public static String getStackTraceAsString(Throwable t) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            return sw.toString();
        }

        public static String format(final String format, final Object... args) {
            final String Separator = "{}";
            Iterator<Object> itArgs = asList(args).iterator();
            String buffer = format;
            while (itArgs.hasNext() && buffer.contains(Separator)) {
                Object arg = itArgs.next();
                String repr = (arg != null) ? arg.toString() : "";
                int pos = buffer.indexOf(Separator);
                buffer = buffer.substring(0, pos) + repr + buffer.substring(pos + Separator.length());
            }
            return buffer;
        }
    }

    public static class Web {


        /**
         * Parse a query string from a HTTP GET request and returns
         * a map containing all parameters from it.
         * If a possible "?" prefix character is found, it will be ignored.
         * Note that it try to resolve numeric value as Java number types.
         * @param query The query string
         * @return A map containing parameters (keys and values)
         */
        @SuppressWarnings("unchecked")
        public static Map<String,Object> parseQueryString(String query) {
            // Creating the resulting map
            Map<String, Object> params = new HashMap<>();

            // Ensuring query string is parsable
            if (query != null && !query.trim().isEmpty()) {
                // Stripping the possible "?" prefix
                if (query.startsWith("?")) {
                    query = query.substring(1);
                }


                // For each tuple (a key/value pair, separated by '&' delimiter)
                for (String tuple : query.split("&")) {
                    // Extracting the string before the '=' delimiter
                    // and ignoring '[]' (used for array parameters)
                    String key = tuple.substring(0, tuple.indexOf("="))
                            .replaceAll("\\[]", "");
                    // Extracting value after the '=' delimiter
                    String strValue = tuple.substring(tuple.indexOf("=") + 1);
                    Object value = parsePrimitiveValueFromString(strValue);

                    // If a duplicate key tuple is found
                    if (params.containsKey(key)) {
                        Object actual = params.get(key);
                        // we replace the single existing value with an ArrayList
                        if (!(actual instanceof ArrayList))
                            params.put(key, new ArrayList<>(singletonList(actual)));

                        // Adding the new value to the ArrayList
                        ((ArrayList<Object>) params.get(key)).add(value);
                    } else {
                        params.put(key, value);
                    }
                }
            }
            return params;
        }
    }
}

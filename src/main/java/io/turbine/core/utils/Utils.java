package io.turbine.core.utils;

import java.io.*;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class Utils {
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

    public static String fromInputStream(final InputStream is) {
        if (is == null)
            return null;

        return new BufferedReader(new InputStreamReader(is))
            .lines()
            .parallel()
            .collect(Collectors.joining("\n"));

    }

    public static List<Class<?>> getClassHierarchy(Class<?> baseClass) {
        List<Class<?>> hierarchy = new ArrayList<>();
        Class<?> cursor = baseClass;
        while (cursor != null) {
            hierarchy.add(cursor);
            cursor = cursor.getSuperclass();
        }
        Collections.reverse(hierarchy);
        return hierarchy;
    }

    public static <E, C extends Collection<E>> C fromIterable(Iterable<E> items, Supplier<C> factory) {
        C collection = factory.get();
        items.forEach(collection::add);

        return collection;
    }

    public static <E> List<E> fromIterable(Iterable<E> items) {
        return fromIterable(items, ArrayList::new);
    }

    public static boolean isPrimitiveOrWrapper(Object response) {
        final Class<?> clazz = response.getClass();
        return clazz.isPrimitive() ||
                asList(PRIMITIVES_WRAPPER_AND_TYPES).contains(clazz);
    }

    public static <E> E orElse(E expression, E defaultValue) {
        return expression == null ? defaultValue : expression;
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

        @SuppressWarnings("unchecked")
        public static Map<String,Object> parseQueryString(String query) {
            Map<String, Object> params = new HashMap<>();

            if (query != null && !query.trim().isEmpty()) {
                for (String tuple : query.split("&")) {
                    String key = tuple.substring(0, tuple.indexOf("="))
                            .replaceAll("\\[]", "");
                    String value = tuple.substring(tuple.indexOf("=") + 1);

                    if (params.containsKey(key)) {
                        Object actual = params.get(key);
                        if (!(actual instanceof ArrayList))
                            params.put(key, new ArrayList<>(singletonList(actual)));

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

package io.turbine.core.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

public class GeneralUtils {

    @SuppressWarnings("unchecked")
    public static Map<String,Object> extractQuery(String query) {
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
}

package io.turbine.core.utils;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
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

package io.turbine.core.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;

import static java.util.Arrays.asList;

public final class Strings extends org.apache.commons.lang3.StringUtils {

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

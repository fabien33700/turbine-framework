package io.turbine.core.utils;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

public final class Dates {
    private static final String ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

    private static final DateTimeFormatter ISO_8601_FORMATTER = DateTimeFormatter
            .ofPattern(ISO_8601)
            .withLocale(Locale.getDefault())
            .withZone(ZoneId.systemDefault());

    public static String formatDateIso3601(TemporalAccessor temporal) {
        return ISO_8601_FORMATTER.format(temporal);
    }
}

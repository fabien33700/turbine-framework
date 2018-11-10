package io.turbine.core.i18n;

import io.reactivex.functions.Function;

import java.util.Locale;
import java.util.Properties;

/**
 * A functionnal interface that describes a MessageProvider, which have to
 * provide messages (wrapped in Properties instance) for a given Locale.
 *
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public interface MessageProvider extends Function<Locale, Properties> {}

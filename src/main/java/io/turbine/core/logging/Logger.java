package io.turbine.core.logging;

import io.turbine.core.i18n.MessageResolver;
import org.slf4j.Marker;

public final class Logger implements org.slf4j.Logger {

    private final MessageResolver resolver;

    private final org.slf4j.Logger delegate;

    public Logger(org.slf4j.Logger delegate, MessageResolver resolver) {
        this.delegate = delegate;
        this.resolver = resolver;
    }

    public org.slf4j.Logger getDelegate() {
        return delegate;
    }

    /** Delegate methods **/

    public String getName() {
        return delegate.getName();
    }

    public boolean isTraceEnabled() {
        return delegate.isTraceEnabled();
    }

    public void trace(String msg) {
        delegate.trace(resolver.getMessage(msg));
    }

    public void trace(String format, Object arg) {
        delegate.trace(resolver.getMessage(format, arg));
    }

    public void trace(String format, Object arg1, Object arg2) {
        delegate.trace(resolver.getMessage(format, arg1, arg2));
    }

    public void trace(String format, Object... arguments) {
        delegate.trace(resolver.getMessage(format, arguments));
    }

    public void trace(String msg, Throwable t) {
        delegate.trace(resolver.getMessage(msg), t);
    }

    public void trace(Marker marker, String msg) {
        delegate.trace(marker, msg);
    }

    public void trace(Marker marker, String format, Object arg) {
        delegate.trace(marker, format, arg);
    }

    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        delegate.trace(marker, format, arg1, arg2);
    }

    public void trace(Marker marker, String format, Object... argArray) {
        delegate.trace(marker, format, argArray);
    }

    public void trace(Marker marker, String msg, Throwable t) {
        delegate.trace(marker, msg, t);
    }

    public void debug(Marker marker, String msg) {
        delegate.debug(marker, msg);
    }

    public void debug(Marker marker, String format, Object arg) {
        delegate.debug(marker, format, arg);
    }

    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        delegate.debug(marker, format, arg1, arg2);
    }

    public void debug(Marker marker, String format, Object... arguments) {
        delegate.debug(marker, format, arguments);
    }

    public void debug(Marker marker, String msg, Throwable t) {
        delegate.debug(marker, msg, t);
    }

    public void info(Marker marker, String msg) {
        delegate.info(marker, msg);
    }

    public void info(Marker marker, String format, Object arg) {
        delegate.info(marker, format, arg);
    }

    public void info(Marker marker, String format, Object arg1, Object arg2) {
        delegate.info(marker, format, arg1, arg2);
    }

    public void info(Marker marker, String format, Object... arguments) {
        delegate.info(marker, format, arguments);
    }

    public void info(Marker marker, String msg, Throwable t) {
        delegate.info(marker, msg, t);
    }

    public void warn(Marker marker, String msg) {
        delegate.warn(marker, msg);
    }

    public void warn(Marker marker, String format, Object arg) {
        delegate.warn(marker, format, arg);
    }

    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        delegate.warn(marker, format, arg1, arg2);
    }

    public void warn(Marker marker, String format, Object... arguments) {
        delegate.warn(marker, format, arguments);
    }

    public void warn(Marker marker, String msg, Throwable t) {
        delegate.warn(marker, msg, t);
    }

    public void error(Marker marker, String msg) {
        delegate.error(marker, msg);
    }

    public void error(Marker marker, String format, Object arg) {
        delegate.error(marker, format, arg);
    }

    public void error(Marker marker, String format, Object arg1, Object arg2) {
        delegate.error(marker, format, arg1, arg2);
    }

    public void error(Marker marker, String format, Object... arguments) {
        delegate.error(marker, format, arguments);
    }

    public void error(Marker marker, String msg, Throwable t) {
        delegate.error(marker, msg, t);
    }

    public boolean isDebugEnabled() {
        return delegate.isDebugEnabled();
    }

    public void debug(String msg) {
        delegate.debug(resolver.getMessage(msg));
    }

    public void debug(String format, Object arg) {
        delegate.debug(resolver.getMessage(format, arg));
    }

    public void debug(String format, Object arg1, Object arg2) {
        delegate.debug(resolver.getMessage(format, arg1, arg2));
    }

    public void debug(String format, Object... arguments) {
        delegate.debug(resolver.getMessage(format, arguments));
    }

    public void debug(String msg, Throwable t) {
        delegate.debug(resolver.getMessage(msg), t);
    }

    public boolean isInfoEnabled() {
        return delegate.isInfoEnabled();
    }

    public void info(String msg) {
        delegate.info(resolver.getMessage(msg));
    }

    public void info(String format, Object arg) {
        delegate.info(resolver.getMessage(format, arg));
    }

    public void info(String format, Object arg1, Object arg2) {
        delegate.info(resolver.getMessage(format, arg1, arg2));
    }

    public void info(String format, Object... arguments) {
        delegate.info(resolver.getMessage(format, arguments));
    }

    public void info(String msg, Throwable t) {
        delegate.info(resolver.getMessage(msg), t);
    }

    public boolean isWarnEnabled() {
        return delegate.isWarnEnabled();
    }

    public void warn(String msg) {
        delegate.warn(resolver.getMessage(msg));
    }

    public void warn(String format, Object arg) {
        delegate.warn(resolver.getMessage(format, arg));
    }

    public void warn(String format, Object... arguments) {
        delegate.warn(resolver.getMessage(format, arguments));
    }

    public void warn(String format, Object arg1, Object arg2) {
        delegate.warn(resolver.getMessage(format, arg1, arg2));
    }

    public void warn(String msg, Throwable t) {
        delegate.warn(resolver.getMessage(msg), t);
    }

    public boolean isWarnEnabled(Marker marker) {
        return delegate.isWarnEnabled(marker);
    }

    public boolean isErrorEnabled(Marker marker) {
        return delegate.isErrorEnabled(marker);
    }

    public boolean isErrorEnabled() {
        return delegate.isErrorEnabled();
    }

    public void error(String msg) {
        delegate.error(resolver.getMessage(msg));
    }

    public void error(String format, Object arg) {
        delegate.error(resolver.getMessage(format, arg));
    }

    public void error(String format, Object arg1, Object arg2) {
        delegate.error(resolver.getMessage(format, arg1, arg2));
    }

    public void error(String format, Object... arguments) {
        delegate.error(resolver.getMessage(format, arguments));
    }

    public void error(String msg, Throwable t) {
        delegate.error(resolver.getMessage(msg), t);
    }

    /*** We will not used these methods so we just declare neutral delegates ***/

    public boolean isTraceEnabled(Marker marker) {
        return delegate.isTraceEnabled(marker);
    }

    public boolean isDebugEnabled(Marker marker) {
        return delegate.isDebugEnabled(marker);
    }

    public boolean isInfoEnabled(Marker marker) {
        return delegate.isInfoEnabled(marker);
    }

}

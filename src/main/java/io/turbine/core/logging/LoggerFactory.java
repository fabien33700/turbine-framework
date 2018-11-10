package io.turbine.core.logging;

import io.turbine.core.i18n.MessageResolverImpl;

public final class LoggerFactory {

    private LoggerFactory() {}

    public static Logger getLogger(String name) {
        org.slf4j.Logger baseLogger = org.slf4j.LoggerFactory.getLogger(name);
        return new Logger(baseLogger, new MessageResolverImpl());
    }

    public static Logger getLogger(Class<?> clazz){
        org.slf4j.Logger baseLogger = org.slf4j.LoggerFactory.getLogger(clazz);
        return new Logger(baseLogger, new MessageResolverImpl());
    }
}

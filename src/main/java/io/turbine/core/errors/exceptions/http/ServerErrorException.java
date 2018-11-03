package io.turbine.core.errors.exceptions.http;

import java.util.UUID;

import static io.turbine.core.utils.StringUtils.getStackTraceAsString;
import static java.util.UUID.randomUUID;

public class ServerErrorException extends HttpException {

    private final UUID uuid;

    public ServerErrorException(Throwable cause) {
        super(cause);
        this.uuid = randomUUID();
    }

    @Override
    protected String defaultMessage() {
        return "An internal server error has occurred. " +
                "Please contact your administrator and supply him the error unique identifier";
    }

    @Override
    public int statusCode() {
        return 500;
    }

    public String getUuid() {
        return uuid.toString();
    }

    @Override
    public String toString() {
        return getMessage() + "\n***** Server error unique identifier : {" + getUuid() + "}\n" +
                "***** Error cause stacktrace : \n" + getStackTraceAsString(getCause());
    }
}

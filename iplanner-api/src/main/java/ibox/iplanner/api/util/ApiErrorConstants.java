package ibox.iplanner.api.util;

public final class ApiErrorConstants {

    public static final int SC_OK = 200;
    public static final int SC_CREATED = 201;
    public static final int SC_BAD_REQUEST = 400;
    public static final int SC_NOT_FOUND = 404;
    public static final int SC_CONFLICT = 409;
    public static final int SC_INTERNAL_SERVER_ERROR = 500;

    public static final String ERROR_BAD_REQUEST = "Bad Request";
    public static final String ERROR_INTERNAL_SERVER_ERROR = "Internal Server Error";

    private ApiErrorConstants() {}

}

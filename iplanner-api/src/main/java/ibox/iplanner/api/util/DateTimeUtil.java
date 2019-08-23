package ibox.iplanner.api.util;

import java.time.Instant;

public class DateTimeUtil {

    private DateTimeUtil() {}

    public static String formatUTCDatetime(Instant time) {
        return time.toString();
    }

    public static Instant parseUTCDatetime(String timeStr) {
        return Instant.parse(timeStr);
    }
}

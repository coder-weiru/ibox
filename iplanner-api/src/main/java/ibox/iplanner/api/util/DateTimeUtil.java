package ibox.iplanner.api.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

public class DateTimeUtil {

    private DateTimeUtil() {}

    public static String formatUTCDatetime(Instant time) {
        return time.toString();
    }

    public static Instant parseUTCDatetime(String timeStr) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Optional<Date> datetime = null;
        try {
            datetime = Optional.of(df.parse(timeStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (datetime.isPresent()) {
            return datetime.get().toInstant();
        }
        return null;
    }
}

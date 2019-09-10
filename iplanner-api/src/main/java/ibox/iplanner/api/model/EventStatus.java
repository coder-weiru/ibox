package ibox.iplanner.api.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum EventStatus {

    OPEN("OPEN"),
    CLOSED("CLOSED"),
    FINISHED("FINISHED");

    private String value;

    private static final Map<String, EventStatus> lookup = new HashMap();

    EventStatus(String value) {
        this.value = value;
    }

    static {
        EnumSet.allOf(EventStatus.class).stream().forEach(e -> lookup.put(e.value, e));
    }

    public static EventStatus of(String value) {
        return lookup.get(value!=null?value.toUpperCase(Locale.getDefault()):"");
    }
}

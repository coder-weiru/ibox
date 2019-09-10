package ibox.iplanner.api.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum ActivityStatus {

    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE");

    private String value;

    private static final Map<String, ActivityStatus> lookup = new HashMap();

    ActivityStatus(String value) {
        this.value = value;
    }

    static {
        EnumSet.allOf(ActivityStatus.class).stream().forEach(e -> lookup.put(e.value, e));
    }

    public static ActivityStatus of(String value) {
        return lookup.get(value!=null?value.toUpperCase(Locale.getDefault()):"");
    }
}

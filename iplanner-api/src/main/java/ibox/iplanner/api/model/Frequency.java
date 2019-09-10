package ibox.iplanner.api.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum Frequency {

    ONE_TIME("one-time"),
    HOURLY("hourly"),
    DAILY("daily"),
    WEEKLY("weekly"),
    MONTHLY("monthly"),
    ANNUALLY("annually");

    private String frequencyType;

    private static final Map<String, Frequency> lookup = new HashMap();

    Frequency(String frequencyType) {
        this.frequencyType = frequencyType;
    }

    static {
        EnumSet.allOf(Frequency.class).stream().forEach(e -> lookup.put(e.frequencyType, e));
    }

    public static Frequency of(String frequencyType) {
        return lookup.get(frequencyType);
    }
}

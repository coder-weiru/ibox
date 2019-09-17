package ibox.iplanner.api.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum Frequency {

    ONE_TIME("ONE_TIME"),
    HOURLY("HOURLY"),
    DAILY("DAILY"),
    WEEKLY("WEEKLY"),
    MONTHLY("MONTHLY"),
    ANNUALLY("ANNUALLY");

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

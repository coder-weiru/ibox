package ibox.iplanner.api.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum TodoStatus {

    OPEN("OPEN"),
    CLOSED("CLOSED"),
    FINISHED("FINISHED");

    private String value;

    private static final Map<String, TodoStatus> lookup = new HashMap();

    TodoStatus(String value) {
        this.value = value;
    }

    static {
        EnumSet.allOf(TodoStatus.class).stream().forEach(e -> lookup.put(e.value, e));
    }

    public static TodoStatus of(String value) {
        return lookup.get(value!=null?value.toUpperCase(Locale.getDefault()):"");
    }
}

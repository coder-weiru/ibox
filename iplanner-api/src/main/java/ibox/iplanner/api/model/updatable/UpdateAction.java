package ibox.iplanner.api.model.updatable;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum UpdateAction {

    ADD("ADD"),
    UPDATE("UPDATE"),
    DELETE("DELETE");

    private String value;

    private static final Map<String, UpdateAction> lookup = new HashMap();

    UpdateAction(String value) {
        this.value = value;
    }

    static {
        EnumSet.allOf(UpdateAction.class).stream().forEach(e -> lookup.put(e.value, e));
    }

    public static UpdateAction of(String value) {
        return lookup.get(value!=null?value.toUpperCase(Locale.getDefault()):"");
    }
}

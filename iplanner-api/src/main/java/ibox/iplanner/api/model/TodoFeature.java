package ibox.iplanner.api.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum TodoFeature {

    EVENT_FEATURE("event-info") {
        @Override
        public Class<? extends TodoAttribute> attributeType() {
            return EventAttribute.class;
        }
    },
    LOCATION_FEATURE("location") {
        @Override
        public Class<? extends TodoAttribute> attributeType() {
            return LocationAttribute.class;
        }
    },
    TAGGING_FEATURE("tags") {
        @Override
        public Class<? extends TodoAttribute> attributeType() {
            return TagAttribute.class;
        }
    },
    TIMELINE_FEATURE("timeline") {
        @Override
        public Class<? extends TodoAttribute> attributeType() {
            return TimelineAttribute.class;
        }
    };

    private String value;

    private static final Map<String, TodoFeature> lookup = new HashMap();

    TodoFeature(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    static {
        EnumSet.allOf(TodoFeature.class).stream().forEach(e -> lookup.put(e.value, e));
    }

    public static TodoFeature of(String value) {
        return lookup.get(value!=null?value:"");
    }

    public abstract Class<? extends TodoAttribute> attributeType();
}

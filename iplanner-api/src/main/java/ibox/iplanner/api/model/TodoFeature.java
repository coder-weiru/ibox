package ibox.iplanner.api.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum TodoFeature {

    EVENT_FEATURE("EventInfo") {
        @Override
        public Class<? extends TodoAttribute> attributeType() {
            return EventAttribute.class;
        }
    },
    LOCATION_FEATURE("LocationInfo") {
        @Override
        public Class<? extends TodoAttribute> attributeType() {
            return LocationAttribute.class;
        }
    },
    TAGGING_FEATURE("Tags") {
        @Override
        public Class<? extends TodoAttribute> attributeType() {
            return TagAttribute.class;
        }
    },
    TIMELINE_FEATURE("Timeline") {
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

package ibox.iplanner.api.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "class",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = EventAttribute.class, name = "event-info"),
        @JsonSubTypes.Type(value = LocationAttribute.class, name = "location"),
        @JsonSubTypes.Type(value = TagAttribute.class, name = "tags"),
        @JsonSubTypes.Type(value = TimelineAttribute.class, name = "timeline")
})
public abstract class TodoAttribute {
    abstract TodoFeature feature();
}

package ibox.iplanner.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "activityType",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Activity.class, name = "activity"),
        @JsonSubTypes.Type(value = Meeting.class, name = "meeting"),
        @JsonSubTypes.Type(value = Task.class, name = "task")
})
public class Activity {
    private String id;
    private String title;
    private String description;
    private String activityType;
    private User creator;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant created;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant updated;
    private ActivityStatus activityStatus;
    private AttributeSet attributeSet = new AttributeSet();

    public Activity() {}

    public TodoAttribute getAttribute(TodoFeature feature) {
        return attributeSet.getAttribute(feature);
    }

    public void addAttribute(TodoAttribute attribute) {
        attributeSet.addAttribute(attribute);
    }

    public void setAttribute(TodoAttribute attribute) {
        attributeSet.setAttribute(attribute);
    }

    public Set<TodoFeature> getSupportedFeatures() {
        return attributeSet.getSupportedFeatures();
    }

    public boolean supports(TodoFeature feature) {
        return getSupportedFeatures().contains(feature);
    }

    public boolean supports(Set<TodoFeature> features) {
        return features.stream().allMatch(todoFeature -> supports(todoFeature));
    }

}

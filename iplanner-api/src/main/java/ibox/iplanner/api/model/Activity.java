package ibox.iplanner.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
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
@Slf4j
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
    private ActivityStatus status;
    private TagAttribute tags = new TagAttribute();

    public Activity() {}

    public TagAttribute getTags() {
        return tags;
    }

    public void setTags(TagAttribute tags) {
        this.tags = tags;
    }

    public Set<TodoFeature> getSupportedFeatures() {
        return new HashSet(Arrays.asList(new TodoFeature[] { TodoFeature.TAGGING_FEATURE} ));
    }

    public TodoAttribute getAttribute(TodoFeature feature) {
        PropertyDescriptor pd;
        try {
            pd = new PropertyDescriptor(feature.getValue(), this.getClass());
            return (TodoAttribute) pd.getReadMethod().invoke(this);
        } catch (IntrospectionException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            log.error("Cannot access field %s", feature.getValue());
        }
        return null;
    }

    public void setAttribute(TodoAttribute attribute) {
        TodoFeature feature = attribute.feature();
        PropertyDescriptor pd;
        try {
            pd = new PropertyDescriptor(feature.getValue(), this.getClass());
            pd.getWriteMethod().invoke(this, attribute);
        } catch (IntrospectionException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            log.error("Cannot access field %s", feature.getValue());
        }
    }

    public boolean supports(TodoFeature feature) {
        return getSupportedFeatures().contains(feature);
    }

    public boolean supports(Set<TodoFeature> features) {
        return features.stream().allMatch(todoFeature -> supports(todoFeature));
    }

}

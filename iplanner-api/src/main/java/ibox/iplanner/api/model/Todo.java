package ibox.iplanner.api.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class Todo {

    private String id;
    private String summary;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant created;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant updated;
    private String activityId;
    private String activityType;
    private User creator;
    private TodoStatus status;
    private TagAttribute tags = new TagAttribute();
    private EventAttribute eventInfo = new EventAttribute();
    private LocationAttribute locationInfo = new LocationAttribute();
    private TimelineAttribute timeline = new TimelineAttribute();

    public static Todo fromActivity(Activity activity) {
        Instant now = Instant.now();
        Todo todo = Todo.builder()
                .id(UUID.randomUUID().toString())
                .activityId(activity.getId())
                .activityType(activity.getActivityType())
                .summary(String.format("My %s", activity.getTitle()))
                .description(String.format("%s", activity.getDescription()))
                .creator(activity.getCreator())
                .created(now)
                .updated(now)
                .status(TodoStatus.OPEN)
                .build();

        todo.copyAttributeValuesFromActivity(activity);
        return todo;
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

    public Set<TodoFeature> getSupportedFeatures() {
        return Activities.getSupportedFeatures(Activities.getActivityType(this.activityType));
    }

    protected void copyAttributeValuesFromActivity(Activity activity) {
        Set<TodoFeature> supported = activity.getSupportedFeatures();
        supported.stream().forEach( feature -> this.setAttribute(activity.getAttribute(feature)));
    }
}

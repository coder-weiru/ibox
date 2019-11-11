package ibox.iplanner.api.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    @Builder.Default
    private AttributeSet attributeSet = new AttributeSet();

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
        return attributeSet.getAttribute(feature);
    }

    protected void addFeatureAttribute(TodoAttribute attribute) {
        attributeSet.addAttribute(attribute);
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

    protected void copyAttributeValuesFromActivity(Activity activity) {
        if (activity!=null && !activity.getAttributeSet().getAttributes().isEmpty()) {
            activity.getAttributeSet().getAttributes().stream().forEach( attribute -> attributeSet.addAttribute(attribute));
        }
    }
}

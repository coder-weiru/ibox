package ibox.iplanner.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Activity.class, name = "activity"),
        @JsonSubTypes.Type(value = Meeting.class, name = "meeting"),
        @JsonSubTypes.Type(value = Task.class, name = "task")
})
public class Activity {

    private String id;
    @NotNull(message = "Title {javax.validation.constraints.NotNull.message}")
    private String title;
    private String description;
    @NotNull(message = "Type {javax.validation.constraints.NotNull.message}")
    private String type;
    @Valid
    @NotNull(message = "Creator {javax.validation.constraints.NotNull.message}")
    private User creator;
    @NotNull(message = "Created Time {javax.validation.constraints.NotNull.message}")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant created;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant updated;
    private String status;

}

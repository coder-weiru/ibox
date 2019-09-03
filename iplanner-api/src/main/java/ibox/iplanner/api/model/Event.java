package ibox.iplanner.api.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    private String id;
    @NotNull(message = "Summary {javax.validation.constraints.NotNull.message}")
    private String summary;
    private String description;

    @Valid
    @NotNull(message = "Creator {javax.validation.constraints.NotNull.message}")
    private User creator;
    @NotNull(message = "Created Time {javax.validation.constraints.NotNull.message}")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant created;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant updated;
    @NotNull(message = "Start Time {javax.validation.constraints.NotNull.message}")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant start;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant end;
    @NotNull(message = "Activity {javax.validation.constraints.NotNull.message}")
    private String activity;
    private String status;
    private String location;
    private Boolean endTimeUnspecified;
    private Set<String> recurrence;

}

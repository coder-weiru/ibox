package ibox.iplanner.api.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class EventAttribute extends TodoAttribute {

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant start;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant end;
    private Frequency frequency;
    private Set<String> recurrence;

    @Override
    TodoFeature feature() {
        return TodoFeature.EVENT_FEATURE;
    }
}

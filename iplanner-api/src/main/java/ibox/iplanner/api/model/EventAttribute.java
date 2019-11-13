package ibox.iplanner.api.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof EventAttribute)) return false;

        EventAttribute that = (EventAttribute) o;

        return new org.apache.commons.lang3.builder.EqualsBuilder()
                .append(getStart(), that.getStart())
                .append(getEnd(), that.getEnd())
                .append(getFrequency(), that.getFrequency())
                .append(getRecurrence(), that.getRecurrence())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getStart())
                .append(getEnd())
                .append(getFrequency())
                .append(getRecurrence())
                .toHashCode();
    }
}

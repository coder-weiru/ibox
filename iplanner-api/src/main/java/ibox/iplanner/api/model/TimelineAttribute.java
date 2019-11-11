package ibox.iplanner.api.model;


import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TimelineAttribute extends TodoAttribute {

    private Instant startBy;
    private Instant completeBy;

    @Override
    TodoFeature feature() {
        return TodoFeature.TIMELINE_FEATURE;
    }
}

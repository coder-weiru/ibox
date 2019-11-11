package ibox.iplanner.api.model;


import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class TagAttribute extends TodoAttribute {

    private List<Tag> tags = new ArrayList<>();

    @Override
    TodoFeature feature() {
        return TodoFeature.TAGGING_FEATURE;
    }
}

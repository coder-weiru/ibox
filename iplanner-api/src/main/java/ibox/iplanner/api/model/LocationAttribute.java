package ibox.iplanner.api.model;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class LocationAttribute extends TodoAttribute {

    private String location;

    @Override
    TodoFeature feature() {
        return TodoFeature.LOCATION_FEATURE;
    }
}

package ibox.iplanner.api.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class Meeting extends Activity {

    private EventAttribute eventInfo = new EventAttribute();
    private LocationAttribute locationInfo = new LocationAttribute();

    public Meeting() {
        super();
    }

    public Set<TodoFeature> getSupportedFeatures() {
        return new HashSet(Arrays.asList(new TodoFeature[] {
                TodoFeature.TAGGING_FEATURE,
                TodoFeature.EVENT_FEATURE,
                TodoFeature.LOCATION_FEATURE
        } ));
    }
}

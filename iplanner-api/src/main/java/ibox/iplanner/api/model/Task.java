package ibox.iplanner.api.model;


import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class Task extends Activity {

    private TimelineAttribute timeline = new TimelineAttribute();

    public Task() {
        super();
    }

    public Set<TodoFeature> getSupportedFeatures() {
        return new HashSet(Arrays.asList(new TodoFeature[] {
                TodoFeature.TAGGING_FEATURE,
                TodoFeature.TIMELINE_FEATURE
        } ));
    }

}

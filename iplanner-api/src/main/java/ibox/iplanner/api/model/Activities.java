package ibox.iplanner.api.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Activities {

    public static final String MEETING_TYPE = "meeting";
    public static final String TASK_TYPE = "task";

    private Activities() {
    }

    public static Set<TodoFeature> getAllFeatures() {
        return new HashSet(Arrays.asList(new TodoFeature[] {
                TodoFeature.TAGGING_FEATURE,
                TodoFeature.EVENT_FEATURE,
                TodoFeature.LOCATION_FEATURE,
                TodoFeature.TIMELINE_FEATURE
        } ));
    }

    public static Set<TodoFeature> getSupportedFeatures(Class activityType) {
        if (Meeting.class.getName().equals(activityType.getName())) {
            return new HashSet(Arrays.asList(new TodoFeature[] {
                    TodoFeature.TAGGING_FEATURE,
                    TodoFeature.EVENT_FEATURE,
                    TodoFeature.LOCATION_FEATURE
            } ));
        }
        else if (Task.class.getName().equals(activityType.getName())) {
            return new HashSet(Arrays.asList(new TodoFeature[] {
                    TodoFeature.TAGGING_FEATURE,
                    TodoFeature.TIMELINE_FEATURE
            } ));
        }
        else {
            return new HashSet(Arrays.asList(new TodoFeature[] {
                    TodoFeature.TAGGING_FEATURE
            } ));
        }
    }

    public static Class<? extends Activity> getActivityType(String activityType) {
        if (MEETING_TYPE.equals(activityType)) {
            return Meeting.class;
        }
        else if (TASK_TYPE.equals(activityType)) {
            return Task.class;
        }
        else {
            return Activity.class;
        }
    }

    public static Activity newInstanceOf(String activityType) {
        try {
            return getActivityType(activityType).newInstance();
        } catch (InstantiationException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        }
    }
}

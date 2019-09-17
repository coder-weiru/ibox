package ibox.iplanner.api.util;

import ibox.iplanner.api.model.Activity;
import ibox.iplanner.api.model.ActivityStatus;
import ibox.iplanner.api.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ActivityUtil extends BaseEntityUtil {

    public static String anyActivityTitle() {
        return anyTitle();
    }

    public static String anyActivityType() {
        return anyType();
    }

    public static User anyActivityCreator() {
        return anyUser();
    }

    public static ActivityStatus anyActivityStatus() {
        return Arrays.asList(new ActivityStatus[] {
                ActivityStatus.ACTIVE,
                ActivityStatus.INACTIVE
        }).get(new Random().nextInt(2));
    }

    public static Activity anyActivity() {
        Activity activity = anyActivityWithoutId();
        activity.setId(anyShortId());
        return activity;
    }

    public static Activity anyActivityWithoutId() {
        Activity activity = new Activity();
        activity.setTitle(anyActivityTitle());
        activity.setDescription(anyDescription());
        activity.setStatus(anyActivityStatus().name());
        activity.setType(anyActivityType());
        activity.setCreated(anyCreatedTime());
        activity.setUpdated(anyUpdatedTime());
        activity.setCreator(anyActivityCreator());

        return activity;
    }

    public static List<Activity> anyActivityList() {
        int size = new Random().nextInt(10);
        List<Activity> activityList = new ArrayList<>();
        int i = 0;
        while (i < size) {
            activityList.add(anyActivity());
            i ++;
        }
        return activityList;
    }
}

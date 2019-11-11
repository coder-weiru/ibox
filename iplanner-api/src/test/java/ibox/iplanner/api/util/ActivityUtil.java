package ibox.iplanner.api.util;

import ibox.iplanner.api.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ActivityUtil extends BaseEntityUtil {

    public static String anyActivityTitle() {
        return anyTitle();
    }

    public static String anyActivityType() {
        return "activity";
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
        activity.setActivityStatus(anyActivityStatus());
        activity.setActivityType(anyActivityType());
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

    public static TagAttribute anyTagAttribute() {
        return TagAttribute.builder()
                .tags( Arrays.asList( new Tag[]{
                        Tag.builder()
                        .value("tag1")
                        .rgbHexCode("#567890")
                        .build(),
                        Tag.builder()
                        .value("tag1")
                        .rgbHexCode("#5FFF90")
                        .build()
                }))
                .build();
    }
}

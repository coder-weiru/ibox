package ibox.iplanner.api.service;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import ibox.iplanner.api.model.Activity;
import ibox.iplanner.api.model.ActivityStatus;
import ibox.iplanner.api.model.User;
import ibox.iplanner.api.util.ActivityUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class ActivityDataServiceIntegrationTest extends LocalDynamoDBIntegrationTestSupport {

    private static ActivityDataService activityDataService;

    @BeforeClass
    public static void setup() {
        dynamoDBSetup.createActivityTable(10L, 5L);

        activityDataService = new ActivityDataService(new DynamoDB(amazonDynamoDB));
    }

    @Test
    public void givenValidActivity_addActivity_shouldCreateRecord() {

        Activity activity = ActivityUtil.anyActivity();

        activityDataService.addActivity(activity);

        Activity dbActivity = activityDataService.getActivity(activity.getId());

        verifyActivitiesAreEqual(activity, dbActivity);
    }

    @Test
    public void givenValidActivities_addActivities_shouldCreateRecords() {

        List<Activity> activities = ActivityUtil.anyActivityList();

        activityDataService.addActivities(activities);

        activities.stream().forEach(e -> {
            String id = e.getId();

            Activity dbActivity = activityDataService.getActivity(e.getId());

            verifyActivitiesAreEqual(e, dbActivity);

        });
    }

    @Test
    public void givenActivitiesWithCreators_getMyActivities_shouldReturnOnlyCreatorActivities() {
        User creator1 = ActivityUtil.anyActivityCreator();
        User creator2 = ActivityUtil.anyActivityCreator();

        Instant now = Instant.now();

        Activity activity1 = ActivityUtil.anyActivity();
        activity1.setCreator(creator1);
        activity1.setStatus(ActivityStatus.ACTIVE.name());

        Activity activity2 = ActivityUtil.anyActivity();
        activity2.setCreator(creator1);
        activity2.setStatus(ActivityStatus.ACTIVE.name());

        Activity activity3 = ActivityUtil.anyActivity();
        activity3.setCreator(creator1);
        activity3.setStatus(ActivityStatus.INACTIVE.name());

        Activity activity4 = ActivityUtil.anyActivity();
        activity4.setCreator(creator2);
        activity4.setStatus(ActivityStatus.INACTIVE.name());

        Activity activity5 = ActivityUtil.anyActivity();
        activity5.setCreator(creator1);
        activity5.setStatus(ActivityStatus.ACTIVE.name());

        Activity activity6 = ActivityUtil.anyActivity();
        activity6.setCreator(creator1);
        activity6.setStatus(ActivityStatus.ACTIVE.name());

        List<Activity> activities = Arrays.asList( new Activity[] {activity1, activity2, activity3, activity4, activity5, activity6});

        activityDataService.addActivities(activities);

        List<Activity> myActivitys = activityDataService.getMyActivities(creator1.getId(), ActivityStatus.ACTIVE.name(), null);

        assertThat(myActivitys.size(), is(equalTo(4)));


    }

    private void verifyActivitiesAreEqual(Activity expected, Activity actual) {

        assertThat(expected.getId(), is(equalTo(actual.getId())));
        assertThat(expected.getTitle(), is(equalTo(actual.getTitle())));
        assertThat(expected.getDescription(), is(equalTo(actual.getDescription())));
        assertThat(expected.getCreator().getId(), is(equalTo(actual.getCreator().getId())));
        assertThat(expected.getCreator().getDisplayName(), is(equalTo(actual.getCreator().getDisplayName())));
        assertThat(expected.getCreator().getEmail(), is(equalTo(actual.getCreator().getEmail())));
        assertThat(expected.getCreator().getSelf(), is(equalTo(actual.getCreator().getSelf())));
        assertThat(expected.getCreated(), is(equalTo(actual.getCreated())));
        assertThat(expected.getUpdated(), is(equalTo(actual.getUpdated())));
        assertThat(expected.getTemplate(), is(equalTo(actual.getTemplate())));
        assertThat(expected.getStatus(), is(equalTo(actual.getStatus())));
    }

}

package ibox.iplanner.api.service;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import ibox.iplanner.api.model.Activity;
import ibox.iplanner.api.model.ActivityStatus;
import ibox.iplanner.api.model.User;
import ibox.iplanner.api.model.updatable.Updatable;
import ibox.iplanner.api.model.updatable.UpdatableAttribute;
import ibox.iplanner.api.model.updatable.UpdatableKey;
import ibox.iplanner.api.model.updatable.UpdateAction;
import ibox.iplanner.api.service.dbmodel.ActivityDefinition;
import ibox.iplanner.api.util.ActivityUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public void givenValidUpdatable_updateActivity_shouldUpdateRecord() {

        Activity activity = ActivityUtil.anyActivity();

        activityDataService.addActivity(activity);

        Activity dbActivity = activityDataService.getActivity(activity.getId());

        String newTitle = "new title";
        String newDescription = "new description";
        String newTemplate = "new template";
        Set<UpdatableAttribute> updatableAttributeSet = new HashSet<>();
        updatableAttributeSet.add( UpdatableAttribute.builder()
            .attributeName(ActivityDefinition.FIELD_NAME_TITLE)
            .action(UpdateAction.UPDATE)
            .value(newTitle)
            .build());
        updatableAttributeSet.add( UpdatableAttribute.builder()
                .attributeName(ActivityDefinition.FIELD_NAME_DESCRIPTION)
                .action(UpdateAction.UPDATE)
                .value(newDescription)
                .build());
        updatableAttributeSet.add( UpdatableAttribute.builder()
                .attributeName(ActivityDefinition.FIELD_NAME_TYPE)
                .action(UpdateAction.UPDATE)
                .value(newTemplate)
                .build());

        Updatable updatable = Updatable.builder()
                .objectType("activity")
                .primaryKey(new UpdatableKey()
                        .addComponent(ActivityDefinition.FIELD_NAME_ID, dbActivity.getId()))
                .updatableAttributes(updatableAttributeSet)
                .build();

        Activity updated = activityDataService.updateActivity(updatable);

        assertThat(updated.getTitle(), is(equalTo(newTitle)));
        assertThat(updated.getDescription(), is(equalTo(newDescription)));
        assertThat(updated.getType(), is(equalTo(newTemplate)));

    }

    @Test
    public void givenValidId_deleteActivity_shouldUpdateActivityStatus() {

        Activity activity = ActivityUtil.anyActivity();
        activity.setStatus(ActivityStatus.ACTIVE.name());

        activityDataService.addActivity(activity);

        Activity dbActivity = activityDataService.getActivity(activity.getId());

        Activity deleted = activityDataService.deleteActivity(dbActivity.getId());

        assertThat(deleted.getStatus(), is(equalTo(ActivityStatus.INACTIVE.name())));

        Activity theActivity = activityDataService.getActivity(dbActivity.getId());

        assertThat(theActivity.getStatus(), is(equalTo(ActivityStatus.INACTIVE.name())));

    }

    @Test(expected = AmazonDynamoDBException.class)
    public void givenValidUpdatable_updateActivity_shouldNotUpdateKeyField() {

        Activity activity = ActivityUtil.anyActivity();

        activityDataService.addActivity(activity);

        Activity dbActivity = activityDataService.getActivity(activity.getId());

        Set<UpdatableAttribute> updatableAttributeSet = new HashSet<>();
        updatableAttributeSet.add( UpdatableAttribute.builder()
                .attributeName(ActivityDefinition.FIELD_NAME_ID)
                .action(UpdateAction.UPDATE)
                .value("1234567890")
                .build());
        Updatable updatable = Updatable.builder()
                .objectType("activity")
                .primaryKey(new UpdatableKey()
                        .addComponent(ActivityDefinition.FIELD_NAME_ID, dbActivity.getId()))
                .updatableAttributes(updatableAttributeSet)
                .build();

        Activity updated = activityDataService.updateActivity(updatable);
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
        assertThat(expected.getType(), is(equalTo(actual.getType())));
        assertThat(expected.getStatus(), is(equalTo(actual.getStatus())));
    }

}

package ibox.iplanner.api.service;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import ibox.iplanner.api.lambda.exception.InvalidInputException;
import ibox.iplanner.api.model.*;
import ibox.iplanner.api.util.ActivityUtil;
import ibox.iplanner.api.util.MeetingUtil;
import ibox.iplanner.api.util.TaskUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static ibox.iplanner.api.service.TestHelper.*;
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
    public void givenValidMultiTypeActivities_addActivities_shouldCreateRecordsForAllTypes() {

        List<Activity> multiActivities = Arrays.asList(new Activity[]{
                ActivityUtil.anyActivity(),
                MeetingUtil.anyMeeting(),
                TaskUtil.anyTask()
        });

        activityDataService.addActivities(multiActivities);

        multiActivities.stream().forEach(e -> {
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

        dbActivity.setTitle("new title");
        dbActivity.setDescription("new description");
        dbActivity.setActivityType("new activity");

        Activity updated = activityDataService.updateActivity(dbActivity);

        assertThat(updated.getTitle(), is(equalTo("new title")));
        assertThat(updated.getDescription(), is(equalTo("new description")));
        assertThat(updated.getActivityType(), is(equalTo(activity.getActivityType())));

    }

    @Test
    public void givenValidUpdatable_updateActivity_shouldUpdateItsAttributeSet() {

        Meeting meeting = MeetingUtil.anyMeeting();

        activityDataService.addActivity(meeting);

        Activity dbActivity = activityDataService.getActivity(meeting.getId());

        Instant now = Instant.now();
        EventAttribute eventAttribute = (EventAttribute) dbActivity.getAttribute(TodoFeature.EVENT_FEATURE);
        eventAttribute.setStart(now);
        eventAttribute.setFrequency(Frequency.ONE_TIME);
        eventAttribute.getRecurrence().clear();

        TagAttribute tagAttribute = (TagAttribute) dbActivity.getAttribute(TodoFeature.TAGGING_FEATURE);
        tagAttribute.getTags().clear();
        tagAttribute.getTags().add(new Tag("abc", "#111111"));

        LocationAttribute locationAttribute = (LocationAttribute) dbActivity.getAttribute(TodoFeature.LOCATION_FEATURE);
        locationAttribute.setLocation("new location");

        dbActivity.setAttribute(eventAttribute);
        dbActivity.setAttribute(tagAttribute);
        dbActivity.setAttribute(locationAttribute);

        Activity updated = activityDataService.updateActivity(dbActivity);

        EventAttribute newEventAttribute = (EventAttribute) updated.getAttribute(TodoFeature.EVENT_FEATURE);
        assertThat(newEventAttribute.getStart(), is(equalTo(now)));
        assertThat(newEventAttribute.getFrequency(), is(equalTo(Frequency.ONE_TIME)));
        assertThat(newEventAttribute.getRecurrence().size(), is(equalTo(0)));

        TagAttribute newTagAttribute = (TagAttribute) dbActivity.getAttribute(TodoFeature.TAGGING_FEATURE);
        assertThat(newTagAttribute.getTags().size(), is(equalTo(0)));
        assertThat(newTagAttribute.getTags().get(0).getValue(), is(equalTo("abc")));
        assertThat(newTagAttribute.getTags().get(0).getRgbHexCode(), is(equalTo("#111111")));

        LocationAttribute newLocationAttribute = (LocationAttribute) dbActivity.getAttribute(TodoFeature.LOCATION_FEATURE);
        assertThat(newLocationAttribute.getLocation(), is(equalTo("new location")));

    }

    @Test
    public void givenValidId_deleteActivity_shouldUpdateActivityStatus() {

        Activity activity = ActivityUtil.anyActivity();
        activity.setActivityStatus(ActivityStatus.ACTIVE);

        activityDataService.addActivity(activity);

        Activity dbActivity = activityDataService.getActivity(activity.getId());

        Activity deleted = activityDataService.deleteActivity(dbActivity.getId());

        assertThat(deleted.getActivityStatus(), is(equalTo(ActivityStatus.INACTIVE)));

        Activity theActivity = activityDataService.getActivity(dbActivity.getId());

        assertThat(theActivity.getActivityStatus(), is(equalTo(ActivityStatus.INACTIVE)));

    }

    @Test(expected = InvalidInputException.class)
    public void givenValidUpdatable_updateActivity_shouldNotUpdateKeyField() {

        Activity activity = ActivityUtil.anyActivity();

        activityDataService.addActivity(activity);

        Activity dbActivity = activityDataService.getActivity(activity.getId());

        dbActivity.setId("123456789");

        Activity updated = activityDataService.updateActivity(dbActivity);
    }

    @Test
    public void givenActivitiesWithCreators_getMyActivities_shouldReturnOnlyCreatorActivities() {
        User creator1 = ActivityUtil.anyActivityCreator();
        User creator2 = ActivityUtil.anyActivityCreator();

        Instant now = Instant.now();

        Activity activity1 = ActivityUtil.anyActivity();
        activity1.setCreator(creator1);
        activity1.setActivityStatus(ActivityStatus.ACTIVE);

        Activity activity2 = ActivityUtil.anyActivity();
        activity2.setCreator(creator1);
        activity2.setActivityStatus(ActivityStatus.ACTIVE);

        Activity activity3 = ActivityUtil.anyActivity();
        activity3.setCreator(creator1);
        activity3.setActivityStatus(ActivityStatus.INACTIVE);

        Activity activity4 = ActivityUtil.anyActivity();
        activity4.setCreator(creator2);
        activity4.setActivityStatus(ActivityStatus.INACTIVE);

        Activity activity5 = ActivityUtil.anyActivity();
        activity5.setCreator(creator1);
        activity5.setActivityStatus(ActivityStatus.ACTIVE);

        Activity activity6 = ActivityUtil.anyActivity();
        activity6.setCreator(creator1);
        activity6.setActivityStatus(ActivityStatus.ACTIVE);

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
        assertThat(expected.getActivityType(), is(equalTo(actual.getActivityType())));
        assertThat(expected.getActivityStatus(), is(equalTo(actual.getActivityStatus())));
        assertThat(expected.getAttributeSet().getAttributes().size(), is(equalTo(actual.getAttributeSet().getAttributes().size())));

        if (expected.getClass().equals(Meeting.class)) {
            verifyTaggingAttributeAreEqual((TagAttribute) expected.getAttribute(TodoFeature.TAGGING_FEATURE), (TagAttribute) actual.getAttribute(TodoFeature.TAGGING_FEATURE));
            verifyEventAttributeAreEqual((EventAttribute) expected.getAttribute(TodoFeature.EVENT_FEATURE), (EventAttribute) actual.getAttribute(TodoFeature.EVENT_FEATURE));
            verifyLocationAttributeAreEqual((LocationAttribute) expected.getAttribute(TodoFeature.LOCATION_FEATURE), (LocationAttribute) actual.getAttribute(TodoFeature.LOCATION_FEATURE));
        } else if (expected.getClass().equals(Task.class)) {
            verifyTaggingAttributeAreEqual((TagAttribute) expected.getAttribute(TodoFeature.TAGGING_FEATURE), (TagAttribute) actual.getAttribute(TodoFeature.TAGGING_FEATURE));
            verifyTimelineAttributeAreEqual((TimelineAttribute) expected.getAttribute(TodoFeature.TIMELINE_FEATURE), (TimelineAttribute) actual.getAttribute(TodoFeature.TIMELINE_FEATURE));
        }
    }


}

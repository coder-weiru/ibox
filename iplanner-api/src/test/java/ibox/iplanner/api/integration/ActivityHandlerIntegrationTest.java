package ibox.iplanner.api.integration;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import ibox.iplanner.api.lambda.runtime.TestContext;
import ibox.iplanner.api.model.Activity;
import ibox.iplanner.api.model.ActivityStatus;
import ibox.iplanner.api.model.User;
import ibox.iplanner.api.model.updatable.Updatable;
import ibox.iplanner.api.model.updatable.UpdatableAttribute;
import ibox.iplanner.api.model.updatable.UpdatableKey;
import ibox.iplanner.api.model.updatable.UpdateAction;
import ibox.iplanner.api.service.LocalDynamoDBIntegrationTestSupport;
import ibox.iplanner.api.service.dbmodel.ActivityDefinition;
import ibox.iplanner.api.util.ActivityUtil;
import ibox.iplanner.api.util.JsonUtil;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Instant;
import java.util.*;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ActivityHandlerIntegrationTest extends LocalDynamoDBIntegrationTestSupport {

    private AddActivityHandlerTestWrapper addActivityHandler = new AddActivityHandlerTestWrapper();
    private GetActivityHandlerTestWrapper getActivityHandler = new GetActivityHandlerTestWrapper();
    private ListActivityHandlerTestWrapper listActivityHandler = new ListActivityHandlerTestWrapper();
    private UpdateActivityHandlerTestWrapper updateActivityHandler = new UpdateActivityHandlerTestWrapper();
    private DeleteActivityHandlerTestWrapper deleteActivityHandler = new DeleteActivityHandlerTestWrapper();

    private TestContext testContext = TestContext.builder().build();

    private Activity activity;

    @BeforeClass
    public static void setupOnce() {
        dynamoDBSetup.createActivityTable(10L, 5L);
    }

    @Before
    public void setup() {
        activity = ActivityUtil.anyActivity();
    }

    @Test
    public void givenValidActivity_addActivity_shouldCreateRecord() {

        Activity added = addActivity(activity);

        Activity getActivity = getActivity(added.getId());

        verifyActivitiesAreEqual(activity, getActivity);
    }

    @Test
    public void givenValidActivities_addActivities_shouldCreateRecords() {

        List<Activity> activities = ActivityUtil.anyActivityList();

        List<Activity> added = addActivities(activities);

        added.stream().forEach(activity -> {
            Activity getActivity = getActivity(activity.getId());

            verifyActivitiesAreEqual(activity, getActivity);

        });

    }

    @Test
    public void givenValidUpdatable_updateActivity_shouldUpdateRecord() throws InterruptedException {

        Activity added = addActivity(activity);

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
                        .addComponent(ActivityDefinition.FIELD_NAME_ID, added.getId()))
                .updatableAttributes(updatableAttributeSet)
                .build();

        updateActivity(added.getId(), updatable);

        Activity updated = getActivity(added.getId());

        assertThat(updated.getTitle(), is(equalTo(newTitle)));
        assertThat(updated.getDescription(), is(equalTo(newDescription)));
        assertThat(updated.getType(), is(equalTo(newTemplate)));

        assertThat(updated.getCreator().getId(), is(equalTo(activity.getCreator().getId())));
        assertThat(updated.getCreator().getDisplayName(), is(equalTo(activity.getCreator().getDisplayName())));
        assertThat(updated.getCreator().getEmail(), is(equalTo(activity.getCreator().getEmail())));
        assertThat(updated.getCreator().getSelf(), is(equalTo(activity.getCreator().getSelf())));
        assertThat(updated.getCreated(), is(equalTo(activity.getCreated())));
        assertThat(updated.getUpdated(), is(equalTo(activity.getUpdated())));
        assertThat(updated.getStatus(), is(equalTo(activity.getStatus())));

    }

    @Test
    public void givenValidId_deleteActivity_shouldUpdateActivityStatus() {

        Activity added = addActivity(activity);

        deleteActivity(added.getId());

        Activity deleted = getActivity(added.getId());

        assertThat(deleted.getStatus(), is(equalTo(ActivityStatus.INACTIVE.name())));

    }

    @Test
    public void givenValidUpdatable_updateActivity_shouldNotUpdateKeyField() {

        Activity added = addActivity(activity);

        Set<UpdatableAttribute> updatableAttributeSet = new HashSet<>();
        updatableAttributeSet.add( UpdatableAttribute.builder()
                .attributeName(ActivityDefinition.FIELD_NAME_ID)
                .action(UpdateAction.UPDATE)
                .value("1234567890")
                .build());
        Updatable updatable = Updatable.builder()
                .objectType("activity")
                .primaryKey(new UpdatableKey()
                        .addComponent(ActivityDefinition.FIELD_NAME_ID, added.getId()))
                .updatableAttributes(updatableAttributeSet)
                .build();

        updateActivityResultInInternalServerError(added.getId(), updatable);
    }

    @Test
    public void givenActivitiesWithCreators_getMyActivitiesWithinTime_shouldReturnOnlyCreatorActivitiesWithinTime() {
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

        addActivities(activities);

        Instant timeWindowStart = now.plus(5, MINUTES);
        Instant timeWindowEnd = now.plus(35, MINUTES);

        List<Activity> listActivities = listActivities(creator1.getId(), timeWindowStart, timeWindowEnd, ActivityStatus.ACTIVE, null);
        assertThat(listActivities.size(), is(equalTo(4)));
    }

    private Activity addActivity(Activity activity) {
        APIGatewayProxyRequestEvent addRequestEvent = new APIGatewayProxyRequestEvent();
        addRequestEvent.setBody(JsonUtil.toJsonString(Arrays.asList( new Activity[] { activity})));
        APIGatewayProxyResponseEvent addResponseEvent = addActivityHandler.handleRequest(addRequestEvent, testContext);

        assertEquals(200, addResponseEvent.getStatusCode());

        List<Activity> added = (List<Activity>) JsonUtil.fromJsonString(addResponseEvent.getBody(), List.class, Activity.class);

        return added.get(0);
    }

    private List<Activity> addActivities(List<Activity> activities) {
        APIGatewayProxyRequestEvent addRequestEvent = new APIGatewayProxyRequestEvent();
        addRequestEvent.setBody(JsonUtil.toJsonString(activities));
        APIGatewayProxyResponseEvent addResponseEvent = addActivityHandler.handleRequest(addRequestEvent, testContext);

        assertEquals(200, addResponseEvent.getStatusCode());

        List<Activity> added = (List<Activity>) JsonUtil.fromJsonString(addResponseEvent.getBody(), List.class, Activity.class);

        return added;
    }

    private Activity getActivity(String activityId) {
        APIGatewayProxyRequestEvent getRequestEvent = new APIGatewayProxyRequestEvent();
        getRequestEvent.setPathParameters(Collections.singletonMap("activityId", activityId));
        APIGatewayProxyResponseEvent getResponseEvent = getActivityHandler.handleRequest(getRequestEvent, testContext);

        assertEquals(200, getResponseEvent.getStatusCode());

        Activity added = JsonUtil.fromJsonString(getResponseEvent.getBody(), Activity.class);

        return added;
    }

    private void deleteActivity(String activityId) {
        APIGatewayProxyRequestEvent deleteRequestEvent = new APIGatewayProxyRequestEvent();
        deleteRequestEvent.setPathParameters(Collections.singletonMap("activityId", activityId));
        APIGatewayProxyResponseEvent deleteResponseEvent = deleteActivityHandler.handleRequest(deleteRequestEvent, testContext);

        assertEquals(200, deleteResponseEvent.getStatusCode());
    }

    private void updateActivity(String activityId, Updatable updatable) {
        APIGatewayProxyRequestEvent updateRequestEvent = new APIGatewayProxyRequestEvent();
        updateRequestEvent.setPathParameters(Collections.singletonMap("activityId", activityId));
        updateRequestEvent.setBody(JsonUtil.toJsonString(updatable));
        APIGatewayProxyResponseEvent updateResponseEvent = updateActivityHandler.handleRequest(updateRequestEvent, testContext);

        assertEquals(200, updateResponseEvent.getStatusCode());
    }

    private void updateActivityResultInInternalServerError(String activityId, Updatable updatable) {
        APIGatewayProxyRequestEvent updateRequestEvent = new APIGatewayProxyRequestEvent();
        updateRequestEvent.setPathParameters(Collections.singletonMap("activityId", activityId));
        updateRequestEvent.setBody(JsonUtil.toJsonString(updatable));
        APIGatewayProxyResponseEvent updateResponseEvent = updateActivityHandler.handleRequest(updateRequestEvent, testContext);

        assertEquals(500, updateResponseEvent.getStatusCode());
    }

    private List<Activity> listActivities(String creatorId, Instant timeWindowStart, Instant timeWindowEnd, ActivityStatus status, Integer limit) {
        APIGatewayProxyRequestEvent listRequestEvent = new APIGatewayProxyRequestEvent();
        listRequestEvent.setPathParameters(Collections.singletonMap("creatorId", creatorId));
        Map<String, String> requestParams = new HashMap<>();
        if (Optional.ofNullable(timeWindowStart).isPresent()) {
            requestParams.put("start", timeWindowStart.toString());
        }
        if (Optional.ofNullable(timeWindowEnd).isPresent()) {
            requestParams.put("end", timeWindowEnd.toString());
        }
        if (Optional.ofNullable(status).isPresent()) {
            requestParams.put("status", status.name());
        }
        if (Optional.ofNullable(limit).isPresent()) {
            requestParams.put("limit", limit.toString());
        }
        listRequestEvent.setQueryStringParameters(requestParams);

        APIGatewayProxyResponseEvent getResponseEvent = listActivityHandler.handleRequest(listRequestEvent, testContext);

        assertEquals(200, getResponseEvent.getStatusCode());

        List<Activity> listActivities = (List<Activity>)JsonUtil.fromJsonString(getResponseEvent.getBody(), List.class, Activity.class);

        return listActivities;
    }

    private void verifyActivitiesAreEqual(Activity expected, Activity actual) {

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

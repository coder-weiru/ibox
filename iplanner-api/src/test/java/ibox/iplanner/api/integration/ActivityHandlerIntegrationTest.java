package ibox.iplanner.api.integration;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import ibox.iplanner.api.lambda.runtime.TestContext;
import ibox.iplanner.api.model.Activity;
import ibox.iplanner.api.model.ActivityStatus;
import ibox.iplanner.api.model.User;
import ibox.iplanner.api.service.LocalDynamoDBIntegrationTestSupport;
import ibox.iplanner.api.util.ActivityUtil;
import ibox.iplanner.api.util.JsonUtil;
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

    private TestContext testContext = TestContext.builder().build();

    @BeforeClass
    public static void setup() {
        dynamoDBSetup.createActivityTable(10L, 5L);
    }

    @Test
    public void givenValidActivity_addActivity_shouldCreateRecord() {

        Activity activity = ActivityUtil.anyActivity();

        APIGatewayProxyRequestEvent addRequestEvent = new APIGatewayProxyRequestEvent();
        addRequestEvent.setBody(JsonUtil.toJsonString(Arrays.asList( new Activity[] { activity })));
        APIGatewayProxyResponseEvent addResponseEvent = addActivityHandler.handleRequest(addRequestEvent, testContext);

        assertEquals(200, addResponseEvent.getStatusCode());

        List<Activity> added = (List<Activity>) JsonUtil.fromJsonString(addResponseEvent.getBody(), List.class, Activity.class);
        APIGatewayProxyRequestEvent getRequestEvent = new APIGatewayProxyRequestEvent();
        getRequestEvent.setPathParameters(Collections.singletonMap("activityId", added.get(0).getId()));
        APIGatewayProxyResponseEvent getResponseEvent = getActivityHandler.handleRequest(getRequestEvent, testContext);

        assertEquals(200, getResponseEvent.getStatusCode());

        Activity getActivity = JsonUtil.fromJsonString(getResponseEvent.getBody(), Activity.class);

        verifyActivitiesAreEqual(activity, getActivity);
    }

    @Test
    public void givenValidActivities_addActivities_shouldCreateRecords() {

        List<Activity> activities = ActivityUtil.anyActivityList();

        APIGatewayProxyRequestEvent addRequestEvent = new APIGatewayProxyRequestEvent();
        addRequestEvent.setBody(JsonUtil.toJsonString(activities));
        APIGatewayProxyResponseEvent addResponseEvent = addActivityHandler.handleRequest(addRequestEvent, testContext);

        assertEquals(200, addResponseEvent.getStatusCode());

        List<Activity> added = (List<Activity>) JsonUtil.fromJsonString(addResponseEvent.getBody(), List.class, Activity.class);

        APIGatewayProxyRequestEvent getRequestEvent = new APIGatewayProxyRequestEvent();

        added.stream().forEach(activity -> {
            String id = activity.getId();

            getRequestEvent.setPathParameters(Collections.singletonMap("activityId", activity.getId()));
            APIGatewayProxyResponseEvent getResponseEvent = getActivityHandler.handleRequest(getRequestEvent, testContext);

            assertEquals(200, getResponseEvent.getStatusCode());

            Activity getActivity = JsonUtil.fromJsonString(getResponseEvent.getBody(), Activity.class);

            verifyActivitiesAreEqual(activity, getActivity);

        });

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

        APIGatewayProxyRequestEvent addRequestEvent = new APIGatewayProxyRequestEvent();
        addRequestEvent.setBody(JsonUtil.toJsonString(activities));
        APIGatewayProxyResponseEvent addResponseEvent = addActivityHandler.handleRequest(addRequestEvent, testContext);

        assertEquals(200, addResponseEvent.getStatusCode());

        APIGatewayProxyRequestEvent getRequestEvent = new APIGatewayProxyRequestEvent();

        Instant timeWindowStart = now.plus(5, MINUTES);
        Instant timeWindowEnd = now.plus(35, MINUTES);

        APIGatewayProxyRequestEvent listRequestEvent = new APIGatewayProxyRequestEvent();
        listRequestEvent.setPathParameters(Collections.singletonMap("creatorId", creator1.getId()));
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("start", timeWindowStart.toString());
        requestParams.put("end", timeWindowEnd.toString());
        listRequestEvent.setQueryStringParameters(requestParams);

        APIGatewayProxyResponseEvent getResponseEvent = listActivityHandler.handleRequest(listRequestEvent, testContext);

        assertEquals(200, getResponseEvent.getStatusCode());

        List<Activity> listActivities = (List<Activity>)JsonUtil.fromJsonString(getResponseEvent.getBody(), List.class, Activity.class);

        assertThat(listActivities.size(), is(equalTo(4)));
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
        assertThat(expected.getTemplate(), is(equalTo(actual.getTemplate())));
        assertThat(expected.getStatus(), is(equalTo(actual.getStatus())));

    }

}

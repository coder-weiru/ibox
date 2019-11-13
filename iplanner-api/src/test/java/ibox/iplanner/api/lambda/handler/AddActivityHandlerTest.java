package ibox.iplanner.api.lambda.handler;

import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.util.StringUtils;
import ibox.iplanner.api.config.DaggerIPlannerComponent;
import ibox.iplanner.api.config.IPlannerComponent;
import ibox.iplanner.api.lambda.runtime.TestContext;
import ibox.iplanner.api.model.Activity;
import ibox.iplanner.api.model.ApiError;
import ibox.iplanner.api.model.User;
import ibox.iplanner.api.service.ActivityDataService;
import ibox.iplanner.api.util.ActivityUtil;
import ibox.iplanner.api.util.JsonUtil;
import ibox.iplanner.api.util.MeetingUtil;
import ibox.iplanner.api.util.TaskUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static ibox.iplanner.api.util.ApiErrorConstants.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AddActivityHandlerTest {

    @InjectMocks
    private AddActivityHandler handler = new AddActivityHandler();

    private List<Activity> activities;

    @Mock
    private ActivityDataService activityDataServiceMock;

    public AddActivityHandlerTest() {
        IPlannerComponent iPlannerComponent = DaggerIPlannerComponent.builder().build();
        iPlannerComponent.inject(handler);
    }

    @Before
    public void setUp() {
        this.activities = Arrays.asList(new Activity[]{
                ActivityUtil.anyActivity(),
                ActivityUtil.anyActivity(),
                ActivityUtil.anyActivity()
        });
    }

    @Test
    public void createActivity_shouldInvokeActivityDateServiceWithActivityList() throws Exception {

        doNothing().when(activityDataServiceMock).addActivities(any(List.class));

        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setBody(JsonUtil.toJsonString(activities));
        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(200, responseEvent.getStatusCode());

        ArgumentCaptor<List> requestCaptor = ArgumentCaptor.forClass(List.class);

        verify(activityDataServiceMock, times(1)).addActivities(requestCaptor.capture());

        verifyNoMoreInteractions(activityDataServiceMock);

        List<Activity> argument = requestCaptor.getValue();

        assertThat(argument.size(), is(equalTo(activities.size())));
        assertThat(argument.get(0).getId(), not(equalTo(activities.get(0).getId())));
        assertThat(argument.get(0).getTitle(), is(equalTo(activities.get(0).getTitle())));
        assertThat(argument.get(0).getDescription(), is(equalTo(activities.get(0).getDescription())));
        assertThat(argument.get(0).getActivityType(), is(equalTo(activities.get(0).getActivityType())));
        assertThat(argument.get(0).getStatus(), is(equalTo(activities.get(0).getStatus())));
        assertThat(argument.get(0).getCreator().getId(), is(equalTo(activities.get(0).getCreator().getId())));
        assertThat(argument.get(0).getCreator().getEmail(), is(equalTo(activities.get(0).getCreator().getEmail())));
        assertThat(argument.get(0).getCreator().getDisplayName(), is(equalTo(activities.get(0).getCreator().getDisplayName())));
        assertThat(argument.get(0).getCreator().getSelf(), is(equalTo(activities.get(0).getCreator().getSelf())));
        assertThat(argument.get(0).getCreated(), is(equalTo(activities.get(0).getCreated())));
        assertThat(argument.get(0).getUpdated(), is(equalTo(activities.get(0).getUpdated())));

        assertThat(argument.get(1).getId(), not(equalTo(activities.get(1).getId())));
        assertThat(argument.get(1).getTitle(), is(equalTo(activities.get(1).getTitle())));
        assertThat(argument.get(1).getDescription(), is(equalTo(activities.get(1).getDescription())));
        assertThat(argument.get(1).getActivityType(), is(equalTo(activities.get(1).getActivityType())));
        assertThat(argument.get(1).getStatus(), is(equalTo(activities.get(1).getStatus())));
        assertThat(argument.get(1).getCreator().getId(), is(equalTo(activities.get(1).getCreator().getId())));
        assertThat(argument.get(1).getCreator().getEmail(), is(equalTo(activities.get(1).getCreator().getEmail())));
        assertThat(argument.get(1).getCreator().getDisplayName(), is(equalTo(activities.get(1).getCreator().getDisplayName())));
        assertThat(argument.get(1).getCreator().getSelf(), is(equalTo(activities.get(1).getCreator().getSelf())));
        assertThat(argument.get(1).getCreated(), is(equalTo(activities.get(1).getCreated())));
        assertThat(argument.get(1).getUpdated(), is(equalTo(activities.get(1).getUpdated())));

        assertThat(argument.get(2).getId(), not(equalTo(activities.get(2).getId())));
        assertThat(argument.get(2).getTitle(), is(equalTo(activities.get(2).getTitle())));
        assertThat(argument.get(2).getDescription(), is(equalTo(activities.get(2).getDescription())));
        assertThat(argument.get(2).getActivityType(), is(equalTo(activities.get(2).getActivityType())));
        assertThat(argument.get(2).getStatus(), is(equalTo(activities.get(2).getStatus())));
        assertThat(argument.get(2).getCreator().getId(), is(equalTo(activities.get(2).getCreator().getId())));
        assertThat(argument.get(2).getCreator().getEmail(), is(equalTo(activities.get(2).getCreator().getEmail())));
        assertThat(argument.get(2).getCreator().getDisplayName(), is(equalTo(activities.get(2).getCreator().getDisplayName())));
        assertThat(argument.get(2).getCreator().getSelf(), is(equalTo(activities.get(2).getCreator().getSelf())));
        assertThat(argument.get(2).getCreated(), is(equalTo(activities.get(2).getCreated())));
        assertThat(argument.get(2).getUpdated(), is(equalTo(activities.get(2).getUpdated())));
    }

    @Test
    public void createActivity_shouldAddActivitiesOfVariousTypes() throws Exception {

        doNothing().when(activityDataServiceMock).addActivities(any(List.class));

        List<Activity> multiActivities = Arrays.asList(new Activity[]{
                ActivityUtil.anyActivity(),
                MeetingUtil.anyMeeting(),
                TaskUtil.anyTask()
        });

        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setBody(JsonUtil.toJsonString(multiActivities));
        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(200, responseEvent.getStatusCode());

        ArgumentCaptor<List> requestCaptor = ArgumentCaptor.forClass(List.class);

        verify(activityDataServiceMock, times(1)).addActivities(requestCaptor.capture());

        verifyNoMoreInteractions(activityDataServiceMock);

        List<Activity> argument = requestCaptor.getValue();

        assertThat(argument.size(), is(equalTo(multiActivities.size())));
        assertThat(argument.get(0).getId(), not(equalTo(multiActivities.get(0).getId())));
        assertThat(argument.get(0).getTitle(), is(equalTo(multiActivities.get(0).getTitle())));
        assertThat(argument.get(0).getDescription(), is(equalTo(multiActivities.get(0).getDescription())));
        assertThat(argument.get(0).getActivityType(), is(equalTo(multiActivities.get(0).getActivityType())));
        assertThat(argument.get(0).getStatus(), is(equalTo(multiActivities.get(0).getStatus())));
        assertThat(argument.get(0).getCreator().getId(), is(equalTo(multiActivities.get(0).getCreator().getId())));
        assertThat(argument.get(0).getCreator().getEmail(), is(equalTo(multiActivities.get(0).getCreator().getEmail())));
        assertThat(argument.get(0).getCreator().getDisplayName(), is(equalTo(multiActivities.get(0).getCreator().getDisplayName())));
        assertThat(argument.get(0).getCreator().getSelf(), is(equalTo(multiActivities.get(0).getCreator().getSelf())));
        assertThat(argument.get(0).getCreated(), is(equalTo(multiActivities.get(0).getCreated())));
        assertThat(argument.get(0).getUpdated(), is(equalTo(multiActivities.get(0).getUpdated())));

        assertThat(argument.get(1).getId(), not(equalTo(multiActivities.get(1).getId())));
        assertThat(argument.get(1).getTitle(), is(equalTo(multiActivities.get(1).getTitle())));
        assertThat(argument.get(1).getDescription(), is(equalTo(multiActivities.get(1).getDescription())));
        assertThat(argument.get(1).getActivityType(), is(equalTo(multiActivities.get(1).getActivityType())));
        assertThat(argument.get(1).getStatus(), is(equalTo(multiActivities.get(1).getStatus())));
        assertThat(argument.get(1).getCreator().getId(), is(equalTo(multiActivities.get(1).getCreator().getId())));
        assertThat(argument.get(1).getCreator().getEmail(), is(equalTo(multiActivities.get(1).getCreator().getEmail())));
        assertThat(argument.get(1).getCreator().getDisplayName(), is(equalTo(multiActivities.get(1).getCreator().getDisplayName())));
        assertThat(argument.get(1).getCreator().getSelf(), is(equalTo(multiActivities.get(1).getCreator().getSelf())));
        assertThat(argument.get(1).getCreated(), is(equalTo(multiActivities.get(1).getCreated())));
        assertThat(argument.get(1).getUpdated(), is(equalTo(multiActivities.get(1).getUpdated())));

        assertThat(argument.get(2).getId(), not(equalTo(multiActivities.get(2).getId())));
        assertThat(argument.get(2).getTitle(), is(equalTo(multiActivities.get(2).getTitle())));
        assertThat(argument.get(2).getDescription(), is(equalTo(multiActivities.get(2).getDescription())));
        assertThat(argument.get(2).getActivityType(), is(equalTo(multiActivities.get(2).getActivityType())));
        assertThat(argument.get(2).getStatus(), is(equalTo(multiActivities.get(2).getStatus())));
        assertThat(argument.get(2).getCreator().getId(), is(equalTo(multiActivities.get(2).getCreator().getId())));
        assertThat(argument.get(2).getCreator().getEmail(), is(equalTo(multiActivities.get(2).getCreator().getEmail())));
        assertThat(argument.get(2).getCreator().getDisplayName(), is(equalTo(multiActivities.get(2).getCreator().getDisplayName())));
        assertThat(argument.get(2).getCreator().getSelf(), is(equalTo(multiActivities.get(2).getCreator().getSelf())));
        assertThat(argument.get(2).getCreated(), is(equalTo(multiActivities.get(2).getCreated())));
        assertThat(argument.get(2).getUpdated(), is(equalTo(multiActivities.get(2).getUpdated())));
    }

    @Test
    public void createActivity_shouldReturnBadRequestMessageIfMissingTitle() throws Exception {
        Activity activity = ActivityUtil.anyActivity();
        activity.setTitle(null);

        verifyBadRequestMessage(Arrays.asList(new Activity[] {
                activity
        }));
    }

    @Test
    public void createActivity_shouldReturnBadRequestMessageIfMissingCreator() throws Exception {
        Activity activity = ActivityUtil.anyActivity();
        activity.setCreator(null);

        verifyBadRequestMessage(Arrays.asList(new Activity[] {
                activity
        }));
    }

    @Test
    public void createActivity_shouldReturnBadRequestMessageIfMissingTemplate() throws Exception {
        Activity activity = ActivityUtil.anyActivity();
        activity.setActivityType(null);

        verifyBadRequestMessage(Arrays.asList(new Activity[] {
                activity
        }));
    }

    @Test
    public void createActivity_shouldReturnBadRequestMessageIfCreatorInvalid() throws Exception {
        Activity activity = ActivityUtil.anyActivity();

        User creator = new User();

        activity.setCreator(creator);

        verifyBadRequestMessage(Arrays.asList(new Activity[] {
                activity
        }));
    }


    private void verifyBadRequestMessage(List<Activity> activities) throws Exception {

        doNothing().when(activityDataServiceMock).addActivities(any(List.class));

        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setBody(JsonUtil.toJsonString(activities));
        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(400, responseEvent.getStatusCode());

        ApiError error = JsonUtil.fromJsonString(responseEvent.getBody(), ApiError.class);
        assertEquals(400, error.getStatus());
        assertEquals(ERROR_BAD_REQUEST, error.getError());
        assertFalse(StringUtils.isNullOrEmpty(error.getMessage()));
        assertFalse(error.getErrorDetails().isEmpty());
    }

    @Test
    public void createActivity_shouldReturnInternalServerErrorMessageIfAmazonServiceExceptionIsThrown() throws Exception {
        Activity activity = ActivityUtil.anyActivity();

        AmazonDynamoDBException amazonDynamoDBException = new AmazonDynamoDBException("dynamo db error");
        amazonDynamoDBException.setStatusCode(SC_NOT_FOUND);
        amazonDynamoDBException.setErrorCode("AWSERR");
        amazonDynamoDBException.setServiceName("PutItem");
        amazonDynamoDBException.setRequestId("request1");
        amazonDynamoDBException.setErrorMessage("error message");

        doThrow(amazonDynamoDBException).when(activityDataServiceMock).addActivities(any(List.class));

        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setBody(JsonUtil.toJsonString(activities));
        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(500, responseEvent.getStatusCode());

        ApiError error = JsonUtil.fromJsonString(responseEvent.getBody(), ApiError.class);
        assertEquals(500, error.getStatus());
        assertEquals(ERROR_INTERNAL_SERVER_ERROR, error.getError());
        assertFalse(StringUtils.isNullOrEmpty(error.getMessage()));
        assertFalse(error.getErrorDetails().isEmpty());

        assertTrue(error.getMessage().contains(amazonDynamoDBException.getStatusCode()+""));
        assertTrue(error.getMessage().contains(amazonDynamoDBException.getErrorCode()));
        assertTrue(error.getMessage().contains(amazonDynamoDBException.getErrorMessage()));
        assertTrue(error.getMessage().contains(amazonDynamoDBException.getServiceName()));
        assertTrue(error.getMessage().contains(amazonDynamoDBException.getRequestId()));
    }
}

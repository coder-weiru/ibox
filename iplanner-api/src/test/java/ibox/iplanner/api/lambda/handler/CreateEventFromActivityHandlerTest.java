package ibox.iplanner.api.lambda.handler;

import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.util.StringUtils;
import ibox.iplanner.api.config.DaggerIPlannerComponent;
import ibox.iplanner.api.config.IPlannerComponent;
import ibox.iplanner.api.lambda.runtime.TestContext;
import ibox.iplanner.api.model.*;
import ibox.iplanner.api.service.ActivityDataService;
import ibox.iplanner.api.util.ActivityUtil;
import ibox.iplanner.api.util.JsonUtil;
import ibox.iplanner.api.util.MeetingUtil;
import ibox.iplanner.api.util.TaskUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.UUID;

import static ibox.iplanner.api.util.ApiErrorConstants.*;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CreateEventFromActivityHandlerTest {

    @InjectMocks
    private CreateEventFromActivityHandler handler = new CreateEventFromActivityHandler();

    @Mock
    private ActivityDataService activityDataServiceMock;

    public CreateEventFromActivityHandlerTest() {
        IPlannerComponent iPlannerComponent = DaggerIPlannerComponent.builder().build();
        iPlannerComponent.inject(handler);
    }

    @Test
    public void createEventFromActivity_shouldInvokeActivityDateServiceWithActivityId() {
        Activity activity = ActivityUtil.anyActivity();
        activity.setId(UUID.randomUUID().toString());

        when(activityDataServiceMock.getActivity(any(String.class))).thenReturn(activity);

        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setPathParameters(Collections.singletonMap("activityId", activity.getId()));
        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(200, responseEvent.getStatusCode());

        ArgumentCaptor<String> activityIdCaptor = ArgumentCaptor.forClass(String.class);

        verify(activityDataServiceMock, times(1)).getActivity(activityIdCaptor.capture());

        verifyNoMoreInteractions(activityDataServiceMock);

        String argument = activityIdCaptor.getValue();

        assertThat(argument, is(equalTo(activity.getId())));
    }

    @Test
    public void createEventFromMeeting_shouldReturnValidMeetingEvent() {
        Meeting meeting = MeetingUtil.anyMeeting();
        meeting.setId(UUID.randomUUID().toString());

        when(activityDataServiceMock.getActivity(any(String.class))).thenReturn(meeting);

        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setPathParameters(Collections.singletonMap("activityId", meeting.getId()));
        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(200, responseEvent.getStatusCode());

        Event event = JsonUtil.fromJsonString(responseEvent.getBody(), Event.class);

        assertThat(event.getSummary(), not(isEmptyString()));
        assertThat(event.getDescription(), not(isEmptyString()));
        assertThat(event.getActivity(), is(equalTo(meeting.getId())));
        assertThat(event.getStatus(), not(isEmptyString()));
        assertThat(event.getCreator(), notNullValue());
        assertThat(event.getCreated(), notNullValue());
        assertThat(event.getUpdated(), notNullValue());
        assertThat(event.getStart(), notNullValue());
        assertThat(event.getEnd(), notNullValue());
        assertThat(event.getEndTimeUnspecified(), is(Boolean.TRUE));
    }

    @Test
    public void createEventFromTask_shouldReturnValidTaskEvent() {
        Task task = TaskUtil.anyTask();
        task.setId(UUID.randomUUID().toString());

        when(activityDataServiceMock.getActivity(any(String.class))).thenReturn(task);

        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setPathParameters(Collections.singletonMap("activityId", task.getId()));
        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(200, responseEvent.getStatusCode());

        Event event = JsonUtil.fromJsonString(responseEvent.getBody(), Event.class);

        assertThat(event.getSummary(), not(isEmptyString()));
        assertThat(event.getDescription(), not(isEmptyString()));
        assertThat(event.getActivity(), is(equalTo(task.getId())));
        assertThat(event.getStatus(), not(isEmptyString()));
        assertThat(event.getCreator(), notNullValue());
        assertThat(event.getCreated(), notNullValue());
        assertThat(event.getUpdated(), notNullValue());
        assertThat(event.getStart(), notNullValue());
        assertThat(event.getEndTimeUnspecified(), is(Boolean.FALSE));
    }

    @Test
    public void createEventFromActivity_shouldReturnBadRequestMessageIfActivityIdInvalid() {
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setPathParameters(Collections.singletonMap("activityId", "abc"));
        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(400, responseEvent.getStatusCode());

        ApiError error = JsonUtil.fromJsonString(responseEvent.getBody(), ApiError.class);
        assertEquals(400, error.getStatus());
        assertEquals(ERROR_BAD_REQUEST, error.getError());
        assertFalse(StringUtils.isNullOrEmpty(error.getMessage()));
        assertFalse(error.getErrorDetails().isEmpty());
    }

    @Test
    public void createEventFromActivity_shouldReturnInternalServerErrorMessageIfAmazonServiceExceptionIsThrown() {
        Activity activity = ActivityUtil.anyActivity();
        activity.setId(UUID.randomUUID().toString());

        AmazonDynamoDBException amazonDynamoDBException = new AmazonDynamoDBException("dynamo db error");
        amazonDynamoDBException.setStatusCode(SC_NOT_FOUND);
        amazonDynamoDBException.setErrorCode("AWSERR");
        amazonDynamoDBException.setServiceName("GetItem");
        amazonDynamoDBException.setRequestId("request1");
        amazonDynamoDBException.setErrorMessage("error message");

        doThrow(amazonDynamoDBException).when(activityDataServiceMock).getActivity(any(String.class));

        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setPathParameters(Collections.singletonMap("activityId", activity.getId()));
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

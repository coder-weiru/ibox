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
public class CreateTodoFromActivityHandlerTest {

    @InjectMocks
    private CreateTodoFromActivityHandler handler = new CreateTodoFromActivityHandler();

    @Mock
    private ActivityDataService activityDataServiceMock;

    public CreateTodoFromActivityHandlerTest() {
        IPlannerComponent iPlannerComponent = DaggerIPlannerComponent.builder().build();
        iPlannerComponent.inject(handler);
    }

    @Test
    public void createTodoFromActivity_shouldInvokeActivityDateServiceWithActivityId() {
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
    public void createTodoFromMeeting_shouldReturnValidMeetingTodo() {
        Meeting meeting = MeetingUtil.anyMeeting();
        meeting.setId(UUID.randomUUID().toString());

        when(activityDataServiceMock.getActivity(any(String.class))).thenReturn(meeting);

        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setPathParameters(Collections.singletonMap("activityId", meeting.getId()));
        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(200, responseEvent.getStatusCode());

        Todo todo = JsonUtil.fromJsonString(responseEvent.getBody(), Todo.class);

        assertThat(todo.getSummary(), not(isEmptyString()));
        assertThat(todo.getDescription(), not(isEmptyString()));
        assertThat(todo.getActivity(), is(equalTo(meeting.getId())));
        assertThat(todo.getStatus(), not(isEmptyString()));
        assertThat(todo.getCreator(), notNullValue());
        assertThat(todo.getCreated(), notNullValue());
        assertThat(todo.getUpdated(), notNullValue());
        assertThat(todo.getStart(), notNullValue());
        assertThat(todo.getEnd(), notNullValue());
        assertThat(todo.getEndTimeUnspecified(), is(Boolean.TRUE));
    }

    @Test
    public void createTodoFromTask_shouldReturnValidTaskTodo() {
        Task task = TaskUtil.anyTask();
        task.setId(UUID.randomUUID().toString());

        when(activityDataServiceMock.getActivity(any(String.class))).thenReturn(task);

        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setPathParameters(Collections.singletonMap("activityId", task.getId()));
        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(200, responseEvent.getStatusCode());

        Todo todo = JsonUtil.fromJsonString(responseEvent.getBody(), Todo.class);

        assertThat(todo.getSummary(), not(isEmptyString()));
        assertThat(todo.getDescription(), not(isEmptyString()));
        assertThat(todo.getActivity(), is(equalTo(task.getId())));
        assertThat(todo.getStatus(), not(isEmptyString()));
        assertThat(todo.getCreator(), notNullValue());
        assertThat(todo.getCreated(), notNullValue());
        assertThat(todo.getUpdated(), notNullValue());
        assertThat(todo.getStart(), notNullValue());
        assertThat(todo.getEndTimeUnspecified(), is(Boolean.FALSE));
    }

    @Test
    public void createTodoFromActivity_shouldReturnBadRequestMessageIfActivityIdInvalid() {
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
    public void createTodoFromActivity_shouldReturnInternalServerErrorMessageIfAmazonServiceExceptionIsThrown() {
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

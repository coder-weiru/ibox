package ibox.iplanner.api.lambda.handler;

import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.util.StringUtils;
import ibox.iplanner.api.config.DaggerIPlannerComponent;
import ibox.iplanner.api.config.IPlannerComponent;
import ibox.iplanner.api.lambda.runtime.TestContext;
import ibox.iplanner.api.model.ApiError;
import ibox.iplanner.api.model.Todo;
import ibox.iplanner.api.model.TodoStatus;
import ibox.iplanner.api.service.TodoDataService;
import ibox.iplanner.api.util.JsonUtil;
import ibox.iplanner.api.util.TodoUtil;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.Instant;
import java.util.*;

import static ibox.iplanner.api.util.ApiErrorConstants.*;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ListTodoHandlerTest {

    @InjectMocks
    private ListTodoHandler handler = new ListTodoHandler();

    private List<Todo> todos;

    @Mock
    private TodoDataService todoDataServiceMock;

    public ListTodoHandlerTest() {
        IPlannerComponent iPlannerComponent = DaggerIPlannerComponent.builder().build();
        iPlannerComponent.inject(handler);
    }

    @Before
    public void setUp() {
        this.todos = Arrays.asList(new Todo[]{
                TodoUtil.anyTodo(),
                TodoUtil.anyTodo(),
                TodoUtil.anyTodo()
        });
    }

    @Test
    public void getMyTodoListByFilter_shouldInvokeTodoDateServiceGivenCorrectParams() throws Exception {
        when(todoDataServiceMock.getMyTodoListByFilter(any(String.class), any(Set.class), any(String.class), any(Integer.class))).thenReturn(todos);

        String creatorId = UUID.randomUUID().toString();
        String selectedActivityIds = String.format("%s, %s", todos.get(0).getActivityId(), todos.get(1).getActivityId());
        String status = TodoStatus.OPEN.name();
        String limit = "10";

        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setPathParameters(Collections.singletonMap("creatorId", creatorId));
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("activities", selectedActivityIds);
        requestParams.put("status", status);
        requestParams.put("limit", limit);
        requestEvent.setQueryStringParameters(requestParams);

        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(200, responseEvent.getStatusCode());

        ArgumentCaptor<String> todoIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Set> activitiesCaptor = ArgumentCaptor.forClass(Set.class);
        ArgumentCaptor<String> statusCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> limitCaptor = ArgumentCaptor.forClass(Integer.class);

        verify(todoDataServiceMock, times(1)).getMyTodoListByFilter(todoIdCaptor.capture(), activitiesCaptor.capture(), statusCaptor.capture(), limitCaptor.capture());

        verifyNoMoreInteractions(todoDataServiceMock);

        assertThat(todoIdCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(creatorId)));
        assertThat(Arrays.asList(activitiesCaptor.getValue().toArray()), CoreMatchers.hasItems(todos.get(0).getActivityId(), todos.get(1).getActivityId()));
        assertThat(statusCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(status)));
        assertThat(limitCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(Integer.valueOf(limit))));
    }

    @Test
    public void getMyTodoListByFilter_shouldInvokeTodoDateServiceWithSpecifiedEvenStatus() throws Exception {
        when(todoDataServiceMock.getMyTodoListByFilter(any(String.class), any(Set.class), any(String.class), any(Integer.class))).thenReturn(todos);

        String creatorId = UUID.randomUUID().toString();
        String selectedActivityIds = String.format("%s, %s", todos.get(0).getActivityId(), todos.get(1).getActivityId());
        String status = TodoStatus.FINISHED.name();

        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setPathParameters(Collections.singletonMap("creatorId", creatorId));
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("activities", selectedActivityIds);
        requestParams.put("status", status);
        requestEvent.setQueryStringParameters(requestParams);

        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(200, responseEvent.getStatusCode());

        ArgumentCaptor<String> todoIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Set> activitiesCaptor = ArgumentCaptor.forClass(Set.class);
        ArgumentCaptor<String> statusCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> limitCaptor = ArgumentCaptor.forClass(Integer.class);

        verify(todoDataServiceMock, times(1)).getMyTodoListByFilter(todoIdCaptor.capture(), activitiesCaptor.capture(), statusCaptor.capture(), limitCaptor.capture());

        verifyNoMoreInteractions(todoDataServiceMock);

        assertThat(todoIdCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(creatorId)));
        assertThat(Arrays.asList(activitiesCaptor.getValue().toArray()), CoreMatchers.hasItems(todos.get(0).getActivityId(), todos.get(1).getActivityId()));
        assertThat(statusCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(TodoStatus.FINISHED.name())));
        assertThat(limitCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(Integer.valueOf(100))));

    }

    @Test
    public void getMyTodoListByFilter_shouldReturnBadRequestMessageIfCreatorIdInvalid() throws Exception {
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setPathParameters(Collections.singletonMap("creatorId", "abc"));
        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(400, responseEvent.getStatusCode());

        ApiError error = JsonUtil.fromJsonString(responseEvent.getBody(), ApiError.class);
        assertEquals(400, error.getStatus());
        assertEquals(ERROR_BAD_REQUEST, error.getError());
        assertFalse(StringUtils.isNullOrEmpty(error.getMessage()));
        assertFalse(error.getErrorDetails().isEmpty());
    }

    @Test
    public void getMyTodoListByFilter_shouldInvokeTodoDateServiceEvenActivitiesNotSpecified() throws Exception {
        when(todoDataServiceMock.getMyTodoListByFilter(any(String.class), any(String.class), any(Integer.class))).thenReturn(todos);

        String creatorId = UUID.randomUUID().toString();
        String limit = "10";

        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setPathParameters(Collections.singletonMap("creatorId", creatorId));
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("limit", limit);
        requestEvent.setQueryStringParameters(requestParams);

        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(200, responseEvent.getStatusCode());

        ArgumentCaptor<String> todoIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> statusCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> limitCaptor = ArgumentCaptor.forClass(Integer.class);

        verify(todoDataServiceMock, times(1)).getMyTodoListByFilter(todoIdCaptor.capture(), statusCaptor.capture(), limitCaptor.capture());

        verifyNoMoreInteractions(todoDataServiceMock);

        assertThat(todoIdCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(creatorId)));
        assertThat(statusCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(TodoStatus.OPEN.name())));
        assertThat(limitCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(Integer.valueOf(limit))));
    }

    @Test
    public void getMyTodoListByFilter_shouldInvokeTodoDateServiceEvenLimitIsNotSpecified() throws Exception {
        when(todoDataServiceMock.getMyTodoListByFilter(any(String.class), any(String.class), any(Integer.class))).thenReturn(todos);

        Instant now = Instant.now();
        String creatorId = UUID.randomUUID().toString();
        String selectedActivityIds = String.format("%s, %s", todos.get(0).getActivityId(), todos.get(1).getActivityId());

        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setPathParameters(Collections.singletonMap("creatorId", creatorId));
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("activities", selectedActivityIds);
        requestEvent.setQueryStringParameters(requestParams);

        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(200, responseEvent.getStatusCode());

        ArgumentCaptor<String> todoIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Set> activitiesCaptor = ArgumentCaptor.forClass(Set.class);
        ArgumentCaptor<String> statusCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> limitCaptor = ArgumentCaptor.forClass(Integer.class);

        verify(todoDataServiceMock, times(1)).getMyTodoListByFilter(todoIdCaptor.capture(), activitiesCaptor.capture(), statusCaptor.capture(), limitCaptor.capture());

        verifyNoMoreInteractions(todoDataServiceMock);

        assertThat(todoIdCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(creatorId)));
        assertThat(Arrays.asList(activitiesCaptor.getValue().toArray()), CoreMatchers.hasItems(todos.get(0).getActivityId(), todos.get(1).getActivityId()));
        assertThat(statusCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(TodoStatus.OPEN.name())));
        assertThat(limitCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(Integer.valueOf(100))));

    }

    @Test
    public void getMyTodoListByFilter_shouldReturnInternalServerErrorMessageIfAmazonServiceExceptionIsThrown() throws Exception {
        Todo todo = TodoUtil.anyTodo();
        todo.setId(UUID.randomUUID().toString());
        todo.getCreator().setId(UUID.randomUUID().toString());

        AmazonDynamoDBException amazonDynamoDBException = new AmazonDynamoDBException("dynamo db error");
        amazonDynamoDBException.setStatusCode(SC_NOT_FOUND);
        amazonDynamoDBException.setErrorCode("AWSERR");
        amazonDynamoDBException.setServiceName("QueryItem");
        amazonDynamoDBException.setRequestId("request1");
        amazonDynamoDBException.setErrorMessage("error message");

        doThrow(amazonDynamoDBException).when(todoDataServiceMock).getMyTodoListByFilter(any(String.class), any(String.class), any(Integer.class));

        String creatorId = UUID.randomUUID().toString();

        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setPathParameters(Collections.singletonMap("creatorId", creatorId));
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

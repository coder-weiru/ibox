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
import ibox.iplanner.api.service.TodoDataService;
import ibox.iplanner.api.util.JsonUtil;
import ibox.iplanner.api.util.TodoUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static ibox.iplanner.api.util.ApiErrorConstants.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UpdateTodoHandlerTest {

    @InjectMocks
    private UpdateTodoHandler handler = new UpdateTodoHandler();

    private Todo todo;

    private String newSummary = "new summary";
    private String newDescription = "new description";
    private String newActivity = "new activity";

    @Mock
    private TodoDataService todoDataServiceMock;

    public UpdateTodoHandlerTest() {
        IPlannerComponent iPlannerComponent = DaggerIPlannerComponent.builder().build();
        iPlannerComponent.inject(handler);
    }

    @Before
    public void setUp() {
        this.todo = TodoUtil.anyTodo();
    }

    @Test
    public void updateTodo_shouldInvokeTodoDateServiceWithUpdatable() throws Exception {
        when(todoDataServiceMock.updateTodo(any(Todo.class))).thenReturn(todo);

        todo.setSummary(newSummary);
        todo.setDescription(newDescription);
        todo.setActivityType(newActivity);

        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setPathParameters(Collections.singletonMap("todoId", todo.getId()));
        requestEvent.setBody(JsonUtil.toJsonString(todo));
        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(200, responseEvent.getStatusCode());

        ArgumentCaptor<Todo> requestCaptor = ArgumentCaptor.forClass(Todo.class);

        verify(todoDataServiceMock, times(1)).updateTodo(requestCaptor.capture());

        verifyNoMoreInteractions(todoDataServiceMock);

        Todo argument = requestCaptor.getValue();

        assertThat(argument.getId(), is(equalTo(todo.getId())));
        assertThat(argument.getSummary(), is(equalTo(newSummary)));
        assertThat(argument.getDescription(), is(equalTo(newDescription)));
        assertThat(argument.getActivityType(), is(equalTo(todo.getActivityType())));
    }

    @Test
    public void updateTodo_shouldReturnBadRequestMessageIfMissingKey() throws Exception {
        todo.setId(null);

        when(todoDataServiceMock.updateTodo(any(Todo.class))).thenReturn(todo);

        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setPathParameters(Collections.singletonMap("todoId", todo.getId()));
        requestEvent.setBody(JsonUtil.toJsonString(todo));
        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(400, responseEvent.getStatusCode());

        ApiError error = JsonUtil.fromJsonString(responseEvent.getBody(), ApiError.class);
        assertEquals(400, error.getStatus());
        assertEquals(ERROR_BAD_REQUEST, error.getError());
        assertFalse(StringUtils.isNullOrEmpty(error.getMessage()));
        assertTrue(error.getErrorDetails().isEmpty());
    }

    @Test
    public void updateTodo_shouldReturnInternalServerErrorMessageIfAmazonDynamoDBExceptionIsThrown() throws Exception {

        AmazonDynamoDBException amazonDynamoDBException = new AmazonDynamoDBException("dynamo db error");
        amazonDynamoDBException.setStatusCode(SC_NOT_FOUND);
        amazonDynamoDBException.setErrorCode("AWSERR");
        amazonDynamoDBException.setServiceName("UpdateItem");
        amazonDynamoDBException.setRequestId("request1");
        amazonDynamoDBException.setErrorMessage("error message");

        doThrow(amazonDynamoDBException).when(todoDataServiceMock).updateTodo(any(Todo.class));

        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setPathParameters(Collections.singletonMap("todoId", todo.getId()));
        requestEvent.setBody(JsonUtil.toJsonString(todo));
        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(500, responseEvent.getStatusCode());

        ApiError error = JsonUtil.fromJsonString(responseEvent.getBody(), ApiError.class);
        assertEquals(500, error.getStatus());
        assertEquals(ERROR_INTERNAL_SERVER_ERROR, error.getError());
        assertFalse(StringUtils.isNullOrEmpty(error.getMessage()));
        assertFalse(error.getErrorDetails().isEmpty());

        assertTrue(error.getMessage().contains(amazonDynamoDBException.getStatusCode() + ""));
        assertTrue(error.getMessage().contains(amazonDynamoDBException.getErrorCode()));
        assertTrue(error.getMessage().contains(amazonDynamoDBException.getErrorMessage()));
        assertTrue(error.getMessage().contains(amazonDynamoDBException.getServiceName()));
        assertTrue(error.getMessage().contains(amazonDynamoDBException.getRequestId()));
    }
}
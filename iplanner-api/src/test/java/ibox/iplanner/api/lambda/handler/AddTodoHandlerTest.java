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
import ibox.iplanner.api.model.User;
import ibox.iplanner.api.service.TodoDataService;
import ibox.iplanner.api.util.TodoUtil;
import ibox.iplanner.api.util.JsonUtil;
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
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AddTodoHandlerTest {

    @InjectMocks
    private AddTodoHandler handler = new AddTodoHandler();

    private List<Todo> todos;

    @Mock
    private TodoDataService todoDataServiceMock;

    public AddTodoHandlerTest() {
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
    public void createTodo_shouldInvokeTodoDateServiceWithTodoList() throws Exception {

        doNothing().when(todoDataServiceMock).addTodos(any(List.class));

        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setBody(JsonUtil.toJsonString(todos));
        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(200, responseEvent.getStatusCode());

        ArgumentCaptor<List> requestCaptor = ArgumentCaptor.forClass(List.class);

        verify(todoDataServiceMock, times(1)).addTodos(requestCaptor.capture());

        verifyNoMoreInteractions(todoDataServiceMock);

        List<Todo> argument = requestCaptor.getValue();

        assertThat(argument.size(), is(equalTo(todos.size())));
        assertThat(argument.get(0).getId(), not(equalTo(todos.get(0).getId())));
        assertThat(argument.get(0).getSummary(), is(equalTo(todos.get(0).getSummary())));
        assertThat(argument.get(0).getDescription(), is(equalTo(todos.get(0).getDescription())));
        assertThat(argument.get(0).getActivity(), is(equalTo(todos.get(0).getActivity())));
        assertThat(argument.get(0).getStatus(), is(equalTo(todos.get(0).getStatus())));
        assertThat(argument.get(0).getLocation(), is(equalTo(todos.get(0).getLocation())));
        assertThat(argument.get(0).getCreator().getId(), is(equalTo(todos.get(0).getCreator().getId())));
        assertThat(argument.get(0).getCreator().getEmail(), is(equalTo(todos.get(0).getCreator().getEmail())));
        assertThat(argument.get(0).getCreator().getDisplayName(), is(equalTo(todos.get(0).getCreator().getDisplayName())));
        assertThat(argument.get(0).getCreator().getSelf(), is(equalTo(todos.get(0).getCreator().getSelf())));
        assertThat(argument.get(0).getCreated(), is(equalTo(todos.get(0).getCreated())));
        assertThat(argument.get(0).getUpdated(), is(equalTo(todos.get(0).getUpdated())));
        assertThat(argument.get(0).getStart(), is(equalTo(todos.get(0).getStart())));
        assertThat(argument.get(0).getEnd(), is(equalTo(todos.get(0).getEnd())));
        assertThat(argument.get(0).getEndTimeUnspecified(), is(equalTo(todos.get(0).getEndTimeUnspecified())));
        assertThat(argument.get(0).getRecurrence(), is(hasItems(todos.get(0).getRecurrence().toArray(new String[todos.get(0).getRecurrence().size()]))));

        assertThat(argument.get(1).getId(), not(equalTo(todos.get(1).getId())));
        assertThat(argument.get(1).getSummary(), is(equalTo(todos.get(1).getSummary())));
        assertThat(argument.get(1).getDescription(), is(equalTo(todos.get(1).getDescription())));
        assertThat(argument.get(1).getActivity(), is(equalTo(todos.get(1).getActivity())));
        assertThat(argument.get(1).getStatus(), is(equalTo(todos.get(1).getStatus())));
        assertThat(argument.get(1).getLocation(), is(equalTo(todos.get(1).getLocation())));
        assertThat(argument.get(1).getCreator().getId(), is(equalTo(todos.get(1).getCreator().getId())));
        assertThat(argument.get(1).getCreator().getEmail(), is(equalTo(todos.get(1).getCreator().getEmail())));
        assertThat(argument.get(1).getCreator().getDisplayName(), is(equalTo(todos.get(1).getCreator().getDisplayName())));
        assertThat(argument.get(1).getCreator().getSelf(), is(equalTo(todos.get(1).getCreator().getSelf())));
        assertThat(argument.get(1).getCreated(), is(equalTo(todos.get(1).getCreated())));
        assertThat(argument.get(1).getUpdated(), is(equalTo(todos.get(1).getUpdated())));
        assertThat(argument.get(1).getStart(), is(equalTo(todos.get(1).getStart())));
        assertThat(argument.get(1).getEnd(), is(equalTo(todos.get(1).getEnd())));
        assertThat(argument.get(1).getEndTimeUnspecified(), is(equalTo(todos.get(1).getEndTimeUnspecified())));
        assertThat(argument.get(1).getRecurrence(), is(hasItems(todos.get(1).getRecurrence().toArray(new String[todos.get(1).getRecurrence().size()]))));

        assertThat(argument.get(2).getId(), not(equalTo(todos.get(2).getId())));
        assertThat(argument.get(2).getSummary(), is(equalTo(todos.get(2).getSummary())));
        assertThat(argument.get(2).getDescription(), is(equalTo(todos.get(2).getDescription())));
        assertThat(argument.get(2).getActivity(), is(equalTo(todos.get(2).getActivity())));
        assertThat(argument.get(2).getStatus(), is(equalTo(todos.get(2).getStatus())));
        assertThat(argument.get(2).getLocation(), is(equalTo(todos.get(2).getLocation())));
        assertThat(argument.get(2).getCreator().getId(), is(equalTo(todos.get(2).getCreator().getId())));
        assertThat(argument.get(2).getCreator().getEmail(), is(equalTo(todos.get(2).getCreator().getEmail())));
        assertThat(argument.get(2).getCreator().getDisplayName(), is(equalTo(todos.get(2).getCreator().getDisplayName())));
        assertThat(argument.get(2).getCreator().getSelf(), is(equalTo(todos.get(2).getCreator().getSelf())));
        assertThat(argument.get(2).getCreated(), is(equalTo(todos.get(2).getCreated())));
        assertThat(argument.get(2).getUpdated(), is(equalTo(todos.get(2).getUpdated())));
        assertThat(argument.get(2).getStart(), is(equalTo(todos.get(2).getStart())));
        assertThat(argument.get(2).getEnd(), is(equalTo(todos.get(2).getEnd())));
        assertThat(argument.get(2).getEndTimeUnspecified(), is(equalTo(todos.get(2).getEndTimeUnspecified())));
        assertThat(argument.get(2).getRecurrence(), is(hasItems(todos.get(2).getRecurrence().toArray(new String[todos.get(2).getRecurrence().size()]))));
    }

    @Test
    public void createTodo_shouldReturnBadRequestMessageIfMissingSummary() throws Exception {
        Todo todo = TodoUtil.anyTodo();
        todo.setSummary(null);

        verifyBadRequestMessage(Arrays.asList(new Todo[] {
                todo
        }));
    }

    @Test
    public void createTodo_shouldReturnBadRequestMessageIfMissingCreator() throws Exception {
        Todo todo = TodoUtil.anyTodo();
        todo.setCreator(null);

        verifyBadRequestMessage(Arrays.asList(new Todo[] {
                todo
        }));
    }

    @Test
    public void createTodo_shouldReturnBadRequestMessageIfMissingActivity() throws Exception {
        Todo todo = TodoUtil.anyTodo();
        todo.setActivity(null);

        verifyBadRequestMessage(Arrays.asList(new Todo[] {
                todo
        }));
    }

    @Test
    public void createTodo_shouldReturnBadRequestMessageIfMissingStartTime() throws Exception {
        Todo todo = TodoUtil.anyTodo();
        todo.setStart(null);

        verifyBadRequestMessage(Arrays.asList(new Todo[] {
                todo
        }));
    }

    @Test
    public void createTodo_shouldReturnBadRequestMessageIfCreatorInvalid() throws Exception {
        Todo todo = TodoUtil.anyTodo();

        User creator = new User();

        todo.setCreator(creator);

        verifyBadRequestMessage(Arrays.asList(new Todo[] {
                todo
        }));
    }


    private void verifyBadRequestMessage(List<Todo> todos) throws Exception {

        doNothing().when(todoDataServiceMock).addTodos(any(List.class));

        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setBody(JsonUtil.toJsonString(todos));
        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(400, responseEvent.getStatusCode());

        ApiError error = JsonUtil.fromJsonString(responseEvent.getBody(), ApiError.class);
        assertEquals(400, error.getStatus());
        assertEquals(ERROR_BAD_REQUEST, error.getError());
        assertFalse(StringUtils.isNullOrEmpty(error.getMessage()));
        assertFalse(error.getErrorDetails().isEmpty());
    }

    @Test
    public void createTodo_shouldReturnInternalServerErrorMessageIfAmazonServiceExceptionIsThrown() throws Exception {
        Todo todo = TodoUtil.anyTodo();

        AmazonDynamoDBException amazonDynamoDBException = new AmazonDynamoDBException("dynamo db error");
        amazonDynamoDBException.setStatusCode(SC_NOT_FOUND);
        amazonDynamoDBException.setErrorCode("AWSERR");
        amazonDynamoDBException.setServiceName("PutItem");
        amazonDynamoDBException.setRequestId("request1");
        amazonDynamoDBException.setErrorMessage("error message");

        doThrow(amazonDynamoDBException).when(todoDataServiceMock).addTodos(any(List.class));

        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setBody(JsonUtil.toJsonString(todos));
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

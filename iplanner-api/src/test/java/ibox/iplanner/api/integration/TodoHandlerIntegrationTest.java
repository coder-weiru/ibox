package ibox.iplanner.api.integration;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import ibox.iplanner.api.lambda.runtime.TestContext;
import ibox.iplanner.api.model.Todo;
import ibox.iplanner.api.model.TodoStatus;
import ibox.iplanner.api.model.User;
import ibox.iplanner.api.service.LocalDynamoDBIntegrationTestSupport;
import ibox.iplanner.api.util.JsonUtil;
import ibox.iplanner.api.util.TodoUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Instant;
import java.util.*;

import static ibox.iplanner.api.util.TestHelper.verifyTodoAreEqual;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class TodoHandlerIntegrationTest extends LocalDynamoDBIntegrationTestSupport {

    private AddTodoHandlerTestWrapper addTodoHandler = new AddTodoHandlerTestWrapper();
    private GetTodoHandlerTestWrapper getTodoHandler = new GetTodoHandlerTestWrapper();
    private ListTodoHandlerTestWrapper listTodoHandler = new ListTodoHandlerTestWrapper();
    private UpdateTodoHandlerTestWrapper updateTodoHandler = new UpdateTodoHandlerTestWrapper();
    private DeleteTodoHandlerTestWrapper deleteTodoHandler = new DeleteTodoHandlerTestWrapper();

    private TestContext testContext = TestContext.builder().build();

    @BeforeClass
    public static void setup() {
        dynamoDBSetup.createTodoTable(10L, 5L);
    }

    @Test
    public void givenValidTodo_addTodo_shouldCreateRecord() {

        Todo todo = TodoUtil.anyTodo();

        Todo added = addTodo(todo);

        Todo getTodo = getTodo(added.getId());

        verifyTodoAreEqual(added, getTodo);
    }

    @Test
    public void givenValidTodoList_addTodoList_shouldCreateRecords() {

        List<Todo> todos = TodoUtil.anyTodoList();

        List<Todo> added = addTodos(todos);

        added.stream().forEach(activity -> {
            Todo getTodo = getTodo(activity.getId());

            verifyTodoAreEqual(activity, getTodo);

        });
    }

    @Test
    public void givenValidTodo_updateTodo_shouldUpdateRecord() {
        Todo added = addTodo(TodoUtil.anyMeetingTodo());

        String newSummary = "new summary";
        String newDescription = "new description";
        String newLocation = "new location";
        String newActivity = "new activity";
        Set<String> newRecurrence = new HashSet<>();
        newRecurrence.add("abc");

        added.setSummary(newSummary);
        added.setDescription(newDescription);
        added.getLocationInfo().setLocation(newLocation);
        added.setActivityType(newActivity);
        added.getEventInfo().setRecurrence(newRecurrence);

        updateTodo(added);

        Todo updated = getTodo(added.getId());

        assertThat(updated.getSummary(), is(equalTo(newSummary)));
        assertThat(updated.getDescription(), is(equalTo(newDescription)));
        assertThat(updated.getActivityType(), not(equalTo(newActivity)));
        assertThat(updated.getLocationInfo().getLocation(), is(equalTo(newLocation)));
        assertThat(updated.getEventInfo().getRecurrence(), hasItem("abc"));
        assertThat(updated.getCreator().getId(), is(equalTo(added.getCreator().getId())));
        assertThat(updated.getCreator().getDisplayName(), is(equalTo(added.getCreator().getDisplayName())));
        assertThat(updated.getCreator().getEmail(), is(equalTo(added.getCreator().getEmail())));
        assertThat(updated.getCreator().getSelf(), is(equalTo(added.getCreator().getSelf())));
        assertThat(updated.getCreated(), is(equalTo(added.getCreated())));
        assertThat(updated.getUpdated(), not(equalTo(added.getUpdated())));
        assertThat(updated.getEventInfo().getStart(), is(equalTo(added.getEventInfo().getStart())));
        assertThat(updated.getEventInfo().getEnd(), is(equalTo(added.getEventInfo().getEnd())));
        assertThat(updated.getStatus(), is(equalTo(added.getStatus())));
    }

    @Test
    public void givenValidId_deleteTodo_shouldUpdateTodoStatus() {
        Todo added = addTodo(TodoUtil.anyTodo());

        deleteTodo(added.getId());

        Todo deleted = getTodo(added.getId());

        assertThat(deleted.getStatus(), is(equalTo(TodoStatus.CLOSED)));
    }

    @Test
    public void givenValidTodo_updateTodo_shouldNotUpdateKeyField() {

        Todo added = addTodo(TodoUtil.anyTodo());
        added.setId("123456789");
        updateTodoResultInBadRequestError(added);
    }

    @Test
    public void givenTodoListWithCreators_getMyTodoListWithSelectedActivities_shouldReturnOnlyCreatorTodoListWithSelectedActivities() {
        User creator1 = TodoUtil.anyTodoCreator();
        User creator2 = TodoUtil.anyTodoCreator();

        Instant now = Instant.now();

        Todo todo1 = TodoUtil.anyTodo();
        todo1.setCreator(creator1);
        todo1.setActivityId("activity_id_1");
        todo1.setStatus(TodoStatus.OPEN);

        Todo todo2 = TodoUtil.anyTodo();
        todo2.setCreator(creator1);
        todo2.setActivityId("activity_id_2");
        todo2.setStatus(TodoStatus.OPEN);

        Todo todo3 = TodoUtil.anyTodo();
        todo3.setCreator(creator1);
        todo3.setActivityId("activity_id_3");
        todo3.setStatus(TodoStatus.OPEN);

        Todo todo4 = TodoUtil.anyTodo();
        todo4.setCreator(creator1);
        todo4.setActivityId("activity_id_4");
        todo4.setStatus(TodoStatus.OPEN);

        Todo todo5 = TodoUtil.anyTodo();
        todo5.setCreator(creator1);
        todo5.setActivityId("activity_id_5");
        todo5.setStatus(TodoStatus.FINISHED);

        Todo todo6 = TodoUtil.anyTodo();
        todo6.setCreator(creator1);
        todo6.setActivityId("activity_id_6");
        todo6.setStatus(TodoStatus.OPEN);

        List<Todo> todos = Arrays.asList( new Todo[] {todo1, todo2, todo3, todo4, todo5, todo6});

        List<Todo> added = addTodos(todos);

        String selectedActivities = "activity_id_1, activity_id_2, activity_id_3, activity_id_5";

        List<Todo> list = listTodos(creator1.getId(), selectedActivities, null, null);

        assertThat(list.size(), is(equalTo(3)));

        list.stream().forEach(todo -> {
            Todo originTodo = added.stream().filter(t->t.getId().equals(todo.getId())).findFirst().orElse(null);

            verifyTodoAreEqual(todo, originTodo);
        });
    }

    private Todo addTodo(Todo todo) {
        APIGatewayProxyRequestEvent addRequestEvent = new APIGatewayProxyRequestEvent();
        addRequestEvent.setBody(JsonUtil.toJsonString(Arrays.asList( new Todo[] {todo})));
        APIGatewayProxyResponseEvent addResponseEvent = addTodoHandler.handleRequest(addRequestEvent, testContext);

        assertThat(addResponseEvent.getStatusCode(), is(equalTo(200)));

        List<Todo> added = (List<Todo>) JsonUtil.fromJsonString(addResponseEvent.getBody(), List.class, Todo.class);

        return added.get(0);
    }

    private List<Todo> addTodos(List<Todo> activities) {
        APIGatewayProxyRequestEvent addRequestEvent = new APIGatewayProxyRequestEvent();
        addRequestEvent.setBody(JsonUtil.toJsonString(activities));
        APIGatewayProxyResponseEvent addResponseEvent = addTodoHandler.handleRequest(addRequestEvent, testContext);

        assertThat(addResponseEvent.getStatusCode(), is(equalTo(200)));

        List<Todo> added = (List<Todo>) JsonUtil.fromJsonString(addResponseEvent.getBody(), List.class, Todo.class);

        return added;
    }

    private Todo getTodo(String todoId) {
        APIGatewayProxyRequestEvent getRequestEvent = new APIGatewayProxyRequestEvent();
        getRequestEvent.setPathParameters(Collections.singletonMap("todoId", todoId));
        APIGatewayProxyResponseEvent getResponseEvent = getTodoHandler.handleRequest(getRequestEvent, testContext);

        assertThat(getResponseEvent.getStatusCode(), is(equalTo(200)));

        Todo added = JsonUtil.fromJsonString(getResponseEvent.getBody(), Todo.class);

        return added;
    }

    private void deleteTodo(String todoId) {
        APIGatewayProxyRequestEvent deleteRequestEvent = new APIGatewayProxyRequestEvent();
        deleteRequestEvent.setPathParameters(Collections.singletonMap("todoId", todoId));
        APIGatewayProxyResponseEvent deleteResponseEvent = deleteTodoHandler.handleRequest(deleteRequestEvent, testContext);

        assertThat(deleteResponseEvent.getStatusCode(), is(equalTo(200)));
    }

    private void updateTodo(Todo updatable) {
        APIGatewayProxyRequestEvent updateRequestEvent = new APIGatewayProxyRequestEvent();
        updateRequestEvent.setPathParameters(Collections.singletonMap("todoId", updatable.getId()));
        updateRequestEvent.setBody(JsonUtil.toJsonString(updatable));
        APIGatewayProxyResponseEvent updateResponseEvent = updateTodoHandler.handleRequest(updateRequestEvent, testContext);

        assertThat(updateResponseEvent.getStatusCode(), is(equalTo(200)));
    }

    private void updateTodoResultInBadRequestError(Todo updatable) {
        APIGatewayProxyRequestEvent updateRequestEvent = new APIGatewayProxyRequestEvent();
        updateRequestEvent.setPathParameters(Collections.singletonMap("todoId", updatable.getId()));
        updateRequestEvent.setBody(JsonUtil.toJsonString(updatable));
        APIGatewayProxyResponseEvent updateResponseEvent = updateTodoHandler.handleRequest(updateRequestEvent, testContext);

        assertThat(updateResponseEvent.getStatusCode(), is(equalTo(400)));
    }

    private List<Todo> listTodos(String creatorId, String activityIds, TodoStatus status, Integer limit) {
        APIGatewayProxyRequestEvent listRequestEvent = new APIGatewayProxyRequestEvent();
        listRequestEvent.setPathParameters(Collections.singletonMap("creatorId", creatorId));
        Map<String, String> requestParams = new HashMap<>();
        if (Optional.ofNullable(activityIds).isPresent()) {
            requestParams.put("activities", activityIds);
        }
        if (Optional.ofNullable(status).isPresent()) {
            requestParams.put("status", status.name());
        }
        if (Optional.ofNullable(limit).isPresent()) {
            requestParams.put("limit", limit.toString());
        }
        listRequestEvent.setQueryStringParameters(requestParams);

        APIGatewayProxyResponseEvent listResponseEvent = listTodoHandler.handleRequest(listRequestEvent, testContext);

        assertThat(listResponseEvent.getStatusCode(), is(equalTo(200)));

        List<Todo> listTodos = (List<Todo>)JsonUtil.fromJsonString(listResponseEvent.getBody(), List.class, Todo.class);

        return listTodos;
    }

}

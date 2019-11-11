package ibox.iplanner.api.integration;

import ibox.iplanner.api.service.LocalDynamoDBIntegrationTestSupport;

public class TodoHandlerIntegrationTest extends LocalDynamoDBIntegrationTestSupport {
/*
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

        verifyTodosAreEqual(todo, getTodo);
    }

    @Test
    public void givenValidTodos_addTodos_shouldCreateRecords() {

        List<Todo> todos = TodoUtil.anyTodoList();

        List<Todo> added = addTodos(todos);

        added.stream().forEach(activity -> {
            Todo getTodo = getTodo(activity.getId());

            verifyTodosAreEqual(activity, getTodo);

        });
    }

    @Test
    public void givenValidUpdatable_updateTodo_shouldUpdateRecord() throws InterruptedException {
        Todo added = addTodo(TodoUtil.anyTodo());

        String newSummary = "new summary";
        String newDescription = "new description";
        String newLocation = "new location";
        String newActivity = "new activity";
        Set<String> newRecurrence = new HashSet<>();
        newRecurrence.add("abc");
        Set<UpdatableAttribute> updatableAttributeSet = new HashSet<>();
        updatableAttributeSet.add( UpdatableAttribute.builder()
                .attributeName(FIELD_NAME_SUMMARY)
                .action(UpdateAction.UPDATE)
                .value(newSummary)
                .build());
        updatableAttributeSet.add( UpdatableAttribute.builder()
                .attributeName(FIELD_NAME_DESCRIPTION)
                .action(UpdateAction.UPDATE)
                .value(newDescription)
                .build());
        /*
        updatableAttributeSet.add( UpdatableAttribute.builder()
                .attributeName(FIELD_NAME_ACTIVITY)
                .action(UpdateAction.UPDATE)
                .value(newActivity)
                .build());
        updatableAttributeSet.add( UpdatableAttribute.builder()
                .attributeName(FIELD_NAME_TODO_LOCATION)
                .action(UpdateAction.UPDATE)
                .value(newLocation)
                .build());
        updatableAttributeSet.add( UpdatableAttribute.builder()
                .attributeName(FIELD_NAME_TODO_RECURRENCE)
                .action(UpdateAction.UPDATE)
                .value(newRecurrence)
                .build());

        Updatable updatable = Updatable.builder()
                .objectType("todo")
                .primaryKey(new UpdatableKey()
                        .addComponent(FIELD_NAME_ID, added.getId()))
                .updatableAttributes(updatableAttributeSet)
                .build();

        updateTodo(added.getId(), updatable);

        Todo updated = getTodo(added.getId());

        assertThat(updated.getSummary(), is(equalTo(newSummary)));
        assertThat(updated.getDescription(), is(equalTo(newDescription)));
        assertThat(updated.getActivity(), is(equalTo(newActivity)));
        assertThat(updated.getLocation(), is(equalTo(newLocation)));
        assertThat(updated.getRecurrence(), hasItem("abc"));

        assertThat(updated.getCreator().getId(), is(equalTo(added.getCreator().getId())));
        assertThat(updated.getCreator().getDisplayName(), is(equalTo(added.getCreator().getDisplayName())));
        assertThat(updated.getCreator().getEmail(), is(equalTo(added.getCreator().getEmail())));
        assertThat(updated.getCreator().getSelf(), is(equalTo(added.getCreator().getSelf())));
        assertThat(updated.getCreated(), is(equalTo(added.getCreated())));
        assertThat(updated.getUpdated(), is(equalTo(added.getUpdated())));
        assertThat(updated.getStart(), is(equalTo(added.getStart())));
        assertThat(updated.getEnd(), is(equalTo(added.getEnd())));
        assertThat(updated.getStatus(), is(equalTo(added.getStatus())));
        assertThat(updated.getEndTimeUnspecified(), is(equalTo(added.getEndTimeUnspecified())));

    }

    @Test
    public void givenValidId_deleteTodo_shouldUpdateTodoStatus() {
        Todo added = addTodo(TodoUtil.anyTodo());

        deleteTodo(added.getId());

        Todo deleted = getTodo(added.getId());

        assertThat(deleted.getStatus(), is(equalTo(TodoStatus.CLOSED.name())));
    }

    @Test
    public void givenValidUpdatable_updateTodo_shouldNotUpdateKeyField() {

        Todo added = addTodo(TodoUtil.anyTodo());

        Set<UpdatableAttribute> updatableAttributeSet = new HashSet<>();
        updatableAttributeSet.add( UpdatableAttribute.builder()
                .attributeName(TodoDefinition.FIELD_NAME_ID)
                .action(UpdateAction.UPDATE)
                .value("1234567890")
                .build());
        Updatable updatable = Updatable.builder()
                .objectType("todo")
                .primaryKey(new UpdatableKey()
                        .addComponent(TodoDefinition.FIELD_NAME_ID, added.getId()))
                .updatableAttributes(updatableAttributeSet)
                .build();

        updateTodoResultInInternalServerError(added.getId(), updatable);
    }

    @Test
    public void givenTodosWithCreators_getMyTodosWithinTime_shouldReturnOnlyCreatorTodosWithinTime() {
        User creator1 = TodoUtil.anyTodoCreator();
        User creator2 = TodoUtil.anyTodoCreator();

        Instant now = Instant.now();

        Todo todo1 = TodoUtil.anyTodo();
        todo1.setCreator(creator1);
        todo1.setStart(now);
        todo1.setStatus(TodoStatus.OPEN.name());

        Todo todo2 = TodoUtil.anyTodo();
        todo2.setCreator(creator1);
        todo2.setStart(now.plus(10, MINUTES));
        todo2.setStatus(TodoStatus.OPEN.name());

        Todo todo3 = TodoUtil.anyTodo();
        todo3.setCreator(creator1);
        todo3.setStart(now.plus(15, MINUTES));
        todo3.setStatus(TodoStatus.OPEN.name());

        Todo todo4 = TodoUtil.anyTodo();
        todo4.setCreator(creator2);
        todo4.setStart(now.plus(20, MINUTES));
        todo4.setStatus(TodoStatus.OPEN.name());

        Todo todo5 = TodoUtil.anyTodo();
        todo5.setCreator(creator1);
        todo5.setStart(now.plus(30, MINUTES));
        todo5.setStatus(TodoStatus.FINISHED.name());

        Todo todo6 = TodoUtil.anyTodo();
        todo6.setCreator(creator1);
        todo6.setStart(now.plus(40, MINUTES));
        todo6.setStatus(TodoStatus.OPEN.name());

        List<Todo> todos = Arrays.asList( new Todo[] {todo1, todo2, todo3, todo4, todo5, todo6});

        addTodos(todos);

        Instant timeWindowStart = now.plus(5, MINUTES);
        Instant timeWindowEnd = now.plus(35, MINUTES);

        List<Todo> listTodos = listTodos(creator1.getId(), timeWindowStart, timeWindowEnd, null, null);

        assertThat(listTodos.size(), is(equalTo(2)));

        verifyTodosAreEqual(todo2, listTodos.get(0));
        verifyTodosAreEqual(todo3, listTodos.get(1));
    }

    private Todo addTodo(Todo todo) {
        APIGatewayProxyRequestEvent addRequestEvent = new APIGatewayProxyRequestEvent();
        addRequestEvent.setBody(JsonUtil.toJsonString(Arrays.asList( new Todo[] {todo})));
        APIGatewayProxyResponseEvent addResponseEvent = addTodoHandler.handleRequest(addRequestEvent, testContext);

        assertEquals(200, addResponseEvent.getStatusCode());

        List<Todo> added = (List<Todo>) JsonUtil.fromJsonString(addResponseEvent.getBody(), List.class, Todo.class);

        return added.get(0);
    }

    private List<Todo> addTodos(List<Todo> activities) {
        APIGatewayProxyRequestEvent addRequestEvent = new APIGatewayProxyRequestEvent();
        addRequestEvent.setBody(JsonUtil.toJsonString(activities));
        APIGatewayProxyResponseEvent addResponseEvent = addTodoHandler.handleRequest(addRequestEvent, testContext);

        assertEquals(200, addResponseEvent.getStatusCode());

        List<Todo> added = (List<Todo>) JsonUtil.fromJsonString(addResponseEvent.getBody(), List.class, Todo.class);

        return added;
    }

    private Todo getTodo(String todoId) {
        APIGatewayProxyRequestEvent getRequestEvent = new APIGatewayProxyRequestEvent();
        getRequestEvent.setPathParameters(Collections.singletonMap("todoId", todoId));
        APIGatewayProxyResponseEvent getResponseEvent = getTodoHandler.handleRequest(getRequestEvent, testContext);

        assertEquals(200, getResponseEvent.getStatusCode());

        Todo added = JsonUtil.fromJsonString(getResponseEvent.getBody(), Todo.class);

        return added;
    }

    private void deleteTodo(String todoId) {
        APIGatewayProxyRequestEvent deleteRequestEvent = new APIGatewayProxyRequestEvent();
        deleteRequestEvent.setPathParameters(Collections.singletonMap("todoId", todoId));
        APIGatewayProxyResponseEvent deleteResponseEvent = deleteTodoHandler.handleRequest(deleteRequestEvent, testContext);

        assertEquals(200, deleteResponseEvent.getStatusCode());
    }

    private void updateTodo(String todoId, Updatable updatable) {
        APIGatewayProxyRequestEvent updateRequestEvent = new APIGatewayProxyRequestEvent();
        updateRequestEvent.setPathParameters(Collections.singletonMap("todoId", todoId));
        updateRequestEvent.setBody(JsonUtil.toJsonString(updatable));
        APIGatewayProxyResponseEvent updateResponseEvent = updateTodoHandler.handleRequest(updateRequestEvent, testContext);

        assertEquals(200, updateResponseEvent.getStatusCode());
    }

    private void updateTodoResultInInternalServerError(String todoId, Updatable updatable) {
        APIGatewayProxyRequestEvent updateRequestEvent = new APIGatewayProxyRequestEvent();
        updateRequestEvent.setPathParameters(Collections.singletonMap("todoId", todoId));
        updateRequestEvent.setBody(JsonUtil.toJsonString(updatable));
        APIGatewayProxyResponseEvent updateResponseEvent = updateTodoHandler.handleRequest(updateRequestEvent, testContext);

        assertEquals(500, updateResponseEvent.getStatusCode());
    }

    private List<Todo> listTodos(String creatorId, Instant timeWindowStart, Instant timeWindowEnd, TodoStatus status, Integer limit) {
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

        APIGatewayProxyResponseEvent getResponseEvent = listTodoHandler.handleRequest(listRequestEvent, testContext);

        assertEquals(200, getResponseEvent.getStatusCode());

        List<Todo> listTodos = (List<Todo>)JsonUtil.fromJsonString(getResponseEvent.getBody(), List.class, Todo.class);

        return listTodos;
    }

    private void verifyTodosAreEqual(Todo expected, Todo actual) {
        assertThat(expected.getSummary(), is(equalTo(actual.getSummary())));
        assertThat(expected.getDescription(), is(equalTo(actual.getDescription())));
        assertThat(expected.getCreator().getId(), is(equalTo(actual.getCreator().getId())));
        assertThat(expected.getCreator().getDisplayName(), is(equalTo(actual.getCreator().getDisplayName())));
        assertThat(expected.getCreator().getEmail(), is(equalTo(actual.getCreator().getEmail())));
        assertThat(expected.getCreator().getSelf(), is(equalTo(actual.getCreator().getSelf())));
        assertThat(expected.getCreated(), is(equalTo(actual.getCreated())));
        assertThat(expected.getUpdated(), is(equalTo(actual.getUpdated())));
        assertThat(expected.getStart(), is(equalTo(actual.getStart())));
        assertThat(expected.getEnd(), is(equalTo(actual.getEnd())));
        assertThat(expected.getActivity(), is(equalTo(actual.getActivity())));
        assertThat(expected.getStatus(), is(equalTo(actual.getStatus())));
        assertThat(expected.getLocation(), is(equalTo(actual.getLocation())));
        assertThat(expected.getEndTimeUnspecified(), is(equalTo(actual.getEndTimeUnspecified())));

        expected.getRecurrence().stream().forEach(s-> actual.getRecurrence().contains(s));
    }
*/
}

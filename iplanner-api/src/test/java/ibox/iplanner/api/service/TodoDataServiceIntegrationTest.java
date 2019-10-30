package ibox.iplanner.api.service;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import ibox.iplanner.api.model.Todo;
import ibox.iplanner.api.model.TodoStatus;
import ibox.iplanner.api.model.User;
import ibox.iplanner.api.model.updatable.Updatable;
import ibox.iplanner.api.model.updatable.UpdatableAttribute;
import ibox.iplanner.api.model.updatable.UpdatableKey;
import ibox.iplanner.api.model.updatable.UpdateAction;
import ibox.iplanner.api.util.TodoUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ibox.iplanner.api.service.dbmodel.TodoDefinition.*;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class TodoDataServiceIntegrationTest extends LocalDynamoDBIntegrationTestSupport {

    private static TodoDataService todoDataService;

    @BeforeClass
    public static void setup() {
        dynamoDBSetup.createTodoTable(10L, 5L);

        todoDataService = new TodoDataService(new DynamoDB(amazonDynamoDB));
    }

    @Test
    public void givenValidTodo_addTodo_shouldCreateRecord() {

        Todo todo = TodoUtil.anyTodo();

        todoDataService.addTodo(todo);

        Todo dbTodo = todoDataService.getTodo(todo.getId());

        verifyTodosAreEqual(todo, dbTodo);
    }

    @Test
    public void givenValidTodos_addTodos_shouldCreateRecords() {

        List<Todo> todos = TodoUtil.anyTodoList();

        todoDataService.addTodos(todos);

        todos.stream().forEach(e -> {
            String id = e.getId();

            Todo dbTodo = todoDataService.getTodo(e.getId());

            verifyTodosAreEqual(e, dbTodo);

        });
    }

    @Test
    public void givenValidUpdatable_updateTodo_shouldUpdateRecord() {

        Todo activity = TodoUtil.anyTodo();

        todoDataService.addTodo(activity);

        Todo dbTodo = todoDataService.getTodo(activity.getId());

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
                        .addComponent(FIELD_NAME_ID, dbTodo.getId()))
                .updatableAttributes(updatableAttributeSet)
                .build();

        Todo updated = todoDataService.updateTodo(updatable);

        assertThat(updated.getSummary(), is(equalTo(newSummary)));
        assertThat(updated.getDescription(), is(equalTo(newDescription)));
        assertThat(updated.getActivity(), is(equalTo(newActivity)));
        assertThat(updated.getLocation(), is(equalTo(newLocation)));
        assertThat(updated.getRecurrence(), hasItem("abc"));
    }

    @Test
    public void givenValidId_deleteTodo_shouldUpdateTodoStatus() {

        Todo todo = TodoUtil.anyTodo();
        todo.setStatus(TodoStatus.OPEN.name());

        todoDataService.addTodo(todo);

        Todo dbTodo = todoDataService.getTodo(todo.getId());

        Todo deleted = todoDataService.deleteTodo(dbTodo.getId());

        assertThat(deleted.getStatus(), is(equalTo(TodoStatus.CLOSED.name())));

        Todo theTodo = todoDataService.getTodo(dbTodo.getId());

        assertThat(theTodo.getStatus(), is(equalTo(TodoStatus.CLOSED.name())));

    }

    @Test(expected = AmazonDynamoDBException.class)
    public void givenValidUpdatable_updateTodo_shouldNotUpdateKeyField() {

        Todo todo = TodoUtil.anyTodo();

        todoDataService.addTodo(todo);

        Todo dbTodo = todoDataService.getTodo(todo.getId());

        Set<UpdatableAttribute> updatableAttributeSet = new HashSet<>();
        updatableAttributeSet.add( UpdatableAttribute.builder()
                .attributeName(FIELD_NAME_ID)
                .action(UpdateAction.UPDATE)
                .value("1234567890")
                .build());
        Updatable updatable = Updatable.builder()
                .objectType("todo")
                .primaryKey(new UpdatableKey()
                        .addComponent(FIELD_NAME_ID, dbTodo.getId()))
                .updatableAttributes(updatableAttributeSet)
                .build();

        Todo updated = todoDataService.updateTodo(updatable);
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
        todo5.setStatus(TodoStatus.OPEN.name());

        Todo todo6 = TodoUtil.anyTodo();
        todo6.setCreator(creator1);
        todo6.setStart(now.plus(40, MINUTES));
        todo6.setStatus(TodoStatus.OPEN.name());

        List<Todo> todos = Arrays.asList( new Todo[] {todo1, todo2, todo3, todo4, todo5, todo6});

        todoDataService.addTodos(todos);

        Instant timeWindowStart = now.plus(5, MINUTES);
        Instant timeWindowEnd = now.plus(35, MINUTES);

        List<Todo> myTodos = todoDataService.getMyTodosWithinTime(creator1.getId(), timeWindowStart, timeWindowEnd, TodoStatus.OPEN.name(),null);

        assertThat(myTodos.size(), is(equalTo(3)));
    }

    private void verifyTodosAreEqual(Todo expected, Todo actual) {

        assertThat(expected.getId(), is(equalTo(actual.getId())));
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

}

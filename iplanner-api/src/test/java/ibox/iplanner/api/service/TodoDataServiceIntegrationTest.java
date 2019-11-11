package ibox.iplanner.api.service;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import ibox.iplanner.api.lambda.exception.InvalidInputException;
import ibox.iplanner.api.model.*;
import ibox.iplanner.api.util.TodoUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static ibox.iplanner.api.service.TestHelper.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class TodoDataServiceIntegrationTest extends LocalDynamoDBIntegrationTestSupport {

    private static TodoDataService todoDataService;
    private static DynamoDB dynamoDB;

    @BeforeClass
    public static void setup() {
        dynamoDBSetup.createTodoTable(10L, 5L);
        dynamoDB = new DynamoDB(amazonDynamoDB);
        todoDataService = new TodoDataService(dynamoDB);
    }

    @Test
    public void givenValidTodo_addTodo_shouldCreateRecord() {

        Todo todo = TodoUtil.anyTodo();

        todoDataService.addTodo(todo);

        Todo dbTodo = todoDataService.getTodo(todo.getId());

        verifyTodoAreEqual(todo, dbTodo);
    }

    @Test
    public void givenValidTodoList_addTodoList_shouldCreateRecords() {

        List<Todo> todoList = TodoUtil.anyTodoList();

        todoDataService.addTodoList(todoList);

        todoList.stream().forEach(e -> {
            String id = e.getId();

            Todo dbTodo = todoDataService.getTodo(e.getId());

            verifyTodoAreEqual(e, dbTodo);

        });
    }

    @Test
    public void givenValidUpdatable_updateTodo_shouldUpdateRecord() {

        Todo myTodo = TodoUtil.anyTodo();

        todoDataService.addTodo(myTodo);

        Todo dbTodo = todoDataService.getTodo(myTodo.getId());

        dbTodo.setSummary("new summary");
        dbTodo.setDescription("new description");
        dbTodo.setActivityType("new activity");

        Todo updated = todoDataService.updateTodo(dbTodo);

        assertThat(updated.getSummary(), is(equalTo("new summary")));
        assertThat(updated.getDescription(), is(equalTo("new description")));
        assertThat(updated.getActivityType(), is(equalTo(myTodo.getActivityType())));
    }

    @Test
    public void givenValidId_deleteTodo_shouldUpdateTodoStatus() {

        Todo todo = TodoUtil.anyTodo();
        todo.setStatus(TodoStatus.OPEN);

        todoDataService.addTodo(todo);

        Todo dbTodo = todoDataService.getTodo(todo.getId());

        Todo deleted = todoDataService.deleteTodo(dbTodo.getId());

        assertThat(deleted.getStatus(), is(equalTo(TodoStatus.CLOSED)));

        Todo theTodo = todoDataService.getTodo(dbTodo.getId());

        assertThat(theTodo.getStatus(), is(equalTo(TodoStatus.CLOSED)));

    }

    @Test(expected = InvalidInputException.class)
    public void givenValidUpdatable_updateTodo_shouldNotUpdateKeyField() {

        Todo todo = TodoUtil.anyTodo();

        todoDataService.addTodo(todo);

        Todo dbTodo = todoDataService.getTodo(todo.getId());
        dbTodo.setId("123456789");

        Todo updated = todoDataService.updateTodo(dbTodo);
    }

    @Test
    public void givenTodoListWithCreators_getMyTodoListByFilter_shouldReturnOnlyCreatorTodoListForSpecifiedActivities() {

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
        todo4.setCreator(creator2);
        todo4.setActivityId("activity_id_4");
        todo4.setStatus(TodoStatus.OPEN);

        Todo todo5 = TodoUtil.anyTodo();
        todo5.setCreator(creator1);
        todo5.setActivityId("activity_id_5");
        todo5.setStatus(TodoStatus.OPEN);

        Todo todo6 = TodoUtil.anyTodo();
        todo6.setCreator(creator1);
        todo6.setActivityId("activity_id_6");
        todo6.setStatus(TodoStatus.OPEN);

        List<Todo> todoList = Arrays.asList( new Todo[] {todo1, todo2, todo3, todo4, todo5, todo6});

        todoDataService.addTodoList(todoList);

        List<String> selectedActivityIds = Arrays.asList( new String[]{"activity_id_1", "activity_id_2", "activity_id_3"});

        List<Todo> myTodoList = todoDataService.getMyTodoListByFilter(creator1.getId(), new HashSet(selectedActivityIds), TodoStatus.OPEN.name(),null);

        assertThat(myTodoList.size(), is(equalTo(3)));
    }

    private void verifyTodoAreEqual(Todo expected, Todo actual) {

        assertThat(expected.getId(), is(equalTo(actual.getId())));
        assertThat(expected.getSummary(), is(equalTo(actual.getSummary())));
        assertThat(expected.getDescription(), is(equalTo(actual.getDescription())));
        assertThat(expected.getCreator().getId(), is(equalTo(actual.getCreator().getId())));
        assertThat(expected.getCreator().getDisplayName(), is(equalTo(actual.getCreator().getDisplayName())));
        assertThat(expected.getCreator().getEmail(), is(equalTo(actual.getCreator().getEmail())));
        assertThat(expected.getCreator().getSelf(), is(equalTo(actual.getCreator().getSelf())));
        assertThat(expected.getCreated(), is(equalTo(actual.getCreated())));
        assertThat(expected.getUpdated(), is(equalTo(actual.getUpdated())));
        assertThat(expected.getStatus(), is(equalTo(actual.getStatus())));

        verifyTodoAttributeSetAreEqual(expected.getAttributeSet(), actual.getAttributeSet());
    }

    private void verifyTodoAttributeSetAreEqual(AttributeSet expected, AttributeSet actual) {
        assertThat(expected.getAttributes().size(), is(equalTo(actual.getAttributes().size())));
        expected.getSupportedFeatures().stream().forEach(feature -> {
            TodoAttribute expectedAttribute = expected.getAttribute(feature);
            TodoAttribute actualAttribute = actual.getAttribute(feature);
            verifyTodoAttributeAreEqual(expectedAttribute, actualAttribute);
        });
    }

    private void verifyTodoAttributeAreEqual(TodoAttribute expected, TodoAttribute actual) {

        if (expected.getClass().equals(TagAttribute.class)) {
            verifyTaggingAttributeAreEqual((TagAttribute) expected, (TagAttribute) actual);
        }
        else if (expected.getClass().equals(EventAttribute.class)) {
            verifyEventAttributeAreEqual((EventAttribute) expected, (EventAttribute) actual);
        }
        else if (expected.getClass().equals(LocationAttribute.class)) {
            verifyLocationAttributeAreEqual((LocationAttribute) expected, (LocationAttribute) actual);
        }
        else if (expected.getClass().equals(TimelineAttribute.class)) {
            verifyTimelineAttributeAreEqual((TimelineAttribute) expected, (TimelineAttribute) actual);
        }
    }
}

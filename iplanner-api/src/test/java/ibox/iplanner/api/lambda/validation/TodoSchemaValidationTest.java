package ibox.iplanner.api.lambda.validation;

import ibox.iplanner.api.lambda.exception.InvalidInputException;
import ibox.iplanner.api.model.Todo;
import ibox.iplanner.api.util.TodoUtil;
import ibox.iplanner.api.util.JsonUtil;
import org.junit.Test;

import java.util.List;

public class TodoSchemaValidationTest {

    static JsonSchemaValidator validator = new JsonSchemaValidator(new EntitySchemaMap());

    @Test
    public void givenValidTodo_validateTodoShouldPass() {

        Todo todo = TodoUtil.anyTodo();

        validator.validate(JsonUtil.toJsonString(todo), Todo.class, "/todo");
    }

    @Test
    public void givenValidTodoArray_validateTodoArrayShouldPass() {

        List<Todo> activities = TodoUtil.anyTodoList();

        validator.validate(JsonUtil.toJsonString(activities), Todo.class, "/todo-array");
    }

    @Test(expected = InvalidInputException.class)
    public void givenTodoMissingSummary_validateTodoShouldFail() {

        Todo todo = TodoUtil.anyTodo();
        //todo.setSummary(null);
        validator.validate(JsonUtil.toJsonString(todo), Todo.class, "/todo");
    }

    @Test(expected = InvalidInputException.class)
    public void givenTodoEmptySummary_validateTodoShouldFail() {

        Todo todo = TodoUtil.anyTodo();
        //todo.setSummary("");
        validator.validate(JsonUtil.toJsonString(todo), Todo.class, "/todo");
    }

    @Test(expected = InvalidInputException.class)
    public void givenTodoMissingActivity_validateTodoShouldFail() {

        Todo todo = TodoUtil.anyTodo();
        //todo.setActivity(null);
        validator.validate(JsonUtil.toJsonString(todo), Todo.class, "/todo");
    }

    @Test(expected = InvalidInputException.class)
    public void givenTodoEmptyActivity_validateTodoShouldFail() {

        Todo todo = TodoUtil.anyTodo();
        //todo.setActivity("");
        validator.validate(JsonUtil.toJsonString(todo), Todo.class, "/todo");
    }

    @Test(expected = InvalidInputException.class)
    public void givenTodoMissingCreated_validateTodoShouldFail() {

        Todo todo = TodoUtil.anyTodo();
        //todo.setCreated(null);
        validator.validate(JsonUtil.toJsonString(todo), Todo.class, "/todo");
    }

    @Test(expected = InvalidInputException.class)
    public void givenTodoMissingStart_validateTodoShouldFail() {

        Todo todo = TodoUtil.anyTodo();
        //todo.setStart(null);
        validator.validate(JsonUtil.toJsonString(todo), Todo.class, "/todo");
    }

    @Test(expected = InvalidInputException.class)
    public void givenTodoMissingCreator_validateTodoShouldFail() {

        Todo todo = TodoUtil.anyTodo();
        //todo.setCreator(null);
        validator.validate(JsonUtil.toJsonString(todo), Todo.class, "/todo");
    }

    @Test(expected = InvalidInputException.class)
    public void givenTodoMissingCreatorId_validateTodoShouldFail() {

        Todo todo = TodoUtil.anyTodo();
        //todo.getCreator().setId(null);
        validator.validate(JsonUtil.toJsonString(todo), Todo.class, "/todo");
    }

    @Test(expected = InvalidInputException.class)
    public void givenTodoEmptyCreatorId_validateTodoShouldFail() {

        Todo todo = TodoUtil.anyTodo();
        //todo.getCreator().setId("");
        validator.validate(JsonUtil.toJsonString(todo), Todo.class, "/todo");
    }

    @Test(expected = InvalidInputException.class)
    public void givenTodoMissingCreatorEmail_validateTodoShouldFail() {

        Todo todo = TodoUtil.anyTodo();
        //todo.getCreator().setEmail(null);
        validator.validate(JsonUtil.toJsonString(todo), Todo.class, "/todo");
    }

    @Test(expected = InvalidInputException.class)
    public void givenTodoEmptyCreatorEmail_validateTodoShouldFail() {

        Todo todo = TodoUtil.anyTodo();
        //todo.getCreator().setEmail("");
        validator.validate(JsonUtil.toJsonString(todo), Todo.class, "/todo");
    }

    @Test(expected = InvalidInputException.class)
    public void givenTodoMissingCreatorDisplayName_validateTodoShouldFail() {

        Todo todo = TodoUtil.anyTodo();
        //todo.getCreator().setDisplayName(null);
        validator.validate(JsonUtil.toJsonString(todo), Todo.class, "/todo");
    }

    @Test(expected = InvalidInputException.class)
    public void givenTodoEmptyCreatorDisplayName_validateTodoShouldFail() {

        Todo todo = TodoUtil.anyTodo();
        //todo.getCreator().setDisplayName("");
        validator.validate(JsonUtil.toJsonString(todo), Todo.class, "/todo");
    }

    @Test(expected = InvalidInputException.class)
    public void givenTodoMissingCreatorSelf_validateTodoShouldFail() {

        Todo todo = TodoUtil.anyTodo();
        //todo.getCreator().setSelf(null);
        validator.validate(JsonUtil.toJsonString(todo), Todo.class, "/todo");
    }
}

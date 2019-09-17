package ibox.iplanner.api.lambda.validation;

import ibox.iplanner.api.lambda.exception.InvalidInputException;
import ibox.iplanner.api.model.Task;
import ibox.iplanner.api.util.JsonUtil;
import ibox.iplanner.api.util.TaskUtil;
import org.junit.Test;

import java.util.List;

public class TaskSchemaValidationTest {

    static JsonSchemaValidator validator = new JsonSchemaValidator(new EntitySchemaMap());

    @Test
    public void givenValidTask_validateTaskShouldPass() {

        Task task = TaskUtil.anyTask();

        validator.validate(JsonUtil.toJsonString(task), Task.class, "/task");
    }

    @Test
    public void givenValidTaskArray_validateTaskArrayShouldPass() {

        List<Task> activities = TaskUtil.anyTaskList();

        validator.validate(JsonUtil.toJsonString(activities), Task.class, "/task-array");
    }

    @Test(expected = InvalidInputException.class)
    public void givenTaskMissingTitle_validateTaskShouldFail() {

        Task task = TaskUtil.anyTask();
        task.setTitle(null);
        validator.validate(JsonUtil.toJsonString(task), Task.class, "/task");
    }

    @Test(expected = InvalidInputException.class)
    public void givenTaskEmptyTitle_validateTaskShouldFail() {

        Task task = TaskUtil.anyTask();
        task.setTitle("");
        validator.validate(JsonUtil.toJsonString(task), Task.class, "/task");
    }

    @Test(expected = InvalidInputException.class)
    public void givenTaskMissingType_validateTaskShouldFail() {

        Task task = TaskUtil.anyTask();
        task.setType(null);
        validator.validate(JsonUtil.toJsonString(task), Task.class, "/task");
    }

    @Test(expected = InvalidInputException.class)
    public void givenTaskEmptyType_validateTaskShouldFail() {

        Task task = TaskUtil.anyTask();
        task.setType("");
        validator.validate(JsonUtil.toJsonString(task), Task.class, "/task");
    }

    @Test(expected = InvalidInputException.class)
    public void givenTaskMissingCreated_validateTaskShouldFail() {

        Task task = TaskUtil.anyTask();
        task.setCreated(null);
        validator.validate(JsonUtil.toJsonString(task), Task.class, "/task");
    }

    @Test(expected = InvalidInputException.class)
    public void givenTaskMissingCreator_validateTaskShouldFail() {

        Task task = TaskUtil.anyTask();
        task.setCreator(null);
        validator.validate(JsonUtil.toJsonString(task), Task.class, "/task");
    }

    @Test(expected = InvalidInputException.class)
    public void givenTaskMissingCreatorId_validateTaskShouldFail() {

        Task task = TaskUtil.anyTask();
        task.getCreator().setId(null);
        validator.validate(JsonUtil.toJsonString(task), Task.class, "/task");
    }

    @Test(expected = InvalidInputException.class)
    public void givenTaskEmptyCreatorId_validateTaskShouldFail() {

        Task task = TaskUtil.anyTask();
        task.getCreator().setId("");
        validator.validate(JsonUtil.toJsonString(task), Task.class, "/task");
    }

    @Test(expected = InvalidInputException.class)
    public void givenTaskMissingCreatorEmail_validateTaskShouldFail() {

        Task task = TaskUtil.anyTask();
        task.getCreator().setEmail(null);
        validator.validate(JsonUtil.toJsonString(task), Task.class, "/task");
    }

    @Test(expected = InvalidInputException.class)
    public void givenTaskEmptyCreatorEmail_validateTaskShouldFail() {

        Task task = TaskUtil.anyTask();
        task.getCreator().setEmail("");
        validator.validate(JsonUtil.toJsonString(task), Task.class, "/task");
    }

    @Test(expected = InvalidInputException.class)
    public void givenTaskMissingCreatorDisplayName_validateTaskShouldFail() {

        Task task = TaskUtil.anyTask();
        task.getCreator().setDisplayName(null);
        validator.validate(JsonUtil.toJsonString(task), Task.class, "/task");
    }

    @Test(expected = InvalidInputException.class)
    public void givenTaskEmptyCreatorDisplayName_validateTaskShouldFail() {

        Task task = TaskUtil.anyTask();
        task.getCreator().setDisplayName("");
        validator.validate(JsonUtil.toJsonString(task), Task.class, "/task");
    }

    @Test(expected = InvalidInputException.class)
    public void givenTaskMissingCreatorSelf_validateTaskShouldFail() {

        Task task = TaskUtil.anyTask();
        task.getCreator().setSelf(null);
        validator.validate(JsonUtil.toJsonString(task), Task.class, "/task");
    }

    @Test(expected = InvalidInputException.class)
    public void givenTaskMissingDeadline_validateTaskShouldFail() {

        Task task = TaskUtil.anyTask();
        task.setDeadline(null);
        validator.validate(JsonUtil.toJsonString(task), Task.class, "/task");
    }

}

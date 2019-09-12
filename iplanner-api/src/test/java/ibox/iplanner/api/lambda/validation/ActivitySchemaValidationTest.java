package ibox.iplanner.api.lambda.validation;

import ibox.iplanner.api.lambda.exception.InvalidInputException;
import ibox.iplanner.api.model.Activity;
import ibox.iplanner.api.util.ActivityUtil;
import ibox.iplanner.api.util.JsonUtil;
import org.junit.Test;

import java.util.List;

public class ActivitySchemaValidationTest {

    static JsonSchemaValidator validator = new JsonSchemaValidator(new EntitySchemaMap());

    @Test
    public void givenValidActivity_validateActivityShouldPass() {

        Activity activity = ActivityUtil.anyActivity();

        validator.validate(JsonUtil.toJsonString(activity), Activity.class, "/activity");
    }

    @Test
    public void givenValidActivityArray_validateActivityArrayShouldPass() {

        List<Activity> activities = ActivityUtil.anyActivityList();

        validator.validate(JsonUtil.toJsonString(activities), Activity.class, "/activity-array");
    }

    @Test(expected = InvalidInputException.class)
    public void givenActivityMissingTitle_validateActivityShouldFail() {

        Activity activity = ActivityUtil.anyActivity();
        activity.setTitle(null);
        validator.validate(JsonUtil.toJsonString(activity), Activity.class, "/activity");
    }

    @Test(expected = InvalidInputException.class)
    public void givenActivityEmptyTitle_validateActivityShouldFail() {

        Activity activity = ActivityUtil.anyActivity();
        activity.setTitle("");
        validator.validate(JsonUtil.toJsonString(activity), Activity.class, "/activity");
    }

    @Test(expected = InvalidInputException.class)
    public void givenActivityMissingTemplate_validateActivityShouldFail() {

        Activity activity = ActivityUtil.anyActivity();
        activity.setTemplate(null);
        validator.validate(JsonUtil.toJsonString(activity), Activity.class, "/activity");
    }

    @Test(expected = InvalidInputException.class)
    public void givenActivityEmptyTemplate_validateActivityShouldFail() {

        Activity activity = ActivityUtil.anyActivity();
        activity.setTemplate("");
        validator.validate(JsonUtil.toJsonString(activity), Activity.class, "/activity");
    }

    @Test(expected = InvalidInputException.class)
    public void givenActivityMissingCreated_validateActivityShouldFail() {

        Activity activity = ActivityUtil.anyActivity();
        activity.setCreated(null);
        validator.validate(JsonUtil.toJsonString(activity), Activity.class, "/activity");
    }

    @Test(expected = InvalidInputException.class)
    public void givenActivityMissingCreator_validateActivityShouldFail() {

        Activity activity = ActivityUtil.anyActivity();
        activity.setCreator(null);
        validator.validate(JsonUtil.toJsonString(activity), Activity.class, "/activity");
    }

    @Test(expected = InvalidInputException.class)
    public void givenActivityMissingCreatorId_validateActivityShouldFail() {

        Activity activity = ActivityUtil.anyActivity();
        activity.getCreator().setId(null);
        validator.validate(JsonUtil.toJsonString(activity), Activity.class, "/activity");
    }

    @Test(expected = InvalidInputException.class)
    public void givenActivityEmptyCreatorId_validateActivityShouldFail() {

        Activity activity = ActivityUtil.anyActivity();
        activity.getCreator().setId("");
        validator.validate(JsonUtil.toJsonString(activity), Activity.class, "/activity");
    }

    @Test(expected = InvalidInputException.class)
    public void givenActivityMissingCreatorEmail_validateActivityShouldFail() {

        Activity activity = ActivityUtil.anyActivity();
        activity.getCreator().setEmail(null);
        validator.validate(JsonUtil.toJsonString(activity), Activity.class, "/activity");
    }

    @Test(expected = InvalidInputException.class)
    public void givenActivityEmptyCreatorEmail_validateActivityShouldFail() {

        Activity activity = ActivityUtil.anyActivity();
        activity.getCreator().setEmail("");
        validator.validate(JsonUtil.toJsonString(activity), Activity.class, "/activity");
    }

    @Test(expected = InvalidInputException.class)
    public void givenActivityMissingCreatorDisplayName_validateActivityShouldFail() {

        Activity activity = ActivityUtil.anyActivity();
        activity.getCreator().setDisplayName(null);
        validator.validate(JsonUtil.toJsonString(activity), Activity.class, "/activity");
    }

    @Test(expected = InvalidInputException.class)
    public void givenActivityEmptyCreatorDisplayName_validateActivityShouldFail() {

        Activity activity = ActivityUtil.anyActivity();
        activity.getCreator().setDisplayName("");
        validator.validate(JsonUtil.toJsonString(activity), Activity.class, "/activity");
    }

    @Test(expected = InvalidInputException.class)
    public void givenActivityMissingCreatorSelf_validateActivityShouldFail() {

        Activity activity = ActivityUtil.anyActivity();
        activity.getCreator().setSelf(null);
        validator.validate(JsonUtil.toJsonString(activity), Activity.class, "/activity");
    }
}

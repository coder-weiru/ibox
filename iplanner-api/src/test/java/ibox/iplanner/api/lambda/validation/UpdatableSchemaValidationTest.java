package ibox.iplanner.api.lambda.validation;

import ibox.iplanner.api.lambda.exception.InvalidInputException;
import ibox.iplanner.api.model.updatable.Updatable;
import ibox.iplanner.api.model.updatable.UpdatableAttribute;
import ibox.iplanner.api.model.updatable.UpdatableKey;
import ibox.iplanner.api.model.updatable.UpdateAction;
import ibox.iplanner.api.service.dbmodel.ActivityDefinition;
import ibox.iplanner.api.util.ActivityUtil;
import ibox.iplanner.api.util.JsonUtil;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class UpdatableSchemaValidationTest {

    static JsonSchemaValidator validator = new JsonSchemaValidator(new EntitySchemaMap());

    @Test
    public void givenValidUpdatable_validateUpdatableShouldPass() {

        Updatable updatable = activityUpdatable();

        validator.validate(JsonUtil.toJsonString(updatable), Updatable.class);
    }

    @Test(expected = InvalidInputException.class)
    public void givenUpdatableMissingObjectType_validateUpdatableShouldFail() {

        Updatable updatable = activityUpdatable();
        updatable.setObjectType(null);
        validator.validate(JsonUtil.toJsonString(updatable), Updatable.class);
    }

    @Test(expected = InvalidInputException.class)
    public void givenUpdatableEmptyObjectType_validateUpdatableShouldFail() {

        Updatable updatable = activityUpdatable();
        updatable.setObjectType("");
        validator.validate(JsonUtil.toJsonString(updatable), Updatable.class);
    }

    @Test(expected = InvalidInputException.class)
    public void givenUpdatableMissingPrimaryKey_validateUpdatableShouldFail() {

        Updatable updatable = activityUpdatable();
        updatable.setPrimaryKey(null);
        validator.validate(JsonUtil.toJsonString(updatable), Updatable.class);
    }

    @Test(expected = InvalidInputException.class)
    public void givenUpdatableEmptyPrimaryKey_validateUpdatableShouldFail() {

        Updatable updatable = activityUpdatable();
        updatable.getPrimaryKey().getComponents().clear();
        validator.validate(JsonUtil.toJsonString(updatable), Updatable.class);
    }

    @Test(expected = InvalidInputException.class)
    public void givenUpdatableMissingPrimaryKeyAttributeName_validateUpdatableShouldFail() {

        Updatable updatable = activityUpdatable();
        updatable.getPrimaryKey().getComponents().stream().forEach(e -> e.setAttributeName(null));
        validator.validate(JsonUtil.toJsonString(updatable), Updatable.class);
    }

    @Test(expected = InvalidInputException.class)
    public void givenUpdatableMissingPrimaryKeyAttributeValue_validateUpdatableShouldFail() {

        Updatable updatable = activityUpdatable();
        updatable.getPrimaryKey().getComponents().stream().forEach(e -> e.setValue(null));
        validator.validate(JsonUtil.toJsonString(updatable), Updatable.class);
    }

    @Test(expected = InvalidInputException.class)
    public void givenUpdatableMissingUpdatableAttributes_validateUpdatableShouldFail() {

        Updatable updatable = activityUpdatable();
        updatable.setUpdatableAttributes(null);
        validator.validate(JsonUtil.toJsonString(updatable), Updatable.class);
    }

    @Test(expected = InvalidInputException.class)
    public void givenUpdatableMissingUpdatableAttributeName_validateUpdatableShouldFail() {

        Updatable updatable = activityUpdatable();
        updatable.getUpdatableAttributes().stream().forEach(e -> e.setAttributeName(null));
        validator.validate(JsonUtil.toJsonString(updatable), Updatable.class);
    }

    @Test(expected = InvalidInputException.class)
    public void givenUpdatableMissingUpdatableAttributeAction_validateUpdatableShouldFail() {

        Updatable updatable = activityUpdatable();
        updatable.getUpdatableAttributes().stream().forEach(e -> e.setAction(null));
        validator.validate(JsonUtil.toJsonString(updatable), Updatable.class);
    }

    @Test(expected = InvalidInputException.class)
    public void givenUpdatableEmptyUpdatableAttributeName_validateUpdatableShouldFail() {

        Updatable updatable = activityUpdatable();
        updatable.getUpdatableAttributes().stream().forEach(e -> e.setAttributeName(""));
        validator.validate(JsonUtil.toJsonString(updatable), Updatable.class);
    }

    private Updatable activityUpdatable() {
        String newTitle = "new title";
        String newDescription = "new description";
        String newTemplate = "new template";

        Set<UpdatableAttribute> updatableAttributeSet = new HashSet<>();
        updatableAttributeSet.add( UpdatableAttribute.builder()
                .attributeName(ActivityDefinition.FIELD_NAME_TITLE)
                .action(UpdateAction.UPDATE)
                .value(newTitle)
                .build());
        updatableAttributeSet.add( UpdatableAttribute.builder()
                .attributeName(ActivityDefinition.FIELD_NAME_DESCRIPTION)
                .action(UpdateAction.UPDATE)
                .value(newDescription)
                .build());
        updatableAttributeSet.add( UpdatableAttribute.builder()
                .attributeName(ActivityDefinition.FIELD_NAME_ACTIVITY_TYPE)
                .action(UpdateAction.UPDATE)
                .value(newTemplate)
                .build());

        return Updatable.builder()
                .objectType("activity")
                .primaryKey(new UpdatableKey()
                        .addComponent(ActivityDefinition.FIELD_NAME_ID, ActivityUtil.anyActivityId()))
                .updatableAttributes(updatableAttributeSet)
                .build();
    }
}

package ibox.iplanner.api.lambda.validation;

import ibox.iplanner.api.lambda.exception.InvalidInputException;
import ibox.iplanner.api.model.Event;
import ibox.iplanner.api.util.EventUtil;
import ibox.iplanner.api.util.JsonUtil;
import org.junit.Test;

import java.util.List;

public class EventSchemaValidationTest {

    static JsonSchemaValidator validator = new JsonSchemaValidator(new EntitySchemaMap());

    @Test
    public void givenValidEvent_validateEventShouldPass() {

        Event event = EventUtil.anyEvent();

        validator.validate(JsonUtil.toJsonString(event), Event.class, "/event");
    }

    @Test
    public void givenValidEventArray_validateEventArrayShouldPass() {

        List<Event> activities = EventUtil.anyEventList();

        validator.validate(JsonUtil.toJsonString(activities), Event.class, "/event-array");
    }

    @Test(expected = InvalidInputException.class)
    public void givenEventMissingSummary_validateEventShouldFail() {

        Event event = EventUtil.anyEvent();
        event.setSummary(null);
        validator.validate(JsonUtil.toJsonString(event), Event.class, "/event");
    }

    @Test(expected = InvalidInputException.class)
    public void givenEventEmptySummary_validateEventShouldFail() {

        Event event = EventUtil.anyEvent();
        event.setSummary("");
        validator.validate(JsonUtil.toJsonString(event), Event.class, "/event");
    }

    @Test(expected = InvalidInputException.class)
    public void givenEventMissingActivity_validateEventShouldFail() {

        Event event = EventUtil.anyEvent();
        event.setActivity(null);
        validator.validate(JsonUtil.toJsonString(event), Event.class, "/event");
    }

    @Test(expected = InvalidInputException.class)
    public void givenEventEmptyActivity_validateEventShouldFail() {

        Event event = EventUtil.anyEvent();
        event.setActivity("");
        validator.validate(JsonUtil.toJsonString(event), Event.class, "/event");
    }

    @Test(expected = InvalidInputException.class)
    public void givenEventMissingCreated_validateEventShouldFail() {

        Event event = EventUtil.anyEvent();
        event.setCreated(null);
        validator.validate(JsonUtil.toJsonString(event), Event.class, "/event");
    }

    @Test(expected = InvalidInputException.class)
    public void givenEventMissingStart_validateEventShouldFail() {

        Event event = EventUtil.anyEvent();
        event.setStart(null);
        validator.validate(JsonUtil.toJsonString(event), Event.class, "/event");
    }

    @Test(expected = InvalidInputException.class)
    public void givenEventMissingCreator_validateEventShouldFail() {

        Event event = EventUtil.anyEvent();
        event.setCreator(null);
        validator.validate(JsonUtil.toJsonString(event), Event.class, "/event");
    }

    @Test(expected = InvalidInputException.class)
    public void givenEventMissingCreatorId_validateEventShouldFail() {

        Event event = EventUtil.anyEvent();
        event.getCreator().setId(null);
        validator.validate(JsonUtil.toJsonString(event), Event.class, "/event");
    }

    @Test(expected = InvalidInputException.class)
    public void givenEventEmptyCreatorId_validateEventShouldFail() {

        Event event = EventUtil.anyEvent();
        event.getCreator().setId("");
        validator.validate(JsonUtil.toJsonString(event), Event.class, "/event");
    }

    @Test(expected = InvalidInputException.class)
    public void givenEventMissingCreatorEmail_validateEventShouldFail() {

        Event event = EventUtil.anyEvent();
        event.getCreator().setEmail(null);
        validator.validate(JsonUtil.toJsonString(event), Event.class, "/event");
    }

    @Test(expected = InvalidInputException.class)
    public void givenEventEmptyCreatorEmail_validateEventShouldFail() {

        Event event = EventUtil.anyEvent();
        event.getCreator().setEmail("");
        validator.validate(JsonUtil.toJsonString(event), Event.class, "/event");
    }

    @Test(expected = InvalidInputException.class)
    public void givenEventMissingCreatorDisplayName_validateEventShouldFail() {

        Event event = EventUtil.anyEvent();
        event.getCreator().setDisplayName(null);
        validator.validate(JsonUtil.toJsonString(event), Event.class, "/event");
    }

    @Test(expected = InvalidInputException.class)
    public void givenEventEmptyCreatorDisplayName_validateEventShouldFail() {

        Event event = EventUtil.anyEvent();
        event.getCreator().setDisplayName("");
        validator.validate(JsonUtil.toJsonString(event), Event.class, "/event");
    }

    @Test(expected = InvalidInputException.class)
    public void givenEventMissingCreatorSelf_validateEventShouldFail() {

        Event event = EventUtil.anyEvent();
        event.getCreator().setSelf(null);
        validator.validate(JsonUtil.toJsonString(event), Event.class, "/event");
    }
}

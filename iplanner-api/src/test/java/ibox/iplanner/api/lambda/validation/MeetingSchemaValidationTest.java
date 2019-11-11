package ibox.iplanner.api.lambda.validation;

import ibox.iplanner.api.lambda.exception.InvalidInputException;
import ibox.iplanner.api.model.Meeting;
import ibox.iplanner.api.util.MeetingUtil;
import ibox.iplanner.api.util.JsonUtil;
import org.junit.Test;

import java.util.List;

public class MeetingSchemaValidationTest {

    static JsonSchemaValidator validator = new JsonSchemaValidator(new EntitySchemaMap());

    @Test
    public void givenValidMeeting_validateMeetingShouldPass() {

        Meeting meeting = MeetingUtil.anyMeeting();

        validator.validate(JsonUtil.toJsonString(meeting), Meeting.class, "/meeting");
    }

    @Test
    public void givenValidMeetingArray_validateMeetingArrayShouldPass() {

        List<Meeting> activities = MeetingUtil.anyMeetingList();

        validator.validate(JsonUtil.toJsonString(activities), Meeting.class, "/meeting-array");
    }

    @Test(expected = InvalidInputException.class)
    public void givenMeetingMissingTitle_validateMeetingShouldFail() {

        Meeting meeting = MeetingUtil.anyMeeting();
        meeting.setTitle(null);
        validator.validate(JsonUtil.toJsonString(meeting), Meeting.class, "/meeting");
    }

    @Test(expected = InvalidInputException.class)
    public void givenMeetingEmptyTitle_validateMeetingShouldFail() {

        Meeting meeting = MeetingUtil.anyMeeting();
        meeting.setTitle("");
        validator.validate(JsonUtil.toJsonString(meeting), Meeting.class, "/meeting");
    }

    @Test(expected = InvalidInputException.class)
    public void givenMeetingMissingType_validateMeetingShouldFail() {

        Meeting meeting = MeetingUtil.anyMeeting();
        meeting.setActivityType(null);
        validator.validate(JsonUtil.toJsonString(meeting), Meeting.class, "/meeting");
    }

    @Test(expected = InvalidInputException.class)
    public void givenMeetingEmptyType_validateMeetingShouldFail() {

        Meeting meeting = MeetingUtil.anyMeeting();
        meeting.setActivityType("");
        validator.validate(JsonUtil.toJsonString(meeting), Meeting.class, "/meeting");
    }

    @Test(expected = InvalidInputException.class)
    public void givenMeetingMissingCreated_validateMeetingShouldFail() {

        Meeting meeting = MeetingUtil.anyMeeting();
        meeting.setCreated(null);
        validator.validate(JsonUtil.toJsonString(meeting), Meeting.class, "/meeting");
    }

    @Test(expected = InvalidInputException.class)
    public void givenMeetingMissingCreator_validateMeetingShouldFail() {

        Meeting meeting = MeetingUtil.anyMeeting();
        meeting.setCreator(null);
        validator.validate(JsonUtil.toJsonString(meeting), Meeting.class, "/meeting");
    }

    @Test(expected = InvalidInputException.class)
    public void givenMeetingMissingCreatorId_validateMeetingShouldFail() {

        Meeting meeting = MeetingUtil.anyMeeting();
        meeting.getCreator().setId(null);
        validator.validate(JsonUtil.toJsonString(meeting), Meeting.class, "/meeting");
    }

    @Test(expected = InvalidInputException.class)
    public void givenMeetingEmptyCreatorId_validateMeetingShouldFail() {

        Meeting meeting = MeetingUtil.anyMeeting();
        meeting.getCreator().setId("");
        validator.validate(JsonUtil.toJsonString(meeting), Meeting.class, "/meeting");
    }

    @Test(expected = InvalidInputException.class)
    public void givenMeetingMissingCreatorEmail_validateMeetingShouldFail() {

        Meeting meeting = MeetingUtil.anyMeeting();
        meeting.getCreator().setEmail(null);
        validator.validate(JsonUtil.toJsonString(meeting), Meeting.class, "/meeting");
    }

    @Test(expected = InvalidInputException.class)
    public void givenMeetingEmptyCreatorEmail_validateMeetingShouldFail() {

        Meeting meeting = MeetingUtil.anyMeeting();
        meeting.getCreator().setEmail("");
        validator.validate(JsonUtil.toJsonString(meeting), Meeting.class, "/meeting");
    }

    @Test(expected = InvalidInputException.class)
    public void givenMeetingMissingCreatorDisplayName_validateMeetingShouldFail() {

        Meeting meeting = MeetingUtil.anyMeeting();
        meeting.getCreator().setDisplayName(null);
        validator.validate(JsonUtil.toJsonString(meeting), Meeting.class, "/meeting");
    }

    @Test(expected = InvalidInputException.class)
    public void givenMeetingEmptyCreatorDisplayName_validateMeetingShouldFail() {

        Meeting meeting = MeetingUtil.anyMeeting();
        meeting.getCreator().setDisplayName("");
        validator.validate(JsonUtil.toJsonString(meeting), Meeting.class, "/meeting");
    }

    @Test(expected = InvalidInputException.class)
    public void givenMeetingMissingCreatorSelf_validateMeetingShouldFail() {

        Meeting meeting = MeetingUtil.anyMeeting();
        meeting.getCreator().setSelf(null);
        validator.validate(JsonUtil.toJsonString(meeting), Meeting.class, "/meeting");
    }

}

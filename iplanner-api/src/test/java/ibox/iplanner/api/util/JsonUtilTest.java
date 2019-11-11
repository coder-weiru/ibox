package ibox.iplanner.api.util;

import ibox.iplanner.api.model.*;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class JsonUtilTest {

    @Test
    public void testTodoToJsonString() {
        Todo todo = TodoUtil.anyTodo();

        String json = JsonUtil.toJsonString(todo);

        assertTrue(json.contains("\"class\""));
    }

    @Test
    public void testMeetingToJsonString() {
        Meeting meeting = MeetingUtil.anyMeeting();

        String json = JsonUtil.toJsonString(meeting);

        assertTrue(json.contains("\"class\""));
    }

    @Test
    public void testTaskToJsonString() {
        Task task = TaskUtil.anyTask();

        String json = JsonUtil.toJsonString(task);

        assertTrue(json.contains("\"class\""));
    }

    @Test
    public void testActivityToJsonString() {
        Activity activity = ActivityUtil.anyActivity();

        String json = JsonUtil.toJsonString(activity);

        assertTrue(json.contains("\"class\""));
    }

    @Test
    public void testSpecificTodoAttributeToJsonString() {
        Activity activity = ActivityUtil.anyActivity();

        TagAttribute tagAttribute = (TagAttribute) activity.getAttribute(TodoFeature.TAGGING_FEATURE);
        String json = JsonUtil.toJsonString(tagAttribute);

        assertTrue(json.contains("\"class\""));
    }

    @Test
    public void testTodoAttributeListToJsonString() {
        AttributeSet attributeList = new AttributeSet();

        attributeList.addAttribute(ActivityUtil.anyTagAttribute());
        attributeList.addAttribute(MeetingUtil.anyEventAttribute());
        attributeList.addAttribute(MeetingUtil.anyLocationAttribute());
        attributeList.addAttribute(TaskUtil.anyTimelineAttribute());

        String json = JsonUtil.toJsonString(attributeList);

        assertTrue(json.contains("\"class\""));
    }
}

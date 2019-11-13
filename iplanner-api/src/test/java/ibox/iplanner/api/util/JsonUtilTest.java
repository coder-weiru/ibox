package ibox.iplanner.api.util;

import ibox.iplanner.api.model.*;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class JsonUtilTest {

    @Test
    public void testTodoToJsonString() {
        Todo todo = Todo.fromActivity(TaskUtil.anyTask());

        String json = JsonUtil.toJsonString(todo);

        assertTrue(json.contains(TimelineAttribute.class.getName()));
        assertTrue(json.contains(TagAttribute.class.getName()));
    }

    @Test
    public void testMeetingToJsonString() {
        Meeting meeting = MeetingUtil.anyMeeting();

        String json = JsonUtil.toJsonString(meeting);

        assertTrue(json.contains(EventAttribute.class.getName()));
        assertTrue(json.contains(LocationAttribute.class.getName()));
        assertTrue(json.contains(TagAttribute.class.getName()));
    }

    @Test
    public void testTaskToJsonString() {
        Task task = TaskUtil.anyTask();

        String json = JsonUtil.toJsonString(task);

        assertTrue(json.contains(TimelineAttribute.class.getName()));
        assertTrue(json.contains(TagAttribute.class.getName()));
    }

    @Test
    public void testActivityToJsonString() {
        Activity activity = ActivityUtil.anyActivity();

        String json = JsonUtil.toJsonString(activity);

        assertTrue(json.contains("\"tags\""));
    }

}

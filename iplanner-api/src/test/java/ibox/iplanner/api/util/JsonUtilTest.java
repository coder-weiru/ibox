package ibox.iplanner.api.util;

import ibox.iplanner.api.model.*;
import ibox.iplanner.api.service.TestHelper;
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

        assertTrue(json.contains("\"attributeSet\""));
    }

    @Test
    public void testTodoAttributeSetToJsonString() {
        AttributeSet attributeSet = new AttributeSet();

        attributeSet.addAttribute(ActivityUtil.anyTagAttribute());
        attributeSet.addAttribute(MeetingUtil.anyEventAttribute());
        attributeSet.addAttribute(MeetingUtil.anyLocationAttribute());
        attributeSet.addAttribute(TaskUtil.anyTimelineAttribute());

        String json = JsonUtil.toJsonString(attributeSet);

        assertTrue(json.contains(TimelineAttribute.class.getName()));
        assertTrue(json.contains(EventAttribute.class.getName()));
        assertTrue(json.contains(LocationAttribute.class.getName()));
        assertTrue(json.contains(TagAttribute.class.getName()));
    }

    @Test
    public void testTodoAttributeSetFromJsonString() {
        AttributeSet attributeSet = new AttributeSet();

        attributeSet.addAttribute(ActivityUtil.anyTagAttribute());
        attributeSet.addAttribute(MeetingUtil.anyEventAttribute());
        attributeSet.addAttribute(MeetingUtil.anyLocationAttribute());
        attributeSet.addAttribute(TaskUtil.anyTimelineAttribute());

        String json = JsonUtil.toJsonString(attributeSet);

        AttributeSet newAttributeSet = JsonUtil.fromJsonString(json, AttributeSet.class);

        TestHelper.verifyTaggingAttributeAreEqual((TagAttribute) attributeSet.getAttribute(TodoFeature.TAGGING_FEATURE), (TagAttribute) newAttributeSet.getAttribute(TodoFeature.TAGGING_FEATURE));
        TestHelper.verifyEventAttributeAreEqual((EventAttribute) attributeSet.getAttribute(TodoFeature.EVENT_FEATURE), (EventAttribute) newAttributeSet.getAttribute(TodoFeature.EVENT_FEATURE));
        TestHelper.verifyLocationAttributeAreEqual((LocationAttribute) attributeSet.getAttribute(TodoFeature.LOCATION_FEATURE), (LocationAttribute) newAttributeSet.getAttribute(TodoFeature.LOCATION_FEATURE));
        TestHelper.verifyTimelineAttributeAreEqual((TimelineAttribute) attributeSet.getAttribute(TodoFeature.TIMELINE_FEATURE), (TimelineAttribute) newAttributeSet.getAttribute(TodoFeature.TIMELINE_FEATURE));
    }
}

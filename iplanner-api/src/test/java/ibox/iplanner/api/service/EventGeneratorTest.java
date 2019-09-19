package ibox.iplanner.api.service;

import ibox.iplanner.api.model.Event;
import ibox.iplanner.api.model.Meeting;
import ibox.iplanner.api.model.Task;
import ibox.iplanner.api.util.MeetingUtil;
import ibox.iplanner.api.util.TaskUtil;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class EventGeneratorTest {

    @Test
    public void givenValidMeeting_shouldGenerateMeeting() {

        Meeting meeting = MeetingUtil.anyMeeting();

        Event event = EventGenerator.generate(meeting);

        assertThat(event.getSummary(), not(isEmptyString()));
        assertThat(event.getDescription(), not(isEmptyString()));
        assertThat(event.getActivity(), not(isEmptyString()));
        assertThat(event.getStatus(), not(isEmptyString()));
        assertThat(event.getCreator(), notNullValue());
        assertThat(event.getCreated(), notNullValue());
        assertThat(event.getUpdated(), notNullValue());
        assertThat(event.getStart(), notNullValue());
        assertThat(event.getEnd(), notNullValue());
        assertThat(event.getEndTimeUnspecified(), is(Boolean.TRUE));

    }

    @Test
    public void givenValidTask_shouldGenerateTask() {

        Task task = TaskUtil.anyTask();

        Event event = EventGenerator.generate(task);

        assertThat(event.getSummary(), not(isEmptyString()));
        assertThat(event.getDescription(), not(isEmptyString()));
        assertThat(event.getActivity(), not(isEmptyString()));
        assertThat(event.getStatus(), not(isEmptyString()));
        assertThat(event.getCreator(), notNullValue());
        assertThat(event.getCreated(), notNullValue());
        assertThat(event.getUpdated(), notNullValue());
        assertThat(event.getStart(), notNullValue());
        assertThat(event.getEndTimeUnspecified(), is(Boolean.FALSE));

    }
}

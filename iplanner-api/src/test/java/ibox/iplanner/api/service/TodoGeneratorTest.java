package ibox.iplanner.api.service;

import ibox.iplanner.api.model.Todo;
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

public class TodoGeneratorTest {

    @Test
    public void givenValidMeeting_shouldGenerateMeeting() {

        Meeting meeting = MeetingUtil.anyMeeting();

        Todo todo = EventGenerator.generate(meeting);

        assertThat(todo.getSummary(), not(isEmptyString()));
        assertThat(todo.getDescription(), not(isEmptyString()));
        assertThat(todo.getActivity(), not(isEmptyString()));
        assertThat(todo.getStatus(), not(isEmptyString()));
        assertThat(todo.getCreator(), notNullValue());
        assertThat(todo.getCreated(), notNullValue());
        assertThat(todo.getUpdated(), notNullValue());
        assertThat(todo.getStart(), notNullValue());
        assertThat(todo.getEnd(), notNullValue());
        assertThat(todo.getEndTimeUnspecified(), is(Boolean.TRUE));

    }

    @Test
    public void givenValidTask_shouldGenerateTask() {

        Task task = TaskUtil.anyTask();

        Todo todo = EventGenerator.generate(task);

        assertThat(todo.getSummary(), not(isEmptyString()));
        assertThat(todo.getDescription(), not(isEmptyString()));
        assertThat(todo.getActivity(), not(isEmptyString()));
        assertThat(todo.getStatus(), not(isEmptyString()));
        assertThat(todo.getCreator(), notNullValue());
        assertThat(todo.getCreated(), notNullValue());
        assertThat(todo.getUpdated(), notNullValue());
        assertThat(todo.getStart(), notNullValue());
        assertThat(todo.getEndTimeUnspecified(), is(Boolean.FALSE));

    }
}

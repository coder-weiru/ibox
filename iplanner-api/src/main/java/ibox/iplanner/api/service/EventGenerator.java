package ibox.iplanner.api.service;

import ibox.iplanner.api.model.*;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.MINUTES;

public class EventGenerator {

    private EventGenerator() { }

    public static Todo generate(Activity activity) {

        if (activity instanceof Meeting) {
            return generate((Meeting) activity);
        }
        else if (activity instanceof Task) {
            return generate((Task) activity);
        }

        return null;
    }

    public static Todo generate(Meeting meeting) {
        Instant now = Instant.now();
        return Todo.builder()
                .summary(String.format("My %s", meeting.getTitle()))
                .description(String.format("%s", meeting.getDescription()))
                .creator(meeting.getCreator())
                .created(now)
                .updated(now)
                .start(now.plus(60, MINUTES))
                .end(now.plus(120, MINUTES))
                .activity(meeting.getId())
                .status(TodoStatus.OPEN.name())
                .endTimeUnspecified(Boolean.TRUE)
                .build();
    }

    public static Todo generate(Task task) {
        Instant now = Instant.now();
        return Todo.builder()
                .summary(String.format("My %s", task.getTitle()))
                .description(String.format("%s", task.getDescription()))
                .creator(task.getCreator())
                .created(now)
                .updated(now)
                .start(now)
                .activity(task.getId())
                .status(TodoStatus.OPEN.name())
                .endTimeUnspecified(Boolean.FALSE)
                .build();
    }

}

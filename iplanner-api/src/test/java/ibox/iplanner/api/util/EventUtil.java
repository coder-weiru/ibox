package ibox.iplanner.api.util;

import ibox.iplanner.api.model.ActivityStatus;
import ibox.iplanner.api.model.Event;
import ibox.iplanner.api.model.EventStatus;
import ibox.iplanner.api.model.User;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.*;

public class EventUtil extends BaseEntityUtil {

    public static String anyEventLocation() {
        return RandomStringUtils.random(20, true, false);
    }

    public static Boolean anyEventEndTimeUnspecified() {
        return randomBoolean();
    }

    public static Set<String> anyEventRecurrence() {
        int size = new Random().nextInt(10);
        Set<String> recurrence = new HashSet<>();
        int i = 0;
        while (i < size) {
            recurrence.add(RandomStringUtils.random(10, true, false));
            i ++;
        }
        return recurrence;
    }

    public static EventStatus anyEventStatus() {
        return Arrays.asList(new EventStatus[] {
                EventStatus.OPEN,
                EventStatus.CLOSED,
                EventStatus.FINISHED
        }).get(new Random().nextInt(3));
    }

    public static User anyEventCreator() {
        return anyUser();
    }

    public static Event anyEvent() {
        Event event = anyEventWithoutId();
        event.setId(anyShortId());
        return event;
    }

    public static Event anyEventWithoutId() {
        Event event = new Event();
        event.setSummary(anySummary());
        event.setDescription(anyDescription());
        event.setStatus(anyEventStatus().name());
        event.setSummary(anySummary());
        event.setActivity(anyActivityId());
        event.setLocation(anyEventLocation());
        event.setRecurrence(anyEventRecurrence());
        event.setEndTimeUnspecified(anyEventEndTimeUnspecified());
        event.setCreated(anyCreatedTime());
        event.setUpdated(anyUpdatedTime());
        event.setStart(anyStartTime());
        event.setEnd(anyEndTime());
        event.setCreator(anyEventCreator());

        return event;
    }

    public static List<Event> anyEventList() {
        int size = new Random().nextInt(10);
        List<Event> eventList = new ArrayList<>();
        int i = 0;
        while (i < size) {
            eventList.add(anyEvent());
            i ++;
        }
        return eventList;
    }
}

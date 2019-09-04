package ibox.iplanner.api.util;

import ibox.iplanner.api.model.Event;
import ibox.iplanner.api.model.User;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.Instant;
import java.util.*;

public class EventUtil {

    public static Boolean randomBoolean() {
        return Math.random() < 0.5;
    }

    public static String anyEventSummary() {
        return RandomStringUtils.random(30, true, false);
    }

    public static String anyEventDescription() {
        return RandomStringUtils.random(100, true, true);
    }

    public static String anyEventStatus() {
        return RandomStringUtils.random(5, true, false);
    }

    public static String anyEventActivity() {
        return RandomStringUtils.random(10, true, false);
    }

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

    public static Instant anyEventCreatedTime() {
        return Instant.now().minusMillis(new Random().nextInt(1000000));
    }

    public static Instant anyEventUpdatedTime() {
        return Instant.now().minusMillis(new Random().nextInt(500000));
    }

    public static Instant anyEventStartTime() {
        return Instant.now().minusMillis(new Random().nextInt(100));
    }

    public static Instant anyEventEndTime() {
        return Instant.now().plusMillis(new Random().nextInt(1000000));
    }

    public static String anyUUID() {
        return UUID.randomUUID().toString();
    }

    public static String anyShortId() {
        return RandomStringUtils.randomAlphanumeric(8);
    }

    public static String anyDisplayName() {
        return RandomStringUtils.random(20, true, false);
    }

    public static String anyEmail() {
        return RandomStringUtils.random(20, true, true) + "@gmail.com";
    }

    public static User anyEventCreator() {
        User creator = new User();
        creator.setId(anyUUID());
        creator.setDisplayName(anyDisplayName());
        creator.setEmail(anyEmail());
        creator.setSelf(randomBoolean());
        return creator;
    }

    public static Event anyEvent() {
        Event event = anyEventWithoutId();
        event.setId(anyShortId());
        return event;
    }

    public static Event anyEventWithoutId() {
        Event event = new Event();
        event.setSummary(anyEventSummary());
        event.setDescription(anyEventDescription());
        event.setStatus(anyEventStatus());
        event.setSummary(anyEventSummary());
        event.setActivity(anyEventActivity());
        event.setLocation(anyEventLocation());
        event.setRecurrence(anyEventRecurrence());
        event.setEndTimeUnspecified(anyEventEndTimeUnspecified());
        event.setCreated(anyEventCreatedTime());
        event.setUpdated(anyEventUpdatedTime());
        event.setStart(anyEventStartTime());
        event.setEnd(anyEventEndTime());
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

package ibox.iplanner.api.service;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import ibox.iplanner.api.model.Event;
import ibox.iplanner.api.model.User;
import ibox.iplanner.api.util.EventUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class EventDataServiceIntegrationTest extends LocalDynamoDBIntegrationTestSupport {

    private static EventDataService eventDataService;

    @BeforeClass
    public static void setup() {
        dynamoDBSetup.createEventTable(10L, 5L);

        eventDataService = new EventDataService(new DynamoDB(amazonDynamoDB));
    }

    @Test
    public void givenValidEvent_addEvent_shouldCreateRecord() {

        Event event = EventUtil.anyEvent();

        eventDataService.addEvent(event);

        Event dbEvent = eventDataService.getEvent(event.getId());

        verifyEventsAreEqual(event, dbEvent);
    }

    @Test
    public void givenValidEvents_addEvents_shouldCreateRecords() {

        List<Event> events = EventUtil.anyEventList();

        eventDataService.addEvents(events);

        events.stream().forEach(e -> {
            String id = e.getId();

            Event dbEvent = eventDataService.getEvent(e.getId());

            verifyEventsAreEqual(e, dbEvent);

        });
    }

    @Test
    public void givenEventsWithCreators_getMyEventsWithinTime_shouldReturnOnlyCreatorEventsWithinTime() {
        User creator1 = EventUtil.anyEventCreator();
        User creator2 = EventUtil.anyEventCreator();

        Instant now = Instant.now();

        Event event1 = EventUtil.anyEvent();
        event1.setCreator(creator1);
        event1.setStart(now);

        Event event2 = EventUtil.anyEvent();
        event2.setCreator(creator1);
        event2.setStart(now.plus(10, MINUTES));

        Event event3 = EventUtil.anyEvent();
        event3.setCreator(creator1);
        event3.setStart(now.plus(15, MINUTES));

        Event event4 = EventUtil.anyEvent();
        event4.setCreator(creator2);
        event4.setStart(now.plus(20, MINUTES));

        Event event5 = EventUtil.anyEvent();
        event5.setCreator(creator1);
        event5.setStart(now.plus(30, MINUTES));

        Event event6 = EventUtil.anyEvent();
        event6.setCreator(creator1);
        event6.setStart(now.plus(40, MINUTES));

        List<Event> events = Arrays.asList( new Event[] {event1, event2, event3, event4, event5, event6});

        eventDataService.addEvents(events);

        Instant timeWindowStart = now.plus(5, MINUTES);
        Instant timeWindowEnd = now.plus(35, MINUTES);

        List<Event> myEvents = eventDataService.getMyEventsWithinTime(creator1.getId(), timeWindowStart, timeWindowEnd, null);

        assertThat(myEvents.size(), is(equalTo(3)));


    }

    private void verifyEventsAreEqual(Event expected, Event actual) {

        assertThat(expected.getId(), is(equalTo(actual.getId())));
        assertThat(expected.getSummary(), is(equalTo(actual.getSummary())));
        assertThat(expected.getDescription(), is(equalTo(actual.getDescription())));
        assertThat(expected.getCreator().getId(), is(equalTo(actual.getCreator().getId())));
        assertThat(expected.getCreator().getDisplayName(), is(equalTo(actual.getCreator().getDisplayName())));
        assertThat(expected.getCreator().getEmail(), is(equalTo(actual.getCreator().getEmail())));
        assertThat(expected.getCreator().getSelf(), is(equalTo(actual.getCreator().getSelf())));
        assertThat(expected.getCreated(), is(equalTo(actual.getCreated())));
        assertThat(expected.getUpdated(), is(equalTo(actual.getUpdated())));
        assertThat(expected.getStart(), is(equalTo(actual.getStart())));
        assertThat(expected.getEnd(), is(equalTo(actual.getEnd())));
        assertThat(expected.getActivity(), is(equalTo(actual.getActivity())));
        assertThat(expected.getStatus(), is(equalTo(actual.getStatus())));
        assertThat(expected.getLocation(), is(equalTo(actual.getLocation())));
        assertThat(expected.getEndTimeUnspecified(), is(equalTo(actual.getEndTimeUnspecified())));

        expected.getRecurrence().stream().forEach(s-> actual.getRecurrence().contains(s));
    }

}

package ibox.iplanner.api.service;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import ibox.iplanner.api.model.Event;
import ibox.iplanner.api.model.EventStatus;
import ibox.iplanner.api.model.User;
import ibox.iplanner.api.model.updatable.Updatable;
import ibox.iplanner.api.model.updatable.UpdatableAttribute;
import ibox.iplanner.api.model.updatable.UpdatableKey;
import ibox.iplanner.api.model.updatable.UpdateAction;
import ibox.iplanner.api.util.EventUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ibox.iplanner.api.service.dbmodel.EventDefinition.*;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.hamcrest.CoreMatchers.hasItem;
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
    public void givenValidUpdatable_updateEvent_shouldUpdateRecord() {

        Event activity = EventUtil.anyEvent();

        eventDataService.addEvent(activity);

        Event dbEvent = eventDataService.getEvent(activity.getId());

        String newSummary = "new summary";
        String newDescription = "new description";
        String newLocation = "new location";
        String newActivity = "new activity";
        Set<String> newRecurrence = new HashSet<>();
        newRecurrence.add("abc");
        Set<UpdatableAttribute> updatableAttributeSet = new HashSet<>();
        updatableAttributeSet.add( UpdatableAttribute.builder()
                .attributeName(FIELD_NAME_SUMMARY)
                .action(UpdateAction.UPDATE)
                .value(newSummary)
                .build());
        updatableAttributeSet.add( UpdatableAttribute.builder()
                .attributeName(FIELD_NAME_DESCRIPTION)
                .action(UpdateAction.UPDATE)
                .value(newDescription)
                .build());
        updatableAttributeSet.add( UpdatableAttribute.builder()
                .attributeName(FIELD_NAME_ACTIVITY)
                .action(UpdateAction.UPDATE)
                .value(newActivity)
                .build());
        updatableAttributeSet.add( UpdatableAttribute.builder()
                .attributeName(FIELD_NAME_EVENT_LOCATION)
                .action(UpdateAction.UPDATE)
                .value(newLocation)
                .build());
        updatableAttributeSet.add( UpdatableAttribute.builder()
                .attributeName(FIELD_NAME_EVENT_RECURRENCE)
                .action(UpdateAction.UPDATE)
                .value(newRecurrence)
                .build());

        Updatable updatable = Updatable.builder()
                .objectType("event")
                .primaryKey(new UpdatableKey()
                        .addComponent(FIELD_NAME_ID, dbEvent.getId()))
                .updatableAttributes(updatableAttributeSet)
                .build();

        Event updated = eventDataService.updateEvent(updatable);

        assertThat(updated.getSummary(), is(equalTo(newSummary)));
        assertThat(updated.getDescription(), is(equalTo(newDescription)));
        assertThat(updated.getActivity(), is(equalTo(newActivity)));
        assertThat(updated.getLocation(), is(equalTo(newLocation)));
        assertThat(updated.getRecurrence(), hasItem("abc"));
    }

    @Test
    public void givenValidId_deleteEvent_shouldUpdateEventStatus() {

        Event event = EventUtil.anyEvent();
        event.setStatus(EventStatus.OPEN.name());

        eventDataService.addEvent(event);

        Event dbEvent = eventDataService.getEvent(event.getId());

        Event deleted = eventDataService.deleteEvent(dbEvent.getId());

        assertThat(deleted.getStatus(), is(equalTo(EventStatus.CLOSED.name())));

        Event theEvent = eventDataService.getEvent(dbEvent.getId());

        assertThat(theEvent.getStatus(), is(equalTo(EventStatus.CLOSED.name())));

    }

    @Test(expected = AmazonDynamoDBException.class)
    public void givenValidUpdatable_updateEvent_shouldNotUpdateKeyField() {

        Event event = EventUtil.anyEvent();

        eventDataService.addEvent(event);

        Event dbEvent = eventDataService.getEvent(event.getId());

        Set<UpdatableAttribute> updatableAttributeSet = new HashSet<>();
        updatableAttributeSet.add( UpdatableAttribute.builder()
                .attributeName(FIELD_NAME_ID)
                .action(UpdateAction.UPDATE)
                .value("1234567890")
                .build());
        Updatable updatable = Updatable.builder()
                .objectType("event")
                .primaryKey(new UpdatableKey()
                        .addComponent(FIELD_NAME_ID, dbEvent.getId()))
                .updatableAttributes(updatableAttributeSet)
                .build();

        Event updated = eventDataService.updateEvent(updatable);
    }

    @Test
    public void givenEventsWithCreators_getMyEventsWithinTime_shouldReturnOnlyCreatorEventsWithinTime() {
        User creator1 = EventUtil.anyEventCreator();
        User creator2 = EventUtil.anyEventCreator();

        Instant now = Instant.now();

        Event event1 = EventUtil.anyEvent();
        event1.setCreator(creator1);
        event1.setStart(now);
        event1.setStatus(EventStatus.OPEN.name());

        Event event2 = EventUtil.anyEvent();
        event2.setCreator(creator1);
        event2.setStart(now.plus(10, MINUTES));
        event2.setStatus(EventStatus.OPEN.name());

        Event event3 = EventUtil.anyEvent();
        event3.setCreator(creator1);
        event3.setStart(now.plus(15, MINUTES));
        event3.setStatus(EventStatus.OPEN.name());

        Event event4 = EventUtil.anyEvent();
        event4.setCreator(creator2);
        event4.setStart(now.plus(20, MINUTES));
        event4.setStatus(EventStatus.OPEN.name());

        Event event5 = EventUtil.anyEvent();
        event5.setCreator(creator1);
        event5.setStart(now.plus(30, MINUTES));
        event5.setStatus(EventStatus.OPEN.name());

        Event event6 = EventUtil.anyEvent();
        event6.setCreator(creator1);
        event6.setStart(now.plus(40, MINUTES));
        event6.setStatus(EventStatus.OPEN.name());

        List<Event> events = Arrays.asList( new Event[] {event1, event2, event3, event4, event5, event6});

        eventDataService.addEvents(events);

        Instant timeWindowStart = now.plus(5, MINUTES);
        Instant timeWindowEnd = now.plus(35, MINUTES);

        List<Event> myEvents = eventDataService.getMyEventsWithinTime(creator1.getId(), timeWindowStart, timeWindowEnd, EventStatus.OPEN.name(),null);

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

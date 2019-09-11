package ibox.iplanner.api.integration;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import ibox.iplanner.api.lambda.runtime.TestContext;
import ibox.iplanner.api.model.Event;
import ibox.iplanner.api.model.EventStatus;
import ibox.iplanner.api.model.User;
import ibox.iplanner.api.model.updatable.Updatable;
import ibox.iplanner.api.model.updatable.UpdatableAttribute;
import ibox.iplanner.api.model.updatable.UpdatableKey;
import ibox.iplanner.api.model.updatable.UpdateAction;
import ibox.iplanner.api.service.LocalDynamoDBIntegrationTestSupport;
import ibox.iplanner.api.service.dbmodel.EventDefinition;
import ibox.iplanner.api.util.EventUtil;
import ibox.iplanner.api.util.JsonUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Instant;
import java.util.*;

import static ibox.iplanner.api.service.dbmodel.EventDefinition.*;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EventHandlerIntegrationTest extends LocalDynamoDBIntegrationTestSupport {

    private AddEventHandlerTestWrapper addEventHandler = new AddEventHandlerTestWrapper();
    private GetEventHandlerTestWrapper getEventHandler = new GetEventHandlerTestWrapper();
    private ListEventHandlerTestWrapper listEventHandler = new ListEventHandlerTestWrapper();
    private UpdateEventHandlerTestWrapper updateEventHandler = new UpdateEventHandlerTestWrapper();
    private DeleteEventHandlerTestWrapper deleteEventHandler = new DeleteEventHandlerTestWrapper();

    private TestContext testContext = TestContext.builder().build();

    @BeforeClass
    public static void setup() {
        dynamoDBSetup.createEventTable(10L, 5L);
    }

    @Test
    public void givenValidEvent_addEvent_shouldCreateRecord() {

        Event event = EventUtil.anyEvent();

        Event added = addEvent(event);

        Event getEvent = getEvent(added.getId());

        verifyEventsAreEqual(event, getEvent);
    }

    @Test
    public void givenValidEvents_addEvents_shouldCreateRecords() {

        List<Event> events = EventUtil.anyEventList();

        List<Event> added = addEvents(events);

        added.stream().forEach(activity -> {
            Event getEvent = getEvent(activity.getId());

            verifyEventsAreEqual(activity, getEvent);

        });
    }

    @Test
    public void givenValidUpdatable_updateEvent_shouldUpdateRecord() throws InterruptedException {

        Event added = addEvent(EventUtil.anyEvent());

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
                        .addComponent(FIELD_NAME_ID, added.getId()))
                .updatableAttributes(updatableAttributeSet)
                .build();

        updateEvent(added.getId(), updatable);

        Event updated = getEvent(added.getId());

        assertThat(updated.getSummary(), is(equalTo(newSummary)));
        assertThat(updated.getDescription(), is(equalTo(newDescription)));
        assertThat(updated.getActivity(), is(equalTo(newActivity)));
        assertThat(updated.getLocation(), is(equalTo(newLocation)));
        assertThat(updated.getRecurrence(), hasItem("abc"));

        assertThat(updated.getCreator().getId(), is(equalTo(added.getCreator().getId())));
        assertThat(updated.getCreator().getDisplayName(), is(equalTo(added.getCreator().getDisplayName())));
        assertThat(updated.getCreator().getEmail(), is(equalTo(added.getCreator().getEmail())));
        assertThat(updated.getCreator().getSelf(), is(equalTo(added.getCreator().getSelf())));
        assertThat(updated.getCreated(), is(equalTo(added.getCreated())));
        assertThat(updated.getUpdated(), is(equalTo(added.getUpdated())));
        assertThat(updated.getStart(), is(equalTo(added.getStart())));
        assertThat(updated.getEnd(), is(equalTo(added.getEnd())));
        assertThat(updated.getStatus(), is(equalTo(added.getStatus())));
        assertThat(updated.getEndTimeUnspecified(), is(equalTo(added.getEndTimeUnspecified())));

    }

    @Test
    public void givenValidId_deleteEvent_shouldUpdateEventStatus() {

        Event added = addEvent(EventUtil.anyEvent());

        deleteEvent(added.getId());

        Event deleted = getEvent(added.getId());

        assertThat(deleted.getStatus(), is(equalTo(EventStatus.CLOSED.name())));

    }

    @Test
    public void givenValidUpdatable_updateEvent_shouldNotUpdateKeyField() {

        Event added = addEvent(EventUtil.anyEvent());

        Set<UpdatableAttribute> updatableAttributeSet = new HashSet<>();
        updatableAttributeSet.add( UpdatableAttribute.builder()
                .attributeName(EventDefinition.FIELD_NAME_ID)
                .action(UpdateAction.UPDATE)
                .value("1234567890")
                .build());
        Updatable updatable = Updatable.builder()
                .objectType("event")
                .primaryKey(new UpdatableKey()
                        .addComponent(EventDefinition.FIELD_NAME_ID, added.getId()))
                .updatableAttributes(updatableAttributeSet)
                .build();

        updateEventResultInInternalServerError(added.getId(), updatable);
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
        event5.setStatus(EventStatus.FINISHED.name());

        Event event6 = EventUtil.anyEvent();
        event6.setCreator(creator1);
        event6.setStart(now.plus(40, MINUTES));
        event6.setStatus(EventStatus.OPEN.name());

        List<Event> events = Arrays.asList( new Event[] {event1, event2, event3, event4, event5, event6});

        addEvents(events);

        Instant timeWindowStart = now.plus(5, MINUTES);
        Instant timeWindowEnd = now.plus(35, MINUTES);

        List<Event> listEvents = listEvents(creator1.getId(), timeWindowStart, timeWindowEnd, null, null);

        assertThat(listEvents.size(), is(equalTo(2)));

        verifyEventsAreEqual(event2, listEvents.get(0));
        verifyEventsAreEqual(event3, listEvents.get(1));

    }

    private Event addEvent(Event event) {
        APIGatewayProxyRequestEvent addRequestEvent = new APIGatewayProxyRequestEvent();
        addRequestEvent.setBody(JsonUtil.toJsonString(Arrays.asList( new Event[] { event})));
        APIGatewayProxyResponseEvent addResponseEvent = addEventHandler.handleRequest(addRequestEvent, testContext);

        assertEquals(200, addResponseEvent.getStatusCode());

        List<Event> added = (List<Event>) JsonUtil.fromJsonString(addResponseEvent.getBody(), List.class, Event.class);

        return added.get(0);
    }

    private List<Event> addEvents(List<Event> activities) {
        APIGatewayProxyRequestEvent addRequestEvent = new APIGatewayProxyRequestEvent();
        addRequestEvent.setBody(JsonUtil.toJsonString(activities));
        APIGatewayProxyResponseEvent addResponseEvent = addEventHandler.handleRequest(addRequestEvent, testContext);

        assertEquals(200, addResponseEvent.getStatusCode());

        List<Event> added = (List<Event>) JsonUtil.fromJsonString(addResponseEvent.getBody(), List.class, Event.class);

        return added;
    }

    private Event getEvent(String eventId) {
        APIGatewayProxyRequestEvent getRequestEvent = new APIGatewayProxyRequestEvent();
        getRequestEvent.setPathParameters(Collections.singletonMap("eventId", eventId));
        APIGatewayProxyResponseEvent getResponseEvent = getEventHandler.handleRequest(getRequestEvent, testContext);

        assertEquals(200, getResponseEvent.getStatusCode());

        Event added = JsonUtil.fromJsonString(getResponseEvent.getBody(), Event.class);

        return added;
    }

    private void deleteEvent(String eventId) {
        APIGatewayProxyRequestEvent deleteRequestEvent = new APIGatewayProxyRequestEvent();
        deleteRequestEvent.setPathParameters(Collections.singletonMap("eventId", eventId));
        APIGatewayProxyResponseEvent deleteResponseEvent = deleteEventHandler.handleRequest(deleteRequestEvent, testContext);

        assertEquals(200, deleteResponseEvent.getStatusCode());
    }

    private void updateEvent(String eventId, Updatable updatable) {
        APIGatewayProxyRequestEvent updateRequestEvent = new APIGatewayProxyRequestEvent();
        updateRequestEvent.setPathParameters(Collections.singletonMap("eventId", eventId));
        updateRequestEvent.setBody(JsonUtil.toJsonString(updatable));
        APIGatewayProxyResponseEvent updateResponseEvent = updateEventHandler.handleRequest(updateRequestEvent, testContext);

        assertEquals(200, updateResponseEvent.getStatusCode());
    }

    private void updateEventResultInInternalServerError(String eventId, Updatable updatable) {
        APIGatewayProxyRequestEvent updateRequestEvent = new APIGatewayProxyRequestEvent();
        updateRequestEvent.setPathParameters(Collections.singletonMap("eventId", eventId));
        updateRequestEvent.setBody(JsonUtil.toJsonString(updatable));
        APIGatewayProxyResponseEvent updateResponseEvent = updateEventHandler.handleRequest(updateRequestEvent, testContext);

        assertEquals(500, updateResponseEvent.getStatusCode());
    }

    private List<Event> listEvents(String creatorId, Instant timeWindowStart, Instant timeWindowEnd, EventStatus status, Integer limit) {
        APIGatewayProxyRequestEvent listRequestEvent = new APIGatewayProxyRequestEvent();
        listRequestEvent.setPathParameters(Collections.singletonMap("creatorId", creatorId));
        Map<String, String> requestParams = new HashMap<>();
        if (Optional.ofNullable(timeWindowStart).isPresent()) {
            requestParams.put("start", timeWindowStart.toString());
        }
        if (Optional.ofNullable(timeWindowEnd).isPresent()) {
            requestParams.put("end", timeWindowEnd.toString());
        }
        if (Optional.ofNullable(status).isPresent()) {
            requestParams.put("status", status.name());
        }
        if (Optional.ofNullable(limit).isPresent()) {
            requestParams.put("limit", limit.toString());
        }
        listRequestEvent.setQueryStringParameters(requestParams);

        APIGatewayProxyResponseEvent getResponseEvent = listEventHandler.handleRequest(listRequestEvent, testContext);

        assertEquals(200, getResponseEvent.getStatusCode());

        List<Event> listEvents = (List<Event>)JsonUtil.fromJsonString(getResponseEvent.getBody(), List.class, Event.class);

        return listEvents;
    }

    private void verifyEventsAreEqual(Event expected, Event actual) {

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

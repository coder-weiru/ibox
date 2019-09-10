package ibox.iplanner.api.integration;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import ibox.iplanner.api.lambda.runtime.TestContext;
import ibox.iplanner.api.model.Event;
import ibox.iplanner.api.model.EventStatus;
import ibox.iplanner.api.model.User;
import ibox.iplanner.api.service.LocalDynamoDBIntegrationTestSupport;
import ibox.iplanner.api.util.EventUtil;
import ibox.iplanner.api.util.JsonUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Instant;
import java.util.*;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EventHandlerIntegrationTest extends LocalDynamoDBIntegrationTestSupport {

    private AddEventHandlerTestWrapper addEventHandler = new AddEventHandlerTestWrapper();
    private GetEventHandlerTestWrapper getEventHandler = new GetEventHandlerTestWrapper();
    private ListEventHandlerTestWrapper listEventHandler = new ListEventHandlerTestWrapper();

    private TestContext testContext = TestContext.builder().build();

    @BeforeClass
    public static void setup() {
        dynamoDBSetup.createEventTable(10L, 5L);
    }

    @Test
    public void givenValidEvent_addEvent_shouldCreateRecord() {

        Event event = EventUtil.anyEvent();

        APIGatewayProxyRequestEvent addRequestEvent = new APIGatewayProxyRequestEvent();
        addRequestEvent.setBody(JsonUtil.toJsonString(Arrays.asList( new Event[] { event })));
        APIGatewayProxyResponseEvent addResponseEvent = addEventHandler.handleRequest(addRequestEvent, testContext);

        assertEquals(200, addResponseEvent.getStatusCode());

        List<Event> added = (List<Event>) JsonUtil.fromJsonString(addResponseEvent.getBody(), List.class, Event.class);
        APIGatewayProxyRequestEvent getRequestEvent = new APIGatewayProxyRequestEvent();
        getRequestEvent.setPathParameters(Collections.singletonMap("eventId", added.get(0).getId()));
        APIGatewayProxyResponseEvent getResponseEvent = getEventHandler.handleRequest(getRequestEvent, testContext);

        assertEquals(200, getResponseEvent.getStatusCode());

        Event getEvent = JsonUtil.fromJsonString(getResponseEvent.getBody(), Event.class);

        verifyEventsAreEqual(event, getEvent);
    }

    @Test
    public void givenValidEvents_addEvents_shouldCreateRecords() {

        List<Event> events = EventUtil.anyEventList();

        APIGatewayProxyRequestEvent addRequestEvent = new APIGatewayProxyRequestEvent();
        addRequestEvent.setBody(JsonUtil.toJsonString(events));
        APIGatewayProxyResponseEvent addResponseEvent = addEventHandler.handleRequest(addRequestEvent, testContext);

        assertEquals(200, addResponseEvent.getStatusCode());

        List<Event> added = (List<Event>) JsonUtil.fromJsonString(addResponseEvent.getBody(), List.class, Event.class);

        APIGatewayProxyRequestEvent getRequestEvent = new APIGatewayProxyRequestEvent();

        added.stream().forEach(event -> {
            String id = event.getId();

            getRequestEvent.setPathParameters(Collections.singletonMap("eventId", event.getId()));
            APIGatewayProxyResponseEvent getResponseEvent = getEventHandler.handleRequest(getRequestEvent, testContext);

            assertEquals(200, getResponseEvent.getStatusCode());

            Event getEvent = JsonUtil.fromJsonString(getResponseEvent.getBody(), Event.class);

            verifyEventsAreEqual(event, getEvent);

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

        APIGatewayProxyRequestEvent addRequestEvent = new APIGatewayProxyRequestEvent();
        addRequestEvent.setBody(JsonUtil.toJsonString(events));
        APIGatewayProxyResponseEvent addResponseEvent = addEventHandler.handleRequest(addRequestEvent, testContext);

        assertEquals(200, addResponseEvent.getStatusCode());

        APIGatewayProxyRequestEvent getRequestEvent = new APIGatewayProxyRequestEvent();

        Instant timeWindowStart = now.plus(5, MINUTES);
        Instant timeWindowEnd = now.plus(35, MINUTES);

        APIGatewayProxyRequestEvent listRequestEvent = new APIGatewayProxyRequestEvent();
        listRequestEvent.setPathParameters(Collections.singletonMap("creatorId", creator1.getId()));
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("start", timeWindowStart.toString());
        requestParams.put("end", timeWindowEnd.toString());
        listRequestEvent.setQueryStringParameters(requestParams);

        APIGatewayProxyResponseEvent getResponseEvent = listEventHandler.handleRequest(listRequestEvent, testContext);

        assertEquals(200, getResponseEvent.getStatusCode());

        List<Event> listEvents = (List<Event>)JsonUtil.fromJsonString(getResponseEvent.getBody(), List.class, Event.class);

        assertThat(listEvents.size(), is(equalTo(2)));

        verifyEventsAreEqual(event2, listEvents.get(0));
        verifyEventsAreEqual(event3, listEvents.get(1));

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

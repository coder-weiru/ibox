package ibox.iplanner.api.lambda.handler;

import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.util.StringUtils;
import ibox.iplanner.api.config.DaggerIPlannerComponent;
import ibox.iplanner.api.config.IPlannerComponent;
import ibox.iplanner.api.lambda.runtime.TestContext;
import ibox.iplanner.api.model.ApiError;
import ibox.iplanner.api.model.Event;
import ibox.iplanner.api.model.EventStatus;
import ibox.iplanner.api.service.EventDataService;
import ibox.iplanner.api.util.EventUtil;
import ibox.iplanner.api.util.JsonUtil;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.Instant;
import java.util.*;

import static ibox.iplanner.api.util.ApiErrorConstants.*;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ListEventHandlerTest {

    @InjectMocks
    private ListEventHandler handler = new ListEventHandler();

    private List<Event> events;

    @Mock
    private EventDataService eventDataServiceMock;

    public ListEventHandlerTest() {
        IPlannerComponent iPlannerComponent = DaggerIPlannerComponent.builder().build();
        iPlannerComponent.inject(handler);
    }

    @Before
    public void setUp() {
        this.events = Arrays.asList(new Event[]{
                EventUtil.anyEvent(),
                EventUtil.anyEvent(),
                EventUtil.anyEvent()
        });
    }

    @Test
    public void listEvents_shouldInvokeEventDateServiceGivenCorrectParams() throws Exception {
        when(eventDataServiceMock.getMyEventsWithinTime(any(String.class), any(Instant.class), any(Instant.class), any(String.class), any(Integer.class))).thenReturn(events);

        Instant now = Instant.now();
        String creatorId = UUID.randomUUID().toString();
        String windowStart = now.plus(1, MINUTES).toString();
        String windowEnd = now.plus(100, MINUTES).toString();
        String status = EventStatus.OPEN.name();
        String limit = "10";

        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setPathParameters(Collections.singletonMap("creatorId", creatorId));
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("start", windowStart);
        requestParams.put("end", windowEnd);
        requestParams.put("status", status);
        requestParams.put("limit", limit);
        requestEvent.setQueryStringParameters(requestParams);

        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(200, responseEvent.getStatusCode());

        ArgumentCaptor<String> eventIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Instant> windowStartCaptor = ArgumentCaptor.forClass(Instant.class);
        ArgumentCaptor<Instant> windowEndCaptor = ArgumentCaptor.forClass(Instant.class);
        ArgumentCaptor<String> statusCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> limitCaptor = ArgumentCaptor.forClass(Integer.class);

        verify(eventDataServiceMock, times(1)).getMyEventsWithinTime(eventIdCaptor.capture(), windowStartCaptor.capture(), windowEndCaptor.capture(), statusCaptor.capture(), limitCaptor.capture());

        verifyNoMoreInteractions(eventDataServiceMock);

        assertThat(eventIdCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(creatorId)));
        assertThat(windowStartCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(Instant.parse(windowStart))));
        assertThat(windowEndCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(Instant.parse(windowEnd))));
        assertThat(statusCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(status)));
        assertThat(limitCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(Integer.valueOf(limit))));
    }

    @Test
    public void listEvents_shouldInvokeEventDateServiceWithSpecifiedEvenStatus() throws Exception {
        List<Event> events = Arrays.asList(new Event[]{
                EventUtil.anyEvent(),
                EventUtil.anyEvent(),
                EventUtil.anyEvent()
        });

        when(eventDataServiceMock.getMyEventsWithinTime(any(String.class), any(Instant.class), any(Instant.class), any(String.class), any(Integer.class))).thenReturn(events);

        Instant now = Instant.now();
        String creatorId = UUID.randomUUID().toString();
        String windowStart = now.plus(1, MINUTES).toString();
        String windowEnd = now.plus(100, MINUTES).toString();
        String status = EventStatus.FINISHED.name();

        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setPathParameters(Collections.singletonMap("creatorId", creatorId));
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("start", windowStart);
        requestParams.put("end", windowEnd);
        requestParams.put("status", status);
        requestEvent.setQueryStringParameters(requestParams);

        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(200, responseEvent.getStatusCode());

        ArgumentCaptor<String> eventIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Instant> windowStartCaptor = ArgumentCaptor.forClass(Instant.class);
        ArgumentCaptor<Instant> windowEndCaptor = ArgumentCaptor.forClass(Instant.class);
        ArgumentCaptor<String> statusCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> limitCaptor = ArgumentCaptor.forClass(Integer.class);

        verify(eventDataServiceMock, times(1)).getMyEventsWithinTime(eventIdCaptor.capture(), windowStartCaptor.capture(), windowEndCaptor.capture(), statusCaptor.capture(), limitCaptor.capture());

        verifyNoMoreInteractions(eventDataServiceMock);

        assertThat(eventIdCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(creatorId)));
        assertThat(windowStartCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(Instant.parse(windowStart))));
        assertThat(windowEndCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(Instant.parse(windowEnd))));
        assertThat(statusCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(EventStatus.FINISHED.name())));
        assertThat(limitCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(Integer.valueOf(100))));

    }

    @Test
    public void listEvents_shouldReturnBadRequestMessageIfCreatorIdInvalid() throws Exception {
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setPathParameters(Collections.singletonMap("creatorId", "abc"));
        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(400, responseEvent.getStatusCode());

        ApiError error = JsonUtil.fromJsonString(responseEvent.getBody(), ApiError.class);
        assertEquals(400, error.getStatus());
        assertEquals(ERROR_BAD_REQUEST, error.getError());
        assertFalse(StringUtils.isNullOrEmpty(error.getMessage()));
        assertFalse(error.getErrorDetails().isEmpty());
    }

    @Test
    public void listEvents_shouldInvokeEventDateServiceEvenStartIsNotSpecified() throws Exception {
        List<Event> events = Arrays.asList(new Event[]{
                EventUtil.anyEvent(),
                EventUtil.anyEvent(),
                EventUtil.anyEvent()
        });

        when(eventDataServiceMock.getMyEventsWithinTime(any(String.class), any(Instant.class), any(Instant.class), any(String.class), any(Integer.class))).thenReturn(events);

        Instant now = Instant.now();
        String creatorId = UUID.randomUUID().toString();
        String windowEnd = now.plus(100, MINUTES).toString();
        String limit = "10";

        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setPathParameters(Collections.singletonMap("creatorId", creatorId));
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("end", windowEnd);
        requestParams.put("limit", limit);
        requestEvent.setQueryStringParameters(requestParams);

        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(200, responseEvent.getStatusCode());

        ArgumentCaptor<String> eventIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Instant> windowStartCaptor = ArgumentCaptor.forClass(Instant.class);
        ArgumentCaptor<Instant> windowEndCaptor = ArgumentCaptor.forClass(Instant.class);
        ArgumentCaptor<String> statusCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> limitCaptor = ArgumentCaptor.forClass(Integer.class);

        verify(eventDataServiceMock, times(1)).getMyEventsWithinTime(eventIdCaptor.capture(), windowStartCaptor.capture(), windowEndCaptor.capture(), statusCaptor.capture(), limitCaptor.capture());

        verifyNoMoreInteractions(eventDataServiceMock);

        assertThat(eventIdCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(creatorId)));
        assertThat(windowStartCaptor.getValue(), CoreMatchers.notNullValue());
        assertThat(windowEndCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(Instant.parse(windowEnd))));
        assertThat(statusCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(EventStatus.OPEN.name())));
        assertThat(limitCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(Integer.valueOf(limit))));
    }

    @Test
    public void listEvents_shouldInvokeEventDateServiceEvenEndIsNotSpecified() throws Exception {
        List<Event> events = Arrays.asList(new Event[]{
                EventUtil.anyEvent(),
                EventUtil.anyEvent(),
                EventUtil.anyEvent()
        });

        when(eventDataServiceMock.getMyEventsWithinTime(any(String.class), any(Instant.class), any(Instant.class), any(String.class), any(Integer.class))).thenReturn(events);

        Instant now = Instant.now();
        String creatorId = UUID.randomUUID().toString();
        String windowStart = now.plus(1, MINUTES).toString();
        String limit = "10";

        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setPathParameters(Collections.singletonMap("creatorId", creatorId));
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("start", windowStart);
        requestParams.put("limit", limit);
        requestEvent.setQueryStringParameters(requestParams);

        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(200, responseEvent.getStatusCode());

        ArgumentCaptor<String> eventIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Instant> windowStartCaptor = ArgumentCaptor.forClass(Instant.class);
        ArgumentCaptor<Instant> windowEndCaptor = ArgumentCaptor.forClass(Instant.class);
        ArgumentCaptor<String> statusCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> limitCaptor = ArgumentCaptor.forClass(Integer.class);

        verify(eventDataServiceMock, times(1)).getMyEventsWithinTime(eventIdCaptor.capture(), windowStartCaptor.capture(), windowEndCaptor.capture(), statusCaptor.capture(), limitCaptor.capture());

        verifyNoMoreInteractions(eventDataServiceMock);

        assertThat(eventIdCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(creatorId)));
        assertThat(windowStartCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(Instant.parse(windowStart))));
        assertThat(windowEndCaptor.getValue(), CoreMatchers.notNullValue());
        assertThat(statusCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(EventStatus.OPEN.name())));
        assertThat(limitCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(Integer.valueOf(limit))));
    }

    @Test
    public void listEvents_shouldInvokeEventDateServiceEvenLimitIsNotSpecified() throws Exception {
        List<Event> events = Arrays.asList(new Event[]{
                EventUtil.anyEvent(),
                EventUtil.anyEvent(),
                EventUtil.anyEvent()
        });

        when(eventDataServiceMock.getMyEventsWithinTime(any(String.class), any(Instant.class), any(Instant.class), any(String.class), any(Integer.class))).thenReturn(events);

        Instant now = Instant.now();
        String creatorId = UUID.randomUUID().toString();
        String windowStart = now.plus(1, MINUTES).toString();
        String windowEnd = now.plus(100, MINUTES).toString();

        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setPathParameters(Collections.singletonMap("creatorId", creatorId));
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("start", windowStart);
        requestParams.put("end", windowEnd);
        requestEvent.setQueryStringParameters(requestParams);

        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(200, responseEvent.getStatusCode());

        ArgumentCaptor<String> eventIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Instant> windowStartCaptor = ArgumentCaptor.forClass(Instant.class);
        ArgumentCaptor<Instant> windowEndCaptor = ArgumentCaptor.forClass(Instant.class);
        ArgumentCaptor<String> statusCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> limitCaptor = ArgumentCaptor.forClass(Integer.class);

        verify(eventDataServiceMock, times(1)).getMyEventsWithinTime(eventIdCaptor.capture(), windowStartCaptor.capture(), windowEndCaptor.capture(), statusCaptor.capture(), limitCaptor.capture());

        verifyNoMoreInteractions(eventDataServiceMock);

        assertThat(eventIdCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(creatorId)));
        assertThat(windowStartCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(Instant.parse(windowStart))));
        assertThat(windowEndCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(Instant.parse(windowEnd))));
        assertThat(statusCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(EventStatus.OPEN.name())));
        assertThat(limitCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(Integer.valueOf(100))));

    }

    @Test
    public void listEvents_shouldReturnInternalServerErrorMessageIfAmazonServiceExceptionIsThrown() throws Exception {
        Event event = EventUtil.anyEvent();
        event.setId(UUID.randomUUID().toString());
        event.getCreator().setId(UUID.randomUUID().toString());

        AmazonDynamoDBException amazonDynamoDBException = new AmazonDynamoDBException("dynamo db error");
        amazonDynamoDBException.setStatusCode(SC_NOT_FOUND);
        amazonDynamoDBException.setErrorCode("AWSERR");
        amazonDynamoDBException.setServiceName("QueryItem");
        amazonDynamoDBException.setRequestId("request1");
        amazonDynamoDBException.setErrorMessage("error message");

        doThrow(amazonDynamoDBException).when(eventDataServiceMock).getMyEventsWithinTime(any(String.class), any(Instant.class), any(Instant.class), any(String.class), any(Integer.class));

        String creatorId = UUID.randomUUID().toString();

        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setPathParameters(Collections.singletonMap("creatorId", creatorId));
        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(500, responseEvent.getStatusCode());

        ApiError error = JsonUtil.fromJsonString(responseEvent.getBody(), ApiError.class);
        assertEquals(500, error.getStatus());
        assertEquals(ERROR_INTERNAL_SERVER_ERROR, error.getError());
        assertFalse(StringUtils.isNullOrEmpty(error.getMessage()));
        assertFalse(error.getErrorDetails().isEmpty());

        assertTrue(error.getMessage().contains(amazonDynamoDBException.getStatusCode()+""));
        assertTrue(error.getMessage().contains(amazonDynamoDBException.getErrorCode()));
        assertTrue(error.getMessage().contains(amazonDynamoDBException.getErrorMessage()));
        assertTrue(error.getMessage().contains(amazonDynamoDBException.getServiceName()));
        assertTrue(error.getMessage().contains(amazonDynamoDBException.getRequestId()));
    }

}

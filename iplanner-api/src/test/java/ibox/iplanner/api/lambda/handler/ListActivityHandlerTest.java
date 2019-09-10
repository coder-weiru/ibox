package ibox.iplanner.api.lambda.handler;

import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.util.StringUtils;
import ibox.iplanner.api.config.DaggerIPlannerComponent;
import ibox.iplanner.api.config.IPlannerComponent;
import ibox.iplanner.api.lambda.runtime.TestContext;
import ibox.iplanner.api.model.Activity;
import ibox.iplanner.api.model.ActivityStatus;
import ibox.iplanner.api.model.ApiError;
import ibox.iplanner.api.service.ActivityDataService;
import ibox.iplanner.api.util.ActivityUtil;
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
public class ListActivityHandlerTest {

    @InjectMocks
    private ListActivityHandler handler = new ListActivityHandler();

    private List<Activity> activities;

    @Mock
    private ActivityDataService activityDataServiceMock;

    public ListActivityHandlerTest() {
        IPlannerComponent iPlannerComponent = DaggerIPlannerComponent.builder().build();
        iPlannerComponent.inject(handler);
    }

    @Before
    public void setUp() {
        this.activities = Arrays.asList(new Activity[]{
                ActivityUtil.anyActivity(),
                ActivityUtil.anyActivity(),
                ActivityUtil.anyActivity()
        });
    }

    @Test
    public void listActivitys_shouldInvokeActivityDateServiceGivenCorrectParams() throws Exception {
        when(activityDataServiceMock.getMyActivities(any(String.class), any(String.class), any(Integer.class))).thenReturn(activities);

        String creatorId = UUID.randomUUID().toString();
        String status = ActivityStatus.ACTIVE.name();
        String limit = "10";

        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setPathParameters(Collections.singletonMap("creatorId", creatorId));
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("limit", limit);
        requestParams.put("status", status);
        requestEvent.setQueryStringParameters(requestParams);

        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(200, responseEvent.getStatusCode());

        ArgumentCaptor<String> activityIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> statusCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> limitCaptor = ArgumentCaptor.forClass(Integer.class);

        verify(activityDataServiceMock, times(1)).getMyActivities(activityIdCaptor.capture(), statusCaptor.capture(), limitCaptor.capture());

        verifyNoMoreInteractions(activityDataServiceMock);

        assertThat(activityIdCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(creatorId)));
        assertThat(statusCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(status)));
        assertThat(limitCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(Integer.valueOf(limit))));
    }

    @Test
    public void listActivities_shouldReturnBadRequestMessageIfCreatorIdInvalid() throws Exception {
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
    public void listActivities_shouldInvokeActivityDateServiceEvenStatusNotSpecified() throws Exception {
        List<Activity> activities = Arrays.asList(new Activity[]{
                ActivityUtil.anyActivity(),
                ActivityUtil.anyActivity(),
                ActivityUtil.anyActivity()
        });

        when(activityDataServiceMock.getMyActivities(any(String.class), any(String.class), any(Integer.class))).thenReturn(activities);

        Instant now = Instant.now();
        String creatorId = UUID.randomUUID().toString();
        String limit = "10";

        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setPathParameters(Collections.singletonMap("creatorId", creatorId));
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("limit", limit);
        requestEvent.setQueryStringParameters(requestParams);

        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(200, responseEvent.getStatusCode());

        ArgumentCaptor<String> activityIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> statusCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> limitCaptor = ArgumentCaptor.forClass(Integer.class);

        verify(activityDataServiceMock, times(1)).getMyActivities(activityIdCaptor.capture(), statusCaptor.capture(), limitCaptor.capture());

        verifyNoMoreInteractions(activityDataServiceMock);

        assertThat(activityIdCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(creatorId)));
        assertThat(statusCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(ActivityStatus.ACTIVE.name())));
        assertThat(limitCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(Integer.valueOf(limit))));
    }

    @Test
    public void listActivities_shouldInvokeActivityDateServiceEvenLimitIsNotSpecified() throws Exception {
        List<Activity> activities = Arrays.asList(new Activity[]{
                ActivityUtil.anyActivity(),
                ActivityUtil.anyActivity(),
                ActivityUtil.anyActivity()
        });

        when(activityDataServiceMock.getMyActivities(any(String.class), any(String.class), any(Integer.class))).thenReturn(activities);

        Instant now = Instant.now();
        String creatorId = UUID.randomUUID().toString();

        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setPathParameters(Collections.singletonMap("creatorId", creatorId));
        Map<String, String> requestParams = new HashMap<>();
        requestEvent.setQueryStringParameters(requestParams);

        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(200, responseEvent.getStatusCode());

        ArgumentCaptor<String> activityIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> statusCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> limitCaptor = ArgumentCaptor.forClass(Integer.class);

        verify(activityDataServiceMock, times(1)).getMyActivities(activityIdCaptor.capture(), statusCaptor.capture(), limitCaptor.capture());

        verifyNoMoreInteractions(activityDataServiceMock);

        assertThat(activityIdCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(creatorId)));
        assertThat(statusCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(ActivityStatus.ACTIVE.name())));
        assertThat(limitCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(Integer.valueOf(100))));

    }

    @Test
    public void listActivities_shouldReturnInternalServerErrorMessageIfAmazonServiceExceptionIsThrown() throws Exception {
        Activity activity = ActivityUtil.anyActivity();
        activity.setId(UUID.randomUUID().toString());
        activity.getCreator().setId(UUID.randomUUID().toString());

        AmazonDynamoDBException amazonDynamoDBException = new AmazonDynamoDBException("dynamo db error");
        amazonDynamoDBException.setStatusCode(SC_NOT_FOUND);
        amazonDynamoDBException.setErrorCode("AWSERR");
        amazonDynamoDBException.setServiceName("QueryItem");
        amazonDynamoDBException.setRequestId("request1");
        amazonDynamoDBException.setErrorMessage("error message");

        doThrow(amazonDynamoDBException).when(activityDataServiceMock).getMyActivities(any(String.class), any(String.class), any(Integer.class));

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

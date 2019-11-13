package ibox.iplanner.api.lambda.handler;

import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.util.StringUtils;
import ibox.iplanner.api.config.DaggerIPlannerComponent;
import ibox.iplanner.api.config.IPlannerComponent;
import ibox.iplanner.api.lambda.runtime.TestContext;
import ibox.iplanner.api.model.Activity;
import ibox.iplanner.api.model.ApiError;
import ibox.iplanner.api.service.ActivityDataService;
import ibox.iplanner.api.util.ActivityUtil;
import ibox.iplanner.api.util.JsonUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static ibox.iplanner.api.util.ApiErrorConstants.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UpdateActivityHandlerTest {

    @InjectMocks
    private UpdateActivityHandler handler = new UpdateActivityHandler();

    private Activity activity;


    private String newTitle = "new title";
    private String newDescription = "new description";
    private String newTemplate = "new template";

    @Mock
    private ActivityDataService activityDataServiceMock;

    public UpdateActivityHandlerTest() {
        IPlannerComponent iPlannerComponent = DaggerIPlannerComponent.builder().build();
        iPlannerComponent.inject(handler);
    }

    @Before
    public void setUp() {
        this.activity = ActivityUtil.anyActivity();
    }

    @Test
    public void updateActivity_shouldInvokeActivityDateServiceWithUpdatable() {

        when(activityDataServiceMock.updateActivity(any(Activity.class))).thenReturn(activity);

        activity.setTitle(newTitle);
        activity.setDescription(newDescription);
        activity.setActivityType(newTemplate);

        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setPathParameters(Collections.singletonMap("activityId", activity.getId()));
        requestEvent.setBody(JsonUtil.toJsonString(activity));
        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(200, responseEvent.getStatusCode());

        ArgumentCaptor<Activity> requestCaptor = ArgumentCaptor.forClass(Activity.class);

        verify(activityDataServiceMock, times(1)).updateActivity(requestCaptor.capture());

        verifyNoMoreInteractions(activityDataServiceMock);

        Activity argument = requestCaptor.getValue();

        assertThat(argument.getId(), is(equalTo(activity.getId())));
        assertThat(argument.getTitle(), is(equalTo(newTitle)));
        assertThat(argument.getDescription(), is(equalTo(newDescription)));
        assertThat(argument.getActivityType(), is(equalTo(newTemplate)));
    }

    @Test
    public void updateActivity_shouldReturnBadRequestMessageIfMissingKey() throws Exception {
        activity.setId(null);

        when(activityDataServiceMock.updateActivity(any(Activity.class))).thenReturn(activity);

        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setPathParameters(Collections.singletonMap("activityId", activity.getId()));
        requestEvent.setBody(JsonUtil.toJsonString(activity));
        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(400, responseEvent.getStatusCode());

        ApiError error = JsonUtil.fromJsonString(responseEvent.getBody(), ApiError.class);
        assertEquals(400, error.getStatus());
        assertEquals(ERROR_BAD_REQUEST, error.getError());
        assertFalse(StringUtils.isNullOrEmpty(error.getMessage()));
        assertTrue(error.getErrorDetails().isEmpty());
    }

    @Test
    public void updateActivity_shouldReturnInternalServerErrorMessageIfAmazonDynamoDBExceptionIsThrown() throws Exception {

        AmazonDynamoDBException amazonDynamoDBException = new AmazonDynamoDBException("dynamo db error");
        amazonDynamoDBException.setStatusCode(SC_NOT_FOUND);
        amazonDynamoDBException.setErrorCode("AWSERR");
        amazonDynamoDBException.setServiceName("UpdateItem");
        amazonDynamoDBException.setRequestId("request1");
        amazonDynamoDBException.setErrorMessage("error message");

        doThrow(amazonDynamoDBException).when(activityDataServiceMock).updateActivity(any(Activity.class));

        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setPathParameters(Collections.singletonMap("activityId", activity.getId()));
        requestEvent.setBody(JsonUtil.toJsonString(activity));
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

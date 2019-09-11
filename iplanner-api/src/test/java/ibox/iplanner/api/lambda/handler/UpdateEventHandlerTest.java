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
import ibox.iplanner.api.model.updatable.*;
import ibox.iplanner.api.service.EventDataService;
import ibox.iplanner.api.service.dbmodel.EventDefinition;
import ibox.iplanner.api.util.EventUtil;
import ibox.iplanner.api.util.JsonUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static ibox.iplanner.api.util.ApiErrorConstants.*;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UpdateEventHandlerTest {

    @InjectMocks
    private UpdateEventHandler handler = new UpdateEventHandler();

    private Event event;

    private Updatable updatable;

    private String newSummary = "new title";
    private String newDescription = "new description";
    private String newActivity = "new activity";

    @Mock
    private EventDataService eventDataServiceMock;

    public UpdateEventHandlerTest() {
        IPlannerComponent iPlannerComponent = DaggerIPlannerComponent.builder().build();
        iPlannerComponent.inject(handler);
    }

    @Before
    public void setUp() {
        this.event = EventUtil.anyEvent();

        Set<UpdatableAttribute> updatableAttributeSet = new HashSet<>();
        updatableAttributeSet.add(UpdatableAttribute.builder()
                .attributeName(EventDefinition.FIELD_NAME_SUMMARY)
                .action(UpdateAction.UPDATE)
                .value(newSummary)
                .build());
        updatableAttributeSet.add(UpdatableAttribute.builder()
                .attributeName(EventDefinition.FIELD_NAME_DESCRIPTION)
                .action(UpdateAction.UPDATE)
                .value(newDescription)
                .build());
        updatableAttributeSet.add(UpdatableAttribute.builder()
                .attributeName(EventDefinition.FIELD_NAME_ACTIVITY)
                .action(UpdateAction.UPDATE)
                .value(newActivity)
                .build());

        updatable = Updatable.builder()
                .objectType("event")
                .primaryKey(new UpdatableKey()
                        .addComponent(EventDefinition.FIELD_NAME_ID, event.getId()))
                .updatableAttributes(updatableAttributeSet)
                .build();
    }

    @Test
    public void updateEvent_shouldInvokeEventDateServiceWithUpdatable() throws Exception {

        when(eventDataServiceMock.updateEvent(any(Updatable.class))).thenReturn(event);

        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setPathParameters(Collections.singletonMap("eventId", event.getId()));
        requestEvent.setBody(JsonUtil.toJsonString(updatable));
        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(200, responseEvent.getStatusCode());

        ArgumentCaptor<Updatable> requestCaptor = ArgumentCaptor.forClass(Updatable.class);

        verify(eventDataServiceMock, times(1)).updateEvent(requestCaptor.capture());

        verifyNoMoreInteractions(eventDataServiceMock);

        Updatable argument = requestCaptor.getValue();

        assertThat(argument.getObjectType(), is(equalTo("event")));
        assertThat(argument.getPrimaryKey().getComponents(), hasItem(new KeyAttribute(EventDefinition.FIELD_NAME_ID, event.getId())));
        assertThat(argument.getUpdatableAttributes(), hasItem(UpdatableAttribute.builder()
                .attributeName(EventDefinition.FIELD_NAME_SUMMARY)
                .action(UpdateAction.UPDATE)
                .value(newSummary)
                .build()));
        assertThat(argument.getUpdatableAttributes(), hasItem(UpdatableAttribute.builder()
                .attributeName(EventDefinition.FIELD_NAME_DESCRIPTION)
                .action(UpdateAction.UPDATE)
                .value(newDescription)
                .build()));
        assertThat(argument.getUpdatableAttributes(), hasItem(UpdatableAttribute.builder()
                .attributeName(EventDefinition.FIELD_NAME_ACTIVITY)
                .action(UpdateAction.UPDATE)
                .value(newActivity)
                .build()));
    }

    @Test
    public void updateEvent_shouldReturnBadRequestMessageIfMissingKey() throws Exception {
        updatable.setPrimaryKey(null);

        verifyBadRequestMessage();
    }

    @Test
    public void updateEvent_shouldReturnBadRequestMessageIfMissingObjectType() throws Exception {
        updatable.setObjectType(null);

        verifyBadRequestMessage();
    }

    @Test
    public void updateEvent_shouldReturnBadRequestMessageIfUpdatableAttributeIsEmpty() throws Exception {
        updatable.getUpdatableAttributes().clear();

        verifyBadRequestMessage();
    }

    @Test
    public void updateEvent_shouldReturnBadRequestMessageIfUpdatableAttributeUpdateActionNotSpecified() throws Exception {
        updatable.getUpdatableAttributes().stream().forEach(e -> {
            e.setAction(null);
        });
        verifyBadRequestMessage();
    }


    private void verifyBadRequestMessage() throws Exception {

        when(eventDataServiceMock.updateEvent(any(Updatable.class))).thenReturn(event);

        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setPathParameters(Collections.singletonMap("eventId", event.getId()));
        requestEvent.setBody(JsonUtil.toJsonString(updatable));
        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(400, responseEvent.getStatusCode());

        ApiError error = JsonUtil.fromJsonString(responseEvent.getBody(), ApiError.class);
        assertEquals(400, error.getStatus());
        assertEquals(ERROR_BAD_REQUEST, error.getError());
        assertFalse(StringUtils.isNullOrEmpty(error.getMessage()));
        assertFalse(error.getErrorDetails().isEmpty());
    }

    @Test
    public void updateEvent_shouldReturnInternalServerErrorMessageIfAmazonDynamoDBExceptionIsThrown() throws Exception {

        AmazonDynamoDBException amazonDynamoDBException = new AmazonDynamoDBException("dynamo db error");
        amazonDynamoDBException.setStatusCode(SC_NOT_FOUND);
        amazonDynamoDBException.setErrorCode("AWSERR");
        amazonDynamoDBException.setServiceName("UpdateItem");
        amazonDynamoDBException.setRequestId("request1");
        amazonDynamoDBException.setErrorMessage("error message");

        doThrow(amazonDynamoDBException).when(eventDataServiceMock).updateEvent(any(Updatable.class));

        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setPathParameters(Collections.singletonMap("eventId", event.getId()));
        requestEvent.setBody(JsonUtil.toJsonString(updatable));
        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(500, responseEvent.getStatusCode());

        ApiError error = JsonUtil.fromJsonString(responseEvent.getBody(), ApiError.class);
        assertEquals(500, error.getStatus());
        assertEquals(ERROR_INTERNAL_SERVER_ERROR, error.getError());
        assertFalse(StringUtils.isNullOrEmpty(error.getMessage()));
        assertFalse(error.getErrorDetails().isEmpty());

        assertTrue(error.getMessage().contains(amazonDynamoDBException.getStatusCode() + ""));
        assertTrue(error.getMessage().contains(amazonDynamoDBException.getErrorCode()));
        assertTrue(error.getMessage().contains(amazonDynamoDBException.getErrorMessage()));
        assertTrue(error.getMessage().contains(amazonDynamoDBException.getServiceName()));
        assertTrue(error.getMessage().contains(amazonDynamoDBException.getRequestId()));
    }
}
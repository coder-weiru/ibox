package ibox.iplanner.api.lambda.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import ibox.iplanner.api.lambda.exception.GlobalExceptionHandler;
import ibox.iplanner.api.lambda.validation.JsonSchemaValidator;
import ibox.iplanner.api.lambda.validation.RequestEventValidator;
import ibox.iplanner.api.model.Event;
import ibox.iplanner.api.service.EventDataService;
import ibox.iplanner.api.util.JsonUtil;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static ibox.iplanner.api.util.ApiErrorConstants.SC_OK;

public class AddEventHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Inject
    EventDataService eventDataService;
    @Inject
    RequestEventValidator requestEventValidator;
    @Inject
    JsonSchemaValidator jsonSchemaValidator;
    @Inject
    GlobalExceptionHandler globalExceptionHandler;

    public AddEventHandler() {
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {

        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        try {
            requestEventValidator.validateBody(requestEvent);
            jsonSchemaValidator.validate(requestEvent.getBody(), Event.class, "/event-array");

            List<Event> newEvents = (List<Event>) JsonUtil.fromJsonString(requestEvent.getBody(), List.class, Event.class);

            List<Event> dbEvents = newEvents.stream().map(e -> {
                Event dbEvent = e;
                dbEvent.setId(UUID.randomUUID().toString());
                return dbEvent;
            }).collect(Collectors.toList());

            eventDataService.addEvents(dbEvents);

            //setting up the response message
            responseEvent.setBody(JsonUtil.toJsonString(dbEvents));
            responseEvent.setStatusCode(SC_OK);

            return responseEvent;

        } catch (Exception ex) {
            return globalExceptionHandler.handleException(ex);
        }
    }

}

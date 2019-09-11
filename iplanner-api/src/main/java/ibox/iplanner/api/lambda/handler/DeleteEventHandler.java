package ibox.iplanner.api.lambda.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import ibox.iplanner.api.lambda.exception.GlobalExceptionHandler;
import ibox.iplanner.api.lambda.validation.BeanValidator;
import ibox.iplanner.api.lambda.validation.RequestEventValidator;
import ibox.iplanner.api.model.Event;
import ibox.iplanner.api.service.EventDataService;
import ibox.iplanner.api.util.JsonUtil;

import javax.inject.Inject;
import java.util.Map;

import static ibox.iplanner.api.lambda.validation.RequestEventValidator.UUID_PATTERN;
import static ibox.iplanner.api.util.ApiErrorConstants.SC_OK;

public class DeleteEventHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Inject
    EventDataService eventDataService;
    @Inject
    RequestEventValidator requestEventValidator;
    @Inject
    BeanValidator beanValidator;
    @Inject
    GlobalExceptionHandler globalExceptionHandler;

    public DeleteEventHandler() {
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {

        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        try {
            requestEventValidator.validatePathParameterNotBlank(requestEvent, "eventId");
            requestEventValidator.validatePathParameterPattern(requestEvent, UUID_PATTERN, "eventId");

            Map<String, String> pathParameterMap = requestEvent.getPathParameters();
            final String eventId  = pathParameterMap.get("eventId");

            Event deleted = eventDataService.deleteEvent(eventId);

            //setting up the response message
            responseEvent.setBody(JsonUtil.toJsonString(deleted));
            responseEvent.setStatusCode(SC_OK);

            return responseEvent;

        } catch (Exception ex) {
            return globalExceptionHandler.handleException(ex);
        }
    }

}

package ibox.iplanner.api.lambda.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import ibox.iplanner.api.lambda.exception.GlobalExceptionHandler;
import ibox.iplanner.api.lambda.validation.RequestEventValidator;
import ibox.iplanner.api.service.EventDataService;
import ibox.iplanner.api.util.JsonUtil;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;

import static ibox.iplanner.api.lambda.validation.RequestEventValidator.UUID_PATTERN;
import static ibox.iplanner.api.util.ApiErrorConstants.SC_NOT_FOUND;
import static ibox.iplanner.api.util.ApiErrorConstants.SC_OK;

public class GetEventHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Inject
    EventDataService eventDataService;
    @Inject
    RequestEventValidator requestEventValidator;
    @Inject
    GlobalExceptionHandler globalExceptionHandler;

    public GetEventHandler() {
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {

        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        try {
            requestEventValidator.validatePathParameterNotBlank(requestEvent, "eventId");
            requestEventValidator.validatePathParameterPattern(requestEvent, UUID_PATTERN, "eventId");

            Map<String, String> pathParameterMap = requestEvent.getPathParameters();
            final String eventId  = pathParameterMap.get("eventId");

            Optional event = Optional.ofNullable(eventDataService.getEvent(eventId));

            if (event.isPresent()) {
                responseEvent.setBody(JsonUtil.toJsonString(event.get()));
                responseEvent.setStatusCode(SC_OK);
            } else {
                responseEvent.setStatusCode(SC_NOT_FOUND);
            }
            return responseEvent;

        } catch (Exception ex) {
            return globalExceptionHandler.handleException(ex);
        }
    }

}

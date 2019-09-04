package ibox.iplanner.api.lambda.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.util.StringUtils;
import ibox.iplanner.api.lambda.exception.GlobalExceptionHandler;
import ibox.iplanner.api.lambda.validation.RequestEventValidator;
import ibox.iplanner.api.service.EventDataService;
import ibox.iplanner.api.util.DateTimeUtil;
import ibox.iplanner.api.util.JsonUtil;

import javax.inject.Inject;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static ibox.iplanner.api.lambda.validation.RequestEventValidator.UUID_PATTERN;
import static ibox.iplanner.api.util.ApiErrorConstants.SC_NOT_FOUND;
import static ibox.iplanner.api.util.ApiErrorConstants.SC_OK;
import static java.time.temporal.ChronoUnit.DAYS;

public class ListEventHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Inject
    EventDataService eventDataService;
    @Inject
    RequestEventValidator requestEventValidator;
    @Inject
    GlobalExceptionHandler globalExceptionHandler;

    public ListEventHandler() {
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {

        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        try {
            requestEventValidator.validatePathParameterNotBlank(requestEvent, "creatorId");
            requestEventValidator.validatePathParameterPattern(requestEvent, UUID_PATTERN, "creatorId");

            Map<String, String> pathParameterMap = requestEvent.getPathParameters();
            final String creatorId  = pathParameterMap.get("creatorId");

            Map<String, String> requestParameterMap = requestEvent.getQueryStringParameters();
            final String startTime = Optional.ofNullable(requestParameterMap)
                    .map(mapNode -> mapNode.get("start"))
                    .orElse(null);
            Instant timeWindowStart = Instant.now();
            if (!StringUtils.isNullOrEmpty(startTime)) {
                timeWindowStart = DateTimeUtil.parseUTCDatetime(startTime);
            }
            final String endTime = Optional.ofNullable(requestParameterMap)
                    .map(mapNode -> mapNode.get("end"))
                    .orElse(null);
            Instant timeWindowEnd = timeWindowStart.plus(365, DAYS);
            if (!StringUtils.isNullOrEmpty(endTime)) {
                timeWindowEnd = DateTimeUtil.parseUTCDatetime(endTime);
            }
            final String limit = Optional.ofNullable(requestParameterMap)
                    .map(mapNode -> mapNode.get("limit"))
                    .orElse(null);
            Integer queryLimit = 100;
            if (!StringUtils.isNullOrEmpty(limit)) {
                queryLimit = Integer.valueOf(limit);
            }
            Optional eventList = Optional.ofNullable(eventDataService.getMyEventsWithinTime(creatorId, timeWindowStart, timeWindowEnd, queryLimit));
            if (eventList.isPresent()) {
                responseEvent.setBody(JsonUtil.toJsonString(eventList.get()));
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

package ibox.iplanner.api.lambda.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import ibox.iplanner.api.lambda.exception.GlobalExceptionHandler;
import ibox.iplanner.api.lambda.validation.BeanValidator;
import ibox.iplanner.api.lambda.validation.RequestEventValidator;
import ibox.iplanner.api.model.Activity;
import ibox.iplanner.api.service.ActivityDataService;
import ibox.iplanner.api.util.JsonUtil;

import javax.inject.Inject;
import java.util.Map;

import static ibox.iplanner.api.lambda.validation.RequestEventValidator.UUID_PATTERN;
import static ibox.iplanner.api.util.ApiErrorConstants.SC_OK;

public class DeleteActivityHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Inject
    ActivityDataService activityDataService;
    @Inject
    RequestEventValidator requestEventValidator;
    @Inject
    BeanValidator beanValidator;
    @Inject
    GlobalExceptionHandler globalExceptionHandler;

    public DeleteActivityHandler() {
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {

        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        try {
            requestEventValidator.validatePathParameterNotBlank(requestEvent, "activityId");
            requestEventValidator.validatePathParameterPattern(requestEvent, UUID_PATTERN, "activityId");

            Map<String, String> pathParameterMap = requestEvent.getPathParameters();
            final String activityId  = pathParameterMap.get("activityId");

            Activity deleted = activityDataService.deleteActivity(activityId);

            //setting up the response message
            responseEvent.setBody(JsonUtil.toJsonString(deleted));
            responseEvent.setStatusCode(SC_OK);

            return responseEvent;

        } catch (Exception ex) {
            return globalExceptionHandler.handleException(ex);
        }
    }

}

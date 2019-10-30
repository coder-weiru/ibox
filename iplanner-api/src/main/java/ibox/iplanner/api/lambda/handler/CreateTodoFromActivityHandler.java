package ibox.iplanner.api.lambda.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import ibox.iplanner.api.lambda.exception.GlobalExceptionHandler;
import ibox.iplanner.api.lambda.validation.RequestEventValidator;
import ibox.iplanner.api.model.Activity;
import ibox.iplanner.api.model.ApiError;
import ibox.iplanner.api.model.Todo;
import ibox.iplanner.api.service.ActivityDataService;
import ibox.iplanner.api.service.EventGenerator;
import ibox.iplanner.api.util.JsonUtil;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;

import static ibox.iplanner.api.lambda.validation.RequestEventValidator.UUID_PATTERN;
import static ibox.iplanner.api.util.ApiErrorConstants.*;

public class CreateTodoFromActivityHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final String ACTIVITY_NOT_FOUND_ERROR_MESSAGE = "The specified activity is not found";

    @Inject
    ActivityDataService activityDataService;
    @Inject
    RequestEventValidator requestEventValidator;
    @Inject
    GlobalExceptionHandler globalExceptionHandler;

    public CreateTodoFromActivityHandler() {
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {

        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        try {
            requestEventValidator.validatePathParameterNotBlank(requestEvent, "activityId");
            requestEventValidator.validatePathParameterPattern(requestEvent, UUID_PATTERN, "activityId");

            Map<String, String> pathParameterMap = requestEvent.getPathParameters();
            final String activityId  = pathParameterMap.get("activityId");

            Optional<Activity> activity = Optional.ofNullable(activityDataService.getActivity(activityId));

            if (activity.isPresent()) {

                Todo todo = EventGenerator.generate(activity.get());

                responseEvent.setBody(JsonUtil.toJsonString(todo));
                responseEvent.setStatusCode(SC_OK);
            } else {
                ApiError error = ApiError.builder()
                        .error(ERROR_NOT_FOUND)
                        .message(ACTIVITY_NOT_FOUND_ERROR_MESSAGE)
                        .status(SC_NOT_FOUND)
                        .build();

                responseEvent.setBody(JsonUtil.toJsonString(error));
                responseEvent.setStatusCode(SC_NOT_FOUND);
            }
            return responseEvent;

        } catch (Exception ex) {
            return globalExceptionHandler.handleException(ex);
        }
    }

}

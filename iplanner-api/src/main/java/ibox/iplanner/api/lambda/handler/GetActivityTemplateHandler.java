package ibox.iplanner.api.lambda.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import ibox.iplanner.api.lambda.exception.GlobalExceptionHandler;
import ibox.iplanner.api.lambda.validation.RequestEventValidator;
import ibox.iplanner.api.model.Activity;
import ibox.iplanner.api.util.JsonUtil;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ibox.iplanner.api.util.ApiErrorConstants.SC_NOT_FOUND;
import static ibox.iplanner.api.util.ApiErrorConstants.SC_OK;

public class GetActivityTemplateHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final String ACTIVITY_TEMPLATE_RESOURCE = "/template/activity.template.json";

    @Inject
    RequestEventValidator requestEventValidator;
    @Inject
    GlobalExceptionHandler globalExceptionHandler;

    public GetActivityTemplateHandler() {
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {

        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        try {
            requestEventValidator.validateBody(requestEvent);
            List<String> activityNames = (List<String>) JsonUtil.fromJsonString(requestEvent.getBody(), List.class, String.class);

            final JsonNode jsonNode = JsonLoader.fromResource(ACTIVITY_TEMPLATE_RESOURCE);
            Map<String, Object> activities = JsonUtil.fromJsonString(jsonNode.toString(), Map.class, String.class, Activity.class);

            activities = activities.entrySet().stream().filter(entry -> activityNames.contains(entry.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            if (!activities.isEmpty()) {

                responseEvent.setBody(JsonUtil.toJsonString(activities));
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

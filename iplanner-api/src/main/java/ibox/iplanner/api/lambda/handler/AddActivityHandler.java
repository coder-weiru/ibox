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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static ibox.iplanner.api.util.ApiErrorConstants.SC_OK;

public class AddActivityHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Inject
    ActivityDataService activityDataService;
    @Inject
    RequestEventValidator requestEventValidator;
    @Inject
    BeanValidator beanValidator;
    @Inject
    GlobalExceptionHandler globalExceptionHandler;

    public AddActivityHandler() {
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {

        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        try {
            requestEventValidator.validateBody(requestEvent);

            List<Activity> newActivities = (List<Activity>) JsonUtil.fromJsonString(requestEvent.getBody(), List.class, Activity.class);

            newActivities.stream().forEach(e -> beanValidator.validate(e));

            List<Activity> dbActivities = newActivities.stream().map(e -> {
                Activity dbActivity = e;
                dbActivity.setId(UUID.randomUUID().toString());
                return dbActivity;
            }).collect(Collectors.toList());

            activityDataService.addActivities(dbActivities);

            //setting up the response message
            responseEvent.setBody(JsonUtil.toJsonString(newActivities));
            responseEvent.setStatusCode(SC_OK);

            return responseEvent;

        } catch (Exception ex) {
            return globalExceptionHandler.handleException(ex);
        }
    }

}

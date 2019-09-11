package ibox.iplanner.api.lambda.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import ibox.iplanner.api.lambda.exception.GlobalExceptionHandler;
import ibox.iplanner.api.lambda.validation.BeanValidator;
import ibox.iplanner.api.lambda.validation.RequestEventValidator;
import ibox.iplanner.api.model.Activity;
import ibox.iplanner.api.model.updatable.Updatable;
import ibox.iplanner.api.service.ActivityDataService;
import ibox.iplanner.api.util.JsonUtil;

import javax.inject.Inject;

import static ibox.iplanner.api.util.ApiErrorConstants.SC_OK;

public class UpdateActivityHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Inject
    ActivityDataService activityDataService;
    @Inject
    RequestEventValidator requestEventValidator;
    @Inject
    BeanValidator beanValidator;
    @Inject
    GlobalExceptionHandler globalExceptionHandler;

    public UpdateActivityHandler() {
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {

        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        try {
            requestEventValidator.validateBody(requestEvent);

            Updatable updatable = (Updatable) JsonUtil.fromJsonString(requestEvent.getBody(), Updatable.class);

            beanValidator.validate(updatable);

            Activity updated = activityDataService.updateActivity(updatable);

            //setting up the response message
            responseEvent.setBody(JsonUtil.toJsonString(updated));
            responseEvent.setStatusCode(SC_OK);

            return responseEvent;

        } catch (Exception ex) {
            return globalExceptionHandler.handleException(ex);
        }
    }

}

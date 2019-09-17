package ibox.iplanner.api.lambda.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import ibox.iplanner.api.lambda.exception.GlobalExceptionHandler;
import ibox.iplanner.api.lambda.validation.RequestEventValidator;

import javax.inject.Inject;

import static ibox.iplanner.api.util.ApiErrorConstants.SC_OK;

public class CreateEventFromActivityHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Inject
    RequestEventValidator requestEventValidator;
    @Inject
    GlobalExceptionHandler globalExceptionHandler;

    public CreateEventFromActivityHandler() {
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {

        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        try {
            requestEventValidator.validateBody(requestEvent);


            //setting up the response message
            responseEvent.setBody("");
            responseEvent.setStatusCode(SC_OK);

            return responseEvent;

        } catch (Exception ex) {
            return globalExceptionHandler.handleException(ex);
        }
    }

}

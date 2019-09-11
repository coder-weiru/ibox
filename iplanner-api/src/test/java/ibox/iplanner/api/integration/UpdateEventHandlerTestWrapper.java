package ibox.iplanner.api.integration;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import ibox.iplanner.api.config.DaggerTestIPlannerComponent;
import ibox.iplanner.api.config.TestIPlannerComponent;
import ibox.iplanner.api.lambda.handler.UpdateEventHandler;

public class UpdateEventHandlerTestWrapper implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private UpdateEventHandler updateEventHandler = new UpdateEventHandler();

    public UpdateEventHandlerTestWrapper() {
        TestIPlannerComponent iPlannerComponent = DaggerTestIPlannerComponent.builder().build();
        iPlannerComponent.inject(updateEventHandler);
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        return updateEventHandler.handleRequest(requestEvent, context);
    }

}
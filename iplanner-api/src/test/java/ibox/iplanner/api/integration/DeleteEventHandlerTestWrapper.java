package ibox.iplanner.api.integration;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import ibox.iplanner.api.config.DaggerTestIPlannerComponent;
import ibox.iplanner.api.config.TestIPlannerComponent;
import ibox.iplanner.api.lambda.handler.DeleteEventHandler;

public class DeleteEventHandlerTestWrapper implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private DeleteEventHandler deleteEventHandler = new DeleteEventHandler();

    public DeleteEventHandlerTestWrapper() {
        TestIPlannerComponent iPlannerComponent = DaggerTestIPlannerComponent.builder().build();
        iPlannerComponent.inject(deleteEventHandler);
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        return deleteEventHandler.handleRequest(requestEvent, context);
    }

}
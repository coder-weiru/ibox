package ibox.iplanner.api.integration;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import ibox.iplanner.api.config.DaggerTestIPlannerComponent;
import ibox.iplanner.api.config.TestIPlannerComponent;
import ibox.iplanner.api.lambda.handler.GetEventHandler;

public class GetEventHandlerTestWrapper implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private GetEventHandler getEventHandler = new GetEventHandler();

    public GetEventHandlerTestWrapper() {
        TestIPlannerComponent iPlannerComponent = DaggerTestIPlannerComponent.builder().build();
        iPlannerComponent.inject(getEventHandler);
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        return getEventHandler.handleRequest(requestEvent, context);
    }

}


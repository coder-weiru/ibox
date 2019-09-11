package ibox.iplanner.api.integration;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import ibox.iplanner.api.config.DaggerTestIPlannerComponent;
import ibox.iplanner.api.config.TestIPlannerComponent;
import ibox.iplanner.api.lambda.handler.UpdateActivityHandler;

public class UpdateActivityHandlerTestWrapper implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private UpdateActivityHandler updateActivityHandler = new UpdateActivityHandler();

    public UpdateActivityHandlerTestWrapper() {
        TestIPlannerComponent iPlannerComponent = DaggerTestIPlannerComponent.builder().build();
        iPlannerComponent.inject(updateActivityHandler);
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        return updateActivityHandler.handleRequest(requestEvent, context);
    }

}

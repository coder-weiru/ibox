package ibox.iplanner.api.integration;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import ibox.iplanner.api.config.DaggerTestIPlannerComponent;
import ibox.iplanner.api.config.TestIPlannerComponent;
import ibox.iplanner.api.lambda.handler.DeleteActivityHandler;

public class DeleteActivityHandlerTestWrapper implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private DeleteActivityHandler deleteActivityHandler = new DeleteActivityHandler();

    public DeleteActivityHandlerTestWrapper() {
        TestIPlannerComponent iPlannerComponent = DaggerTestIPlannerComponent.builder().build();
        iPlannerComponent.inject(deleteActivityHandler);
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        return deleteActivityHandler.handleRequest(requestEvent, context);
    }

}
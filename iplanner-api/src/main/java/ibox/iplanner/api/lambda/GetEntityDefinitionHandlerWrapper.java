package ibox.iplanner.api.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import ibox.iplanner.api.config.DaggerIPlannerComponent;
import ibox.iplanner.api.config.IPlannerComponent;
import ibox.iplanner.api.lambda.handler.GetEntityDefinitionHandler;

public class GetEntityDefinitionHandlerWrapper implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private GetEntityDefinitionHandler getEntityDefinitionHandler = new GetEntityDefinitionHandler();

    public GetEntityDefinitionHandlerWrapper() {
        IPlannerComponent iPlannerComponent = DaggerIPlannerComponent.builder().build();
        iPlannerComponent.inject(getEntityDefinitionHandler);
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        return getEntityDefinitionHandler.handleRequest(requestEvent, context);
    }
}

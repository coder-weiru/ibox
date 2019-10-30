package ibox.iplanner.api.lambda.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import ibox.iplanner.api.lambda.exception.GlobalExceptionHandler;
import ibox.iplanner.api.lambda.validation.RequestEventValidator;
import ibox.iplanner.api.service.TodoDataService;
import ibox.iplanner.api.util.JsonUtil;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;

import static ibox.iplanner.api.lambda.validation.RequestEventValidator.UUID_PATTERN;
import static ibox.iplanner.api.util.ApiErrorConstants.SC_NOT_FOUND;
import static ibox.iplanner.api.util.ApiErrorConstants.SC_OK;

public class GetTodoHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Inject
    TodoDataService todoDataService;
    @Inject
    RequestEventValidator requestEventValidator;
    @Inject
    GlobalExceptionHandler globalExceptionHandler;

    public GetTodoHandler() {
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {

        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        try {
            requestEventValidator.validatePathParameterNotBlank(requestEvent, "todoId");
            requestEventValidator.validatePathParameterPattern(requestEvent, UUID_PATTERN, "todoId");

            Map<String, String> pathParameterMap = requestEvent.getPathParameters();
            final String todoId  = pathParameterMap.get("todoId");

            Optional todo = Optional.ofNullable(todoDataService.getTodo(todoId));

            if (todo.isPresent()) {
                responseEvent.setBody(JsonUtil.toJsonString(todo.get()));
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

package ibox.iplanner.api.lambda.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.util.StringUtils;
import ibox.iplanner.api.lambda.exception.GlobalExceptionHandler;
import ibox.iplanner.api.lambda.validation.RequestEventValidator;
import ibox.iplanner.api.model.TodoStatus;
import ibox.iplanner.api.service.TodoDataService;
import ibox.iplanner.api.util.JsonUtil;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;

import static ibox.iplanner.api.lambda.validation.RequestEventValidator.UUID_PATTERN;
import static ibox.iplanner.api.util.ApiErrorConstants.SC_NOT_FOUND;
import static ibox.iplanner.api.util.ApiErrorConstants.SC_OK;

public class ListTodoHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Inject
    TodoDataService todoDataService;
    @Inject
    RequestEventValidator requestEventValidator;
    @Inject
    GlobalExceptionHandler globalExceptionHandler;

    public ListTodoHandler() {
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {

        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        try {
            requestEventValidator.validatePathParameterNotBlank(requestEvent, "creatorId");
            requestEventValidator.validatePathParameterPattern(requestEvent, UUID_PATTERN, "creatorId");

            Map<String, String> pathParameterMap = requestEvent.getPathParameters();
            final String creatorId  = pathParameterMap.get("creatorId");

            Map<String, String> requestParameterMap = requestEvent.getQueryStringParameters();
            final String status = Optional.ofNullable(requestParameterMap)
                    .map(mapNode -> mapNode.get("status"))
                    .orElse(null);
            TodoStatus queryStatus = TodoStatus.OPEN;
            if (!StringUtils.isNullOrEmpty(status)) {
                queryStatus = Optional.ofNullable(TodoStatus.of(status)).orElse(TodoStatus.OPEN);
            }
            final String limit = Optional.ofNullable(requestParameterMap)
                    .map(mapNode -> mapNode.get("limit"))
                    .orElse(null);
            Integer queryLimit = 100;
            if (!StringUtils.isNullOrEmpty(limit)) {
                queryLimit = Integer.valueOf(limit);
            }
            Optional todoList = Optional.ofNullable(todoDataService.getMyTodoListByFilter(creatorId, null, queryStatus.name(), queryLimit));
            if (todoList.isPresent()) {
                responseEvent.setBody(JsonUtil.toJsonString(todoList.get()));
                responseEvent.setStatusCode(SC_OK);
            } else {
                responseEvent.setStatusCode(SC_NOT_FOUND);
            }
            return responseEvent;

        } catch (RuntimeException ex) {
            return globalExceptionHandler.handleException(ex);
        }
    }

}

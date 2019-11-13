package ibox.iplanner.api.lambda.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import ibox.iplanner.api.lambda.exception.GlobalExceptionHandler;
import ibox.iplanner.api.lambda.exception.RecordNotFoundException;
import ibox.iplanner.api.lambda.validation.JsonSchemaValidator;
import ibox.iplanner.api.lambda.validation.RequestEventValidator;
import ibox.iplanner.api.model.Todo;
import ibox.iplanner.api.service.TodoDataService;
import ibox.iplanner.api.util.JsonUtil;

import javax.inject.Inject;

import static ibox.iplanner.api.util.ApiErrorConstants.SC_OK;

public class UpdateTodoHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Inject
    TodoDataService todoDataService;
    @Inject
    RequestEventValidator requestEventValidator;
    @Inject
    JsonSchemaValidator jsonSchemaValidator;
    @Inject
    GlobalExceptionHandler globalExceptionHandler;

    public UpdateTodoHandler() {
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {

        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        try {
            requestEventValidator.validateBody(requestEvent);
            jsonSchemaValidator.validate(requestEvent.getBody(), Todo.class);

            Todo updatable = JsonUtil.fromJsonString(requestEvent.getBody(), Todo.class);

            if (updatable.getId()==null) {
                throw new RecordNotFoundException("Missing todo's Id");
            }

            Todo updated = todoDataService.updateTodo(updatable);

            //setting up the response message
            responseEvent.setBody(JsonUtil.toJsonString(updated));
            responseEvent.setStatusCode(SC_OK);

            return responseEvent;

        } catch (Exception ex) {
            return globalExceptionHandler.handleException(ex);
        }
    }

}

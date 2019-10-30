package ibox.iplanner.api.lambda.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import ibox.iplanner.api.lambda.exception.GlobalExceptionHandler;
import ibox.iplanner.api.lambda.validation.JsonSchemaValidator;
import ibox.iplanner.api.lambda.validation.RequestEventValidator;
import ibox.iplanner.api.model.Todo;
import ibox.iplanner.api.service.TodoDataService;
import ibox.iplanner.api.util.JsonUtil;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static ibox.iplanner.api.util.ApiErrorConstants.SC_OK;

public class AddTodoHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Inject
    TodoDataService todoDataService;
    @Inject
    RequestEventValidator requestEventValidator;
    @Inject
    JsonSchemaValidator jsonSchemaValidator;
    @Inject
    GlobalExceptionHandler globalExceptionHandler;

    public AddTodoHandler() {
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {

        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        try {
            requestEventValidator.validateBody(requestEvent);
            jsonSchemaValidator.validate(requestEvent.getBody(), Todo.class, "/todo-array");

            List<Todo> newTodos = (List<Todo>) JsonUtil.fromJsonString(requestEvent.getBody(), List.class, Todo.class);

            List<Todo> dbTodos = newTodos.stream().map(e -> {
                Todo dbTodo = e;
                dbTodo.setId(UUID.randomUUID().toString());
                return dbTodo;
            }).collect(Collectors.toList());

            todoDataService.addTodos(dbTodos);

            //setting up the response message
            responseEvent.setBody(JsonUtil.toJsonString(dbTodos));
            responseEvent.setStatusCode(SC_OK);

            return responseEvent;

        } catch (Exception ex) {
            return globalExceptionHandler.handleException(ex);
        }
    }

}

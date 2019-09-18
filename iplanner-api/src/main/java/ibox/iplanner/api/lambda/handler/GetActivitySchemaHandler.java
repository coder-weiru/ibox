package ibox.iplanner.api.lambda.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import ibox.iplanner.api.lambda.exception.GlobalExceptionHandler;
import ibox.iplanner.api.lambda.exception.InvalidInputException;
import ibox.iplanner.api.lambda.validation.EntitySchemaMap;
import ibox.iplanner.api.lambda.validation.RequestEventValidator;
import ibox.iplanner.api.model.ApiError;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Map;

import static ibox.iplanner.api.util.ApiErrorConstants.*;

public class GetActivitySchemaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Inject
    RequestEventValidator requestEventValidator;
    @Inject
    GlobalExceptionHandler globalExceptionHandler;
    @Inject
    EntitySchemaMap entitySchemaMap;

    private static final String RESOURCE_BASE = "/schema/ibox/iplanner/api/model";
    private static final String ACTIVITY_TYPE_NOT_FOUND_ERROR_MESSAGE = "The specified activity type is not found";

    public GetActivitySchemaHandler() {
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {

        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        try {
            requestEventValidator.validateRequestParameterNotBlank(requestEvent, "type");

            Map<String, String> requestParameterMap = requestEvent.getQueryStringParameters();
            final String type = requestParameterMap.get("type");

            final String resourcePath = String.format("%s/%s.schema.json", RESOURCE_BASE, type);
            if (entitySchemaMap.containsSchemaResource(resourcePath)) {
                final JsonNode jsonSchema = JsonLoader.fromResource(resourcePath);
                if (!StringUtils.isNullOrEmpty(jsonSchema.toString())) {
                    responseEvent.setBody(jsonSchema.toString());
                    responseEvent.setStatusCode(SC_OK);
                } else {
                    responseEvent.setStatusCode(SC_NOT_FOUND);
                }
            } else {
                ApiError error = ApiError.builder()
                        .error(ERROR_BAD_REQUEST)
                        .message(ACTIVITY_TYPE_NOT_FOUND_ERROR_MESSAGE)
                        .status(SC_BAD_REQUEST)
                        .build();
                throw new InvalidInputException(String.format(ACTIVITY_TYPE_NOT_FOUND_ERROR_MESSAGE), error);
            }

            return responseEvent;

        } catch (InvalidInputException | IOException ex) {
            return globalExceptionHandler.handleException(ex);
        }
    }

}

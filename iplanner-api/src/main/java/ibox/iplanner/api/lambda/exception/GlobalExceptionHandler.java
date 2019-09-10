package ibox.iplanner.api.lambda.exception;

import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import ibox.iplanner.api.model.ApiError;
import ibox.iplanner.api.util.JsonUtil;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ibox.iplanner.api.util.ApiErrorConstants.*;

public class GlobalExceptionHandler {

    public APIGatewayProxyResponseEvent handleException(Exception ex) {
        if (ex instanceof InvalidRequestEventException) {
            return handleRequestEventInvalidException((InvalidRequestEventException)ex);
        }
        else if (ex instanceof InvalidInputException) {
            return handleInvalidInputException((InvalidInputException)ex);
        }
        else if (ex instanceof AmazonDynamoDBException) {
            return handleAmazonDynamoDBException((AmazonDynamoDBException)ex);
        }
        else {
            return handleAllException(ex);
        }
    }

    private APIGatewayProxyResponseEvent handleRequestEventInvalidException(InvalidRequestEventException ex) {
        return handleInvalidRequestException(ex);
    }

    private APIGatewayProxyResponseEvent handleInvalidInputException(InvalidInputException ex) {
        return handleInvalidRequestException(ex);
    }

    private APIGatewayProxyResponseEvent handleInvalidRequestException(InvalidRequestException ex) {
        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        responseEvent.setBody(JsonUtil.toJsonString(ex.getApiError()));
        responseEvent.setStatusCode(SC_BAD_REQUEST);
        return responseEvent;
    }

    private APIGatewayProxyResponseEvent handleAmazonDynamoDBException(AmazonDynamoDBException ex) {

        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        ApiError error = new ApiError();
        error.setStatus(SC_INTERNAL_SERVER_ERROR);
        error.setError(ERROR_INTERNAL_SERVER_ERROR);
        error.setTimestamp(Instant.now());

        String message = String.format("AWS Error: %s %nReturned HTTP status code: %d %nAWS Returned error code: %s %nAWS service name: %s %nDetailed error message from the service: %s %nAWS request ID for the failed request: %s",
                ex.getMessage(),
                ex.getStatusCode(),
                ex.getErrorCode(),
                ex.getServiceName(),
                ex.getErrorMessage(),
                ex.getRequestId());

        error.setMessage(message);

        List<String> errorDetails = Arrays.stream(ex.getStackTrace()).map(e -> e.toString()).collect(Collectors.toList());
        error.setErrorDetails(errorDetails);

        responseEvent.setBody(JsonUtil.toJsonString(error));
        responseEvent.setStatusCode(SC_INTERNAL_SERVER_ERROR);
        return responseEvent;
    }

    private APIGatewayProxyResponseEvent handleAllException(Exception ex) {
        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        List<String> errorDetails = Arrays.stream(ex.getStackTrace()).map(e -> e.toString()).collect(Collectors.toList());
        ApiError error = ApiError.builder()
                .error(ERROR_INTERNAL_SERVER_ERROR)
                .message(ex.getMessage())
                .errorDetails(errorDetails)
                .status(SC_INTERNAL_SERVER_ERROR)
                .build();
        responseEvent.setBody(JsonUtil.toJsonString(error));
        responseEvent.setStatusCode(SC_INTERNAL_SERVER_ERROR);
        return responseEvent;
    }

}

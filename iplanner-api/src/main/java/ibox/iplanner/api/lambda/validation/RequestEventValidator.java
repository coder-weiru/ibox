package ibox.iplanner.api.lambda.validation;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.util.StringUtils;
import ibox.iplanner.api.lambda.exception.InvalidRequestEventException;
import ibox.iplanner.api.model.ApiError;
import lombok.RequiredArgsConstructor;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import static ibox.iplanner.api.util.ApiErrorConstants.ERROR_BAD_REQUEST;
import static ibox.iplanner.api.util.ApiErrorConstants.SC_BAD_REQUEST;


@RequiredArgsConstructor
public class RequestEventValidator {

    public static final String UUID_PATTERN = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}";

    private static final String REQUEST_BODY_SHOULD_NOT_BE_NULL_OR_BLANK_ERROR_MESSAGE = "Request body is null or empty";
    private static final String FIELD_SHOULD_NOT_BE_NULL_OR_BLANK_ERROR_MESSAGE = "Field %s should not be null or empty";
    private static final String REQUEST_PARAMETER_NOT_BLANK_ERROR_MESSAGE = "Missing request parameter or invalid request parameter found";
    private static final String PATH_PARAMETER_NOT_BLANK_ERROR_MESSAGE = "Missing path parameter or invalid path parameter found";
    private static final String PATH_PARAMETER_INVALID_PATTERN_ERROR_MESSAGE = "Invalid regex pattern for path parameter";
    private static final String FIELD_VALUE_NOT_MATCHING_PATTERN_ERROR_MESSAGE = "Value %s for field %s does not match the pattern [%s]";


    public void validateBody(final APIGatewayProxyRequestEvent event) {
        String requestBody = event.getBody();
        if (StringUtils.isNullOrEmpty(requestBody)) {
            ApiError error = ApiError.builder()
                    .error(ERROR_BAD_REQUEST)
                    .message(REQUEST_BODY_SHOULD_NOT_BE_NULL_OR_BLANK_ERROR_MESSAGE)
                    .status(SC_BAD_REQUEST)
                    .build();
            throw new InvalidRequestEventException(REQUEST_BODY_SHOULD_NOT_BE_NULL_OR_BLANK_ERROR_MESSAGE, error);
        }

    }

    public void validateRequestParameterNotBlank(final APIGatewayProxyRequestEvent event, final String... parameterNames) {
        Map<String, String> requestParameterMap = event.getQueryStringParameters();
        List<String> errorList = new ArrayList<>();
        for (String param : parameterNames) {
            final String value = Optional.ofNullable(requestParameterMap)
                    .map(mapNode -> mapNode.get(param))
                    .orElse(null);
            if (StringUtils.isNullOrEmpty(value)) {
                errorList.add(String.format(FIELD_SHOULD_NOT_BE_NULL_OR_BLANK_ERROR_MESSAGE, param));
            }
        }
        if (!errorList.isEmpty()) {
            ApiError error = ApiError.builder()
                    .error(ERROR_BAD_REQUEST)
                    .message(REQUEST_PARAMETER_NOT_BLANK_ERROR_MESSAGE)
                    .errorDetails(errorList)
                    .status(SC_BAD_REQUEST)
                    .build();
            throw new InvalidRequestEventException(REQUEST_PARAMETER_NOT_BLANK_ERROR_MESSAGE, error);
        }
    }

    public void validatePathParameterNotBlank(final APIGatewayProxyRequestEvent event, final String... parameterNames) {
        Map<String, String> pathParameterMap = event.getPathParameters();
        List<String> errorList = new ArrayList<>();
        for (String param : parameterNames) {
            final String value = Optional.ofNullable(pathParameterMap)
                    .map(mapNode -> mapNode.get(param))
                    .orElse(null);
            if (StringUtils.isNullOrEmpty(value)) {
                errorList.add(String.format(FIELD_SHOULD_NOT_BE_NULL_OR_BLANK_ERROR_MESSAGE, param));
            }
        }
        if (!errorList.isEmpty()) {
            ApiError error = ApiError.builder()
                    .error(ERROR_BAD_REQUEST)
                    .message(PATH_PARAMETER_NOT_BLANK_ERROR_MESSAGE)
                    .errorDetails(errorList)
                    .status(SC_BAD_REQUEST)
                    .build();
            throw new InvalidRequestEventException(PATH_PARAMETER_NOT_BLANK_ERROR_MESSAGE, error);
        }
    }

    public void validatePathParameterPattern(final APIGatewayProxyRequestEvent event, final String regexPattern, final String... parameterNames) {
        Map<String, String> pathParameterMap = event.getPathParameters();
        List<String> errorList = new ArrayList<>();
        for (String param : parameterNames) {
            final String value = Optional.ofNullable(pathParameterMap)
                    .map(mapNode -> mapNode.get(param))
                    .orElse(null);
            if (StringUtils.isNullOrEmpty(value)) {
                validatePathParameterNotBlank(event, param);
            } else {
                final Pattern pattern = Pattern.compile(regexPattern);
                if (!pattern.matcher(value).matches()) {
                    errorList.add(String.format(FIELD_VALUE_NOT_MATCHING_PATTERN_ERROR_MESSAGE, value, param, regexPattern));
                }
            }
        }
        if (!errorList.isEmpty()) {
            ApiError error = ApiError.builder()
                    .error(ERROR_BAD_REQUEST)
                    .message(PATH_PARAMETER_INVALID_PATTERN_ERROR_MESSAGE)
                    .errorDetails(errorList)
                    .status(SC_BAD_REQUEST)
                    .build();
            throw new InvalidRequestEventException(PATH_PARAMETER_INVALID_PATTERN_ERROR_MESSAGE, error);
        }
    }

}

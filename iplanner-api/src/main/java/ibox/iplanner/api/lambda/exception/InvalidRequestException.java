package ibox.iplanner.api.lambda.exception;

import ibox.iplanner.api.model.ApiError;

public class InvalidRequestException extends IllegalArgumentException {

    private ApiError apiError;

    public InvalidRequestException(final String message) {
        super(message);
    }

    public InvalidRequestException(final String message, final ApiError error) {
        super(message);
        this.apiError = error;
    }

    public ApiError getApiError() {
        return apiError;
    }
}

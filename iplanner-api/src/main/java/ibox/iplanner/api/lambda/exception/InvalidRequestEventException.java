package ibox.iplanner.api.lambda.exception;

import ibox.iplanner.api.model.ApiError;

public class InvalidRequestEventException extends InvalidRequestException {

    public InvalidRequestEventException(final String message, final ApiError error) {
        super(message, error);
    }
}

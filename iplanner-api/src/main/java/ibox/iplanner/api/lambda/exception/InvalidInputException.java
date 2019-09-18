package ibox.iplanner.api.lambda.exception;

import ibox.iplanner.api.model.ApiError;

public class InvalidInputException extends InvalidRequestException {

    public InvalidInputException(final String message, final ApiError error) {
        super(message, error);
    }
}

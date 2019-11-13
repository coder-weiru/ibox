package ibox.iplanner.api.lambda.exception;

import ibox.iplanner.api.model.ApiError;

import static ibox.iplanner.api.util.ApiErrorConstants.ERROR_BAD_REQUEST;
import static ibox.iplanner.api.util.ApiErrorConstants.SC_BAD_REQUEST;

public class RecordNotFoundException extends InvalidInputException {

    public RecordNotFoundException(final String message) {

        super(message, ApiError.builder()
                .error(ERROR_BAD_REQUEST)
                .message(message)
                .status(SC_BAD_REQUEST)
                .build());
    }
}

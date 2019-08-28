package ibox.iplanner.api.lambda;


import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import ibox.iplanner.api.model.ApiError;
import ibox.iplanner.api.model.GatewayResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;

public interface IPlannerRequestStreamHandler extends RequestStreamHandler {
    int SC_OK = 200;
    int SC_CREATED = 201;
    int SC_BAD_REQUEST = 400;
    int SC_NOT_FOUND = 404;
    int SC_CONFLICT = 409;
    int SC_INTERNAL_SERVER_ERROR = 500;

    Map<String, String> APPLICATION_JSON = Collections.singletonMap("Content-Type",
            "application/json");

    ApiError REQUEST_WAS_NULL_ERROR
            = ApiError.builder()
                .error("Bad Request")
                .message("Request was null")
                .status(SC_BAD_REQUEST)
                .build();

    /**
     * This method writes a body has invalid JSON response.
     * @param objectMapper the mapper to use for converting the error response to JSON.
     * @param output the output stream to write with the mapper.
     * @param details a detailed message describing why the JSON was invalid.
     * @throws IOException if there was an issue converting the ApiError object to JSON.
     */
    default void writeInvalidJsonInStreamResponse(ObjectMapper objectMapper,
                                                  OutputStream output,
                                                  String details) throws IOException {

        ApiError apiError = ApiError.builder()
                .error("Bad Request")
                .message("Invalid JSON in body: "
                        + details)
                .status(SC_BAD_REQUEST)
                .build();

        objectMapper.writeValue(output, new GatewayResponse<>(
                objectMapper.writeValueAsString(apiError),
                APPLICATION_JSON, SC_BAD_REQUEST));
    }

    default boolean isNullOrEmpty(final String string) {
        return string == null || string.isEmpty();
    }
}

package ibox.iplanner.api.lambda;

import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ibox.iplanner.api.config.DaggerIPlannerComponent;
import ibox.iplanner.api.config.IPlannerComponent;
import ibox.iplanner.api.model.ApiError;
import ibox.iplanner.api.model.Event;
import ibox.iplanner.api.model.GatewayResponse;
import ibox.iplanner.api.service.EventDataService;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class AddEventHandler implements IPlannerRequestStreamHandler {

    @Inject
    ObjectMapper objectMapper;
    @Inject
    EventDataService eventDataService;
    private final IPlannerComponent iPlannerComponent;

    public AddEventHandler() {
        iPlannerComponent = DaggerIPlannerComponent.builder().build();
        iPlannerComponent.inject(this);
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output,
                              Context context) throws IOException {
        final JsonNode event;
        try {
            event = objectMapper.readTree(input);
        } catch (JsonMappingException e) {
            writeInvalidJsonInStreamResponse(objectMapper, output, e.getMessage());
            return;
        }

        if (event == null) {
            writeInvalidJsonInStreamResponse(objectMapper, output, "event was null");
            return;
        }
        JsonNode requestBody = event.findValue("body");
        if (requestBody == null) {
            objectMapper.writeValue(output,
                    new GatewayResponse<>(
                            objectMapper.writeValueAsString(REQUEST_WAS_NULL_ERROR),
                            APPLICATION_JSON, SC_BAD_REQUEST));
            return;
        }

        final List<Event> newEvents;
        try {
            newEvents = objectMapper.treeToValue(
                    objectMapper.readTree(requestBody.asText()),
                    List.class);
        } catch (JsonParseException | JsonMappingException e) {
            ApiError apiError = ApiError.builder()
                    .error("Bad Request")
                    .message("Invalid JSON in body: "
                            + e.getMessage())
                    .status(SC_BAD_REQUEST)
                    .build();
            objectMapper.writeValue(output,
                    new GatewayResponse<>(
                            objectMapper.writeValueAsString(apiError),
                            APPLICATION_JSON, SC_BAD_REQUEST));
            return;
        }

        if (newEvents == null) {
            objectMapper.writeValue(output,
                    new GatewayResponse<>(
                            objectMapper.writeValueAsString(REQUEST_WAS_NULL_ERROR),
                            APPLICATION_JSON, SC_BAD_REQUEST));
            return;
        }

        try {
            List<Event> dbEvents = newEvents.stream().map(e-> {
                Event dbEvent = e;
                dbEvent.setId(UUID.randomUUID().toString());
                return dbEvent;
            }).collect(Collectors.toList());

            eventDataService.addEvents(dbEvents);
            objectMapper.writeValue(output,
                    new GatewayResponse<>(objectMapper.writeValueAsString(dbEvents),
                            APPLICATION_JSON, SC_CREATED));
        } catch (AmazonDynamoDBException e) {
            objectMapper.writeValue(output,
                    new GatewayResponse<>(
                            objectMapper.writeValueAsString(
                                    ApiError.builder()
                                            .error("Internal Server Error")
                                            .message("Error: "
                                                    + e.getMessage())
                                            .status(SC_INTERNAL_SERVER_ERROR)
                                            .build()),
                            APPLICATION_JSON, SC_INTERNAL_SERVER_ERROR));
        }
    }
}

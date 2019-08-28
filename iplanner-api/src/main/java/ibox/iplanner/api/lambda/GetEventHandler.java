package ibox.iplanner.api.lambda;

import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

import ibox.iplanner.api.config.DaggerIPlannerComponent;
import ibox.iplanner.api.config.IPlannerComponent;
import ibox.iplanner.api.model.ApiError;
import ibox.iplanner.api.model.Event;
import ibox.iplanner.api.model.GatewayResponse;
import ibox.iplanner.api.service.EventDataService;
public class GetEventHandler implements IPlannerRequestStreamHandler {
    @Inject
    ObjectMapper objectMapper;
    @Inject
    EventDataService eventDataService;
    private final IPlannerComponent iPlannerComponent;

    public GetEventHandler() {
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
        final JsonNode pathParameterMap = event.findValue("pathParameters");
        final String eventId = Optional.ofNullable(pathParameterMap)
                .map(mapNode -> mapNode.get("eventId"))
                .map(JsonNode::asText)
                .orElse(null);
        if (isNullOrEmpty(eventId)) {
            objectMapper.writeValue(output,
                    new GatewayResponse<>(
                            objectMapper.writeValueAsString("Event ID not set"),
                            APPLICATION_JSON, SC_BAD_REQUEST));
            return;
        }
        try {
            Event eventObj = eventDataService.getEvent(eventId);
            objectMapper.writeValue(output,
                    new GatewayResponse<>(
                            objectMapper.writeValueAsString(eventObj),
                            APPLICATION_JSON, SC_OK));
        } catch (AmazonDynamoDBException e) {
            objectMapper.writeValue(output,
                    new GatewayResponse<>(
                            objectMapper.writeValueAsString(
                                    ApiError.builder()
                                            .error("Not Found")
                                            .message("Error: "
                                                    + e.getMessage())
                                            .status(SC_NOT_FOUND)
                                            .build()),
                            APPLICATION_JSON, SC_NOT_FOUND));
        }
    }
}

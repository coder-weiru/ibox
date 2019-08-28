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
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import ibox.iplanner.api.config.DaggerIPlannerComponent;
import ibox.iplanner.api.config.IPlannerComponent;
import ibox.iplanner.api.model.ApiError;
import ibox.iplanner.api.model.Event;
import ibox.iplanner.api.model.GatewayResponse;
import ibox.iplanner.api.service.EventDataService;
import ibox.iplanner.api.util.DateTimeUtil;

import static java.time.temporal.ChronoUnit.DAYS;


public class ListEventHandler implements IPlannerRequestStreamHandler {
    @Inject
    ObjectMapper objectMapper;
    @Inject
    EventDataService eventDataService;
    private final IPlannerComponent iPlannerComponent;

    public ListEventHandler() {
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
        final String creatorId = Optional.ofNullable(pathParameterMap)
                .map(mapNode -> mapNode.get("createdBy"))
                .map(JsonNode::asText)
                .orElse(null);
        if (isNullOrEmpty(creatorId)) {
            objectMapper.writeValue(output,
                    new GatewayResponse<>(
                            objectMapper.writeValueAsString("Creator ID not set"),
                            APPLICATION_JSON, SC_BAD_REQUEST));
            return;
        }
        final JsonNode requestParameterMap = event.findValue("requestParameters");
        final String startTime = Optional.ofNullable(requestParameterMap)
                .map(mapNode -> mapNode.get("start"))
                .map(JsonNode::asText)
                .orElse(null);
        Instant timeWindowStart = Instant.now();
        if (!isNullOrEmpty(startTime)) {
            timeWindowStart = DateTimeUtil.parseUTCDatetime(startTime);
        }
        final String endTime = Optional.ofNullable(requestParameterMap)
                .map(mapNode -> mapNode.get("end"))
                .map(JsonNode::asText)
                .orElse(null);
        Instant timeWindowEnd = timeWindowStart.plus(365, DAYS);
        if (!isNullOrEmpty(endTime)) {
            timeWindowEnd = DateTimeUtil.parseUTCDatetime(endTime);
        }
        final String limit = Optional.ofNullable(requestParameterMap)
                .map(mapNode -> mapNode.get("limit"))
                .map(JsonNode::asText)
                .orElse(null);
        Integer queryLimit = 100;
        if (!isNullOrEmpty(limit)) {
            queryLimit = Integer.valueOf(limit);
        }
        try {
            List<Event> eventList = eventDataService.getMyEventsWithinTime(creatorId, timeWindowStart, timeWindowEnd, queryLimit);
            objectMapper.writeValue(output,
                    new GatewayResponse<>(
                            objectMapper.writeValueAsString(eventList),
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


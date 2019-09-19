package ibox.iplanner.api.lambda.handler;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import ibox.iplanner.api.config.DaggerIPlannerComponent;
import ibox.iplanner.api.config.IPlannerComponent;
import ibox.iplanner.api.lambda.runtime.TestContext;
import ibox.iplanner.api.model.ApiError;
import ibox.iplanner.api.util.JsonUtil;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;

import static ibox.iplanner.api.util.ApiErrorConstants.ERROR_BAD_REQUEST;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetEntityDefinitionHandlerTest {

    private GetEntityDefinitionHandler handler = new GetEntityDefinitionHandler();

    public GetEntityDefinitionHandlerTest() {
        IPlannerComponent iPlannerComponent = DaggerIPlannerComponent.builder().build();
        iPlannerComponent.inject(handler);
    }

    @Test
    public void getMeetingSchema_shouldReturnValidJsonSchema() throws Exception {

        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setQueryStringParameters(Collections.singletonMap("type", "meeting"));
        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(200, responseEvent.getStatusCode());

        String jsonSchema = responseEvent.getBody();
        assertThat(jsonSchema, is(equalTo(loadSchema("meeting"))));
    }

    @Test
    public void getTaskSchema_shouldReturnValidJsonSchema() throws Exception {

        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setQueryStringParameters(Collections.singletonMap("type", "task"));
        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(200, responseEvent.getStatusCode());

        String jsonSchema = responseEvent.getBody();
        assertThat(jsonSchema, is(equalTo(loadSchema("task"))));
    }

    @Test
    public void getActivitySchema_shouldReturnValidJsonSchema() throws Exception {

        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setQueryStringParameters(Collections.singletonMap("type", "activity"));
        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(200, responseEvent.getStatusCode());

        String jsonSchema = responseEvent.getBody();
        assertThat(jsonSchema, is(equalTo(loadSchema("activity"))));
    }

    @Test
    public void getEventSchema_shouldReturnValidJsonSchema() throws Exception {

        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setQueryStringParameters(Collections.singletonMap("type", "event"));
        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(200, responseEvent.getStatusCode());

        String jsonSchema = responseEvent.getBody();
        assertThat(jsonSchema, is(equalTo(loadSchema("event"))));
    }

    @Test
    public void getActivitySchema_shouldReturnBadRequestMessageIfTypeIsInvalid() throws Exception {
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setQueryStringParameters(Collections.singletonMap("type", "abc"));
        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(400, responseEvent.getStatusCode());

        ApiError error = JsonUtil.fromJsonString(responseEvent.getBody(), ApiError.class);
        assertEquals(400, error.getStatus());
        assertEquals(ERROR_BAD_REQUEST, error.getError());
        assertFalse(StringUtils.isNullOrEmpty(error.getMessage()));
    }

    private String loadSchema(String type) throws URISyntaxException, IOException {
        final String RESOURCE_BASE = "/schema/ibox/iplanner/api/model";
        final JsonNode jsonSchema = JsonLoader.fromResource(String.format("%s/%s.schema.json", RESOURCE_BASE, type));
        return jsonSchema.toString();
    }
}

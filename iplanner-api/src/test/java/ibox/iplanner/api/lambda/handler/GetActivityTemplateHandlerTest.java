package ibox.iplanner.api.lambda.handler;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.util.StringUtils;
import ibox.iplanner.api.config.DaggerIPlannerComponent;
import ibox.iplanner.api.config.IPlannerComponent;
import ibox.iplanner.api.lambda.runtime.TestContext;
import ibox.iplanner.api.model.*;
import ibox.iplanner.api.util.JsonUtil;
import org.junit.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.Map;

import static ibox.iplanner.api.util.ApiErrorConstants.ERROR_INTERNAL_SERVER_ERROR;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetActivityTemplateHandlerTest {

    private GetActivityTemplateHandler handler = new GetActivityTemplateHandler();

    public GetActivityTemplateHandlerTest() {
        IPlannerComponent iPlannerComponent = DaggerIPlannerComponent.builder().build();
        iPlannerComponent.inject(handler);
    }

    @Test
    public void getActivityTemplate_shouldReturnSpecifiedActivities() throws Exception {

        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setBody(JsonUtil.toJsonString(Arrays.asList( new String[] {"myTask", "myMeeting"})));
        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(200, responseEvent.getStatusCode());

        String response = responseEvent.getBody();
        Map<String, Activity> activities = (Map<String, Activity>) JsonUtil.fromJsonString(response, Map.class, String.class, Activity.class);

        assertThat(activities.size(), equalTo(2));
        assertThat(activities.get("myMeeting").getTitle(), equalTo("My Meeting"));
        assertThat(activities.get("myMeeting").getDescription(), equalTo("Description for a meeting"));
        assertThat(activities.get("myMeeting").getType(), equalTo("meeting"));
        assertThat(activities.get("myMeeting").getStatus(), equalTo("ACTIVE"));
        assertThat(((Meeting)activities.get("myMeeting")).getFrequency(), equalTo(Frequency.DAILY));

        assertThat(activities.get("myTask").getTitle(), equalTo("My Task"));
        assertThat(activities.get("myTask").getDescription(), equalTo("Description for a task"));
        assertThat(activities.get("myTask").getType(), equalTo("task"));
        assertThat(activities.get("myTask").getStatus(), equalTo("ACTIVE"));
        assertThat(((Task)activities.get("myTask")).getDeadline(), equalTo(Instant.parse("2099-12-31T00:00:00.000Z")));

    }

    @Test
    public void getActivityTemplate_shouldReturnNotFoundIfUnknownNameSpecified() {
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setBody(JsonUtil.toJsonString(Arrays.asList( new String[] {"abc"})));
        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(404, responseEvent.getStatusCode());

    }

    @Test
    public void getActivityTemplate_shouldSkipUnknownNameButReturnOnlyFoundActivity() {
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setBody(JsonUtil.toJsonString(Arrays.asList( new String[] {"abc", "myMeeting"})));
        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(200, responseEvent.getStatusCode());

        String response = responseEvent.getBody();
        Map<String, Activity> activities = (Map<String, Activity>) JsonUtil.fromJsonString(response, Map.class, String.class, Activity.class);

        assertThat(activities.size(), equalTo(1));
        assertThat(activities.get("myMeeting").getTitle(), equalTo("My Meeting"));
        assertThat(activities.get("myMeeting").getDescription(), equalTo("Description for a meeting"));
        assertThat(activities.get("myMeeting").getType(), equalTo("meeting"));
        assertThat(activities.get("myMeeting").getStatus(), equalTo("ACTIVE"));
        assertThat(((Meeting)activities.get("myMeeting")).getFrequency(), equalTo(Frequency.DAILY));

    }

    @Test
    public void getActivityTemplate_shouldHandleInvalidRequestBody() {
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setBody(JsonUtil.toJsonString("abc"));
        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, TestContext.builder().build());

        assertEquals(500, responseEvent.getStatusCode());

        ApiError error = JsonUtil.fromJsonString(responseEvent.getBody(), ApiError.class);
        assertEquals(500, error.getStatus());
        assertEquals(ERROR_INTERNAL_SERVER_ERROR, error.getError());
        assertFalse(StringUtils.isNullOrEmpty(error.getMessage()));
        assertFalse(error.getErrorDetails().isEmpty());
    }
}

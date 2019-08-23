package ibox.iplanner.api.controller;

import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import ibox.iplanner.api.model.Event;
import ibox.iplanner.api.service.EventDataService;
import ibox.iplanner.api.service.EventUtil;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@RunWith(SpringRunner.class)
@WebMvcTest(EventController.class)
@AutoConfigureMockMvc
public class EventControllerListEventTest {

    @MockBean
    private EventDataService eventDataServiceMock;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void listEvents_shouldInvokeEventDateServiceGivenCorrectParams() throws Exception {
        List<Event> events = Arrays.asList(new Event[]{
                EventUtil.anyEvent(),
                EventUtil.anyEvent(),
                EventUtil.anyEvent()
        });

        when(eventDataServiceMock.getMyEventsWithinTime(any(String.class), any(Instant.class), any(Instant.class), any(Integer.class))).thenReturn(events);

        Instant now = Instant.now();
        String creatorId = UUID.randomUUID().toString();
        String windowStart = now.plus(1, MINUTES).toString();
        String windowEnd = now.plus(100, MINUTES).toString();
        String limit = "10";

        mockMvc.perform(get("/events/createdBy/" + creatorId)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("start", windowStart)
                .param("end", windowEnd)
                .param("limit", limit))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(3)))
        ;

        ArgumentCaptor<String> eventIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Instant> windowStartCaptor = ArgumentCaptor.forClass(Instant.class);
        ArgumentCaptor<Instant> windowEndCaptor = ArgumentCaptor.forClass(Instant.class);
        ArgumentCaptor<Integer> limitCaptor = ArgumentCaptor.forClass(Integer.class);

        verify(eventDataServiceMock, times(1)).getMyEventsWithinTime(eventIdCaptor.capture(), windowStartCaptor.capture(), windowEndCaptor.capture(), limitCaptor.capture());

        verifyNoMoreInteractions(eventDataServiceMock);

        assertThat(eventIdCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(creatorId)));
        assertThat(windowStartCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(Instant.parse(windowStart))));
        assertThat(windowEndCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(Instant.parse(windowEnd))));
        assertThat(limitCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(Integer.valueOf(limit))));
    }

    @Test
    public void listEvents_shouldReturnBadRequestMessageIfCreatorIdInvalid() throws Exception {
        mockMvc.perform(get("/events/createdBy/abc")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errorDetails").isNotEmpty());
    }

    @Test
    public void listEvents_shouldInvokeEventDateServiceEvenStartIsNotSpecified() throws Exception {
        List<Event> events = Arrays.asList(new Event[]{
                EventUtil.anyEvent(),
                EventUtil.anyEvent(),
                EventUtil.anyEvent()
        });

        when(eventDataServiceMock.getMyEventsWithinTime(any(String.class), any(Instant.class), any(Instant.class), any(Integer.class))).thenReturn(events);

        Instant now = Instant.now();
        String creatorId = UUID.randomUUID().toString();
        String windowEnd = now.plus(100, MINUTES).toString();
        String limit = "10";

        mockMvc.perform(get("/events/createdBy/" + creatorId)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("end", windowEnd)
                .param("limit", limit))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(3)))
        ;

        ArgumentCaptor<String> eventIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Instant> windowStartCaptor = ArgumentCaptor.forClass(Instant.class);
        ArgumentCaptor<Instant> windowEndCaptor = ArgumentCaptor.forClass(Instant.class);
        ArgumentCaptor<Integer> limitCaptor = ArgumentCaptor.forClass(Integer.class);

        verify(eventDataServiceMock, times(1)).getMyEventsWithinTime(eventIdCaptor.capture(), windowStartCaptor.capture(), windowEndCaptor.capture(), limitCaptor.capture());

        verifyNoMoreInteractions(eventDataServiceMock);

        assertThat(eventIdCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(creatorId)));
        assertThat(windowStartCaptor.getValue(), CoreMatchers.notNullValue());
        assertThat(windowEndCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(Instant.parse(windowEnd))));
        assertThat(limitCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(Integer.valueOf(limit))));
    }

    @Test
    public void listEvents_shouldInvokeEventDateServiceEvenEndIsNotSpecified() throws Exception {
        List<Event> events = Arrays.asList(new Event[]{
                EventUtil.anyEvent(),
                EventUtil.anyEvent(),
                EventUtil.anyEvent()
        });

        when(eventDataServiceMock.getMyEventsWithinTime(any(String.class), any(Instant.class), any(Instant.class), any(Integer.class))).thenReturn(events);

        Instant now = Instant.now();
        String creatorId = UUID.randomUUID().toString();
        String windowStart = now.plus(1, MINUTES).toString();
        String limit = "10";

        mockMvc.perform(get("/events/createdBy/" + creatorId)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("start", windowStart)
                .param("limit", limit))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(3)))
        ;

        ArgumentCaptor<String> eventIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Instant> windowStartCaptor = ArgumentCaptor.forClass(Instant.class);
        ArgumentCaptor<Instant> windowEndCaptor = ArgumentCaptor.forClass(Instant.class);
        ArgumentCaptor<Integer> limitCaptor = ArgumentCaptor.forClass(Integer.class);

        verify(eventDataServiceMock, times(1)).getMyEventsWithinTime(eventIdCaptor.capture(), windowStartCaptor.capture(), windowEndCaptor.capture(), limitCaptor.capture());

        verifyNoMoreInteractions(eventDataServiceMock);

        assertThat(eventIdCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(creatorId)));
        assertThat(windowStartCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(Instant.parse(windowStart))));
        assertThat(windowEndCaptor.getValue(), CoreMatchers.notNullValue());
        assertThat(limitCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(Integer.valueOf(limit))));
    }

    @Test
    public void listEvents_shouldInvokeEventDateServiceEvenLimitIsNotSpecified() throws Exception {
        List<Event> events = Arrays.asList(new Event[]{
                EventUtil.anyEvent(),
                EventUtil.anyEvent(),
                EventUtil.anyEvent()
        });

        when(eventDataServiceMock.getMyEventsWithinTime(any(String.class), any(Instant.class), any(Instant.class), any(Integer.class))).thenReturn(events);

        Instant now = Instant.now();
        String creatorId = UUID.randomUUID().toString();
        String windowStart = now.plus(1, MINUTES).toString();
        String windowEnd = now.plus(100, MINUTES).toString();

        mockMvc.perform(get("/events/createdBy/" + creatorId)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("start", windowStart)
                .param("end", windowEnd))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(3)))
        ;

        ArgumentCaptor<String> eventIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Instant> windowStartCaptor = ArgumentCaptor.forClass(Instant.class);
        ArgumentCaptor<Instant> windowEndCaptor = ArgumentCaptor.forClass(Instant.class);
        ArgumentCaptor<Integer> limitCaptor = ArgumentCaptor.forClass(Integer.class);

        verify(eventDataServiceMock, times(1)).getMyEventsWithinTime(eventIdCaptor.capture(), windowStartCaptor.capture(), windowEndCaptor.capture(), limitCaptor.capture());

        verifyNoMoreInteractions(eventDataServiceMock);

        assertThat(eventIdCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(creatorId)));
        assertThat(windowStartCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(Instant.parse(windowStart))));
        assertThat(windowEndCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(Instant.parse(windowEnd))));
        assertThat(limitCaptor.getValue(), CoreMatchers.is(CoreMatchers.equalTo(Integer.valueOf(100))));
    }

    @Test
    public void listEvents_shouldReturnInternalServerErrorMessageIfAmazonServiceExceptionIsThrown() throws Exception {
        Event event = EventUtil.anyEvent();
        event.setId(UUID.randomUUID().toString());
        event.getCreator().setId(UUID.randomUUID().toString());

        AmazonDynamoDBException amazonDynamoDBException = new AmazonDynamoDBException("dynamo db error");
        amazonDynamoDBException.setStatusCode(HttpStatus.NOT_FOUND.value());
        amazonDynamoDBException.setErrorCode("AWSERR");
        amazonDynamoDBException.setServiceName("QueryItem");
        amazonDynamoDBException.setRequestId("request1");
        amazonDynamoDBException.setErrorMessage("error message");

        doThrow(amazonDynamoDBException).when(eventDataServiceMock).getMyEventsWithinTime(any(String.class), any(Instant.class), any(Instant.class), any(Integer.class));

        String creatorId = UUID.randomUUID().toString();

        mockMvc.perform(get("/events/createdBy/" + creatorId)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(jsonPath("$.status").value("500"))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errorDetails").isNotEmpty())
                .andExpect(jsonPath("$.errorDetails[0]").value(containsString(amazonDynamoDBException.getStatusCode()+"")))
                .andExpect(jsonPath("$.errorDetails[1]").value(containsString(amazonDynamoDBException.getErrorCode())))
                .andExpect(jsonPath("$.errorDetails[2]").value(containsString(amazonDynamoDBException.getErrorMessage())))
                .andExpect(jsonPath("$.errorDetails[3]").value(containsString(amazonDynamoDBException.getServiceName())))
                .andExpect(jsonPath("$.errorDetails[4]").value(containsString(amazonDynamoDBException.getRequestId())));
    }


}
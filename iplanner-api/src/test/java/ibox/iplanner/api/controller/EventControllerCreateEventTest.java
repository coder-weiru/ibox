package ibox.iplanner.api.controller;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import ibox.iplanner.api.model.Event;
import ibox.iplanner.api.model.User;
import ibox.iplanner.api.service.EventDataService;
import ibox.iplanner.api.service.EventUtil;
import ibox.iplanner.api.util.JsonUtil;
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

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsEqual.equalToObject;
import static org.hamcrest.core.IsIterableContaining.hasItems;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(EventController.class)
@AutoConfigureMockMvc
public class EventControllerCreateEventTest {

    @MockBean
    private EventDataService eventDataServiceMock;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void createEvent_shouldInvokeEventDateServiceWithEventList() throws Exception {
        List<Event> events = Arrays.asList(new Event[]{
                EventUtil.anyEvent(),
                EventUtil.anyEvent(),
                EventUtil.anyEvent()
        });

        doNothing().when(eventDataServiceMock).addEvents(any(List.class));

        mockMvc.perform(post("/events")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JsonUtil.toJsonString(events)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id").value(not(events.get(0).getId())))
                .andExpect(jsonPath("$[0].summary").value(events.get(0).getSummary()))
                .andExpect(jsonPath("$[0].description").value(events.get(0).getDescription()))
                .andExpect(jsonPath("$[0].activity").value(events.get(0).getActivity()))
                .andExpect(jsonPath("$[0].status").value(events.get(0).getStatus()))
                .andExpect(jsonPath("$[0].location").value(events.get(0).getLocation()))
                .andExpect(jsonPath("$[0].creator.id").value(events.get(0).getCreator().getId()))
                .andExpect(jsonPath("$[0].creator.email").value(events.get(0).getCreator().getEmail()))
                .andExpect(jsonPath("$[0].creator.displayName").value(events.get(0).getCreator().getDisplayName()))
                .andExpect(jsonPath("$[0].creator.self").value(events.get(0).getCreator().getSelf()))
                .andExpect(jsonPath("$[0].created").value(events.get(0).getCreated().toString()))
                .andExpect(jsonPath("$[0].updated").value(events.get(0).getUpdated().toString()))
                .andExpect(jsonPath("$[0].start").value(events.get(0).getStart().toString()))
                .andExpect(jsonPath("$[0].end").value(events.get(0).getEnd().toString()))
                .andExpect(jsonPath("$[0].endTimeUnspecified").value(events.get(0).getEndTimeUnspecified()))
                .andExpect(jsonPath("$[0].recurrence", hasSize(events.get(0).getRecurrence().size())))
                .andExpect(jsonPath("$[0].recurrence", hasItems(events.get(0).getRecurrence().toArray(new String[events.get(0).getRecurrence().size()]))))
                .andExpect(jsonPath("$[1].id").value(not(events.get(1).getId())))
                .andExpect(jsonPath("$[1].summary").value(events.get(1).getSummary()))
                .andExpect(jsonPath("$[1].description").value(events.get(1).getDescription()))
                .andExpect(jsonPath("$[1].activity").value(events.get(1).getActivity()))
                .andExpect(jsonPath("$[1].status").value(events.get(1).getStatus()))
                .andExpect(jsonPath("$[1].location").value(events.get(1).getLocation()))
                .andExpect(jsonPath("$[1].creator.id").value(events.get(1).getCreator().getId()))
                .andExpect(jsonPath("$[1].creator.email").value(events.get(1).getCreator().getEmail()))
                .andExpect(jsonPath("$[1].creator.displayName").value(events.get(1).getCreator().getDisplayName()))
                .andExpect(jsonPath("$[1].creator.self").value(events.get(1).getCreator().getSelf()))
                .andExpect(jsonPath("$[1].created").value(events.get(1).getCreated().toString()))
                .andExpect(jsonPath("$[1].updated").value(events.get(1).getUpdated().toString()))
                .andExpect(jsonPath("$[1].start").value(events.get(1).getStart().toString()))
                .andExpect(jsonPath("$[1].end").value(events.get(1).getEnd().toString()))
                .andExpect(jsonPath("$[1].endTimeUnspecified").value(events.get(1).getEndTimeUnspecified()))
                .andExpect(jsonPath("$[1].recurrence", hasSize(events.get(1).getRecurrence().size())))
                .andExpect(jsonPath("$[1].recurrence", hasItems(events.get(1).getRecurrence().toArray(new String[events.get(1).getRecurrence().size()]))))
                .andExpect(jsonPath("$[2].id").value(not(events.get(2).getId())))
                .andExpect(jsonPath("$[2].summary").value(events.get(2).getSummary()))
                .andExpect(jsonPath("$[2].description").value(events.get(2).getDescription()))
                .andExpect(jsonPath("$[2].activity").value(events.get(2).getActivity()))
                .andExpect(jsonPath("$[2].status").value(events.get(2).getStatus()))
                .andExpect(jsonPath("$[2].location").value(events.get(2).getLocation()))
                .andExpect(jsonPath("$[2].creator.id").value(events.get(2).getCreator().getId()))
                .andExpect(jsonPath("$[2].creator.email").value(events.get(2).getCreator().getEmail()))
                .andExpect(jsonPath("$[2].creator.displayName").value(events.get(2).getCreator().getDisplayName()))
                .andExpect(jsonPath("$[2].creator.self").value(events.get(2).getCreator().getSelf()))
                .andExpect(jsonPath("$[2].created").value(events.get(2).getCreated().toString()))
                .andExpect(jsonPath("$[2].updated").value(events.get(2).getUpdated().toString()))
                .andExpect(jsonPath("$[2].start").value(events.get(2).getStart().toString()))
                .andExpect(jsonPath("$[2].end").value(events.get(2).getEnd().toString()))
                .andExpect(jsonPath("$[2].endTimeUnspecified").value(events.get(2).getEndTimeUnspecified()))
                .andExpect(jsonPath("$[2].recurrence", hasSize(events.get(2).getRecurrence().size())))
                .andExpect(jsonPath("$[2].recurrence", hasItems(events.get(2).getRecurrence().toArray(new String[events.get(2).getRecurrence().size()]))));

        ArgumentCaptor<List> requestCaptor = ArgumentCaptor.forClass(List.class);

        verify(eventDataServiceMock, times(1)).addEvents(requestCaptor.capture());

        verifyNoMoreInteractions(eventDataServiceMock);

        List<Event> argument = requestCaptor.getValue();

        assertThat(argument.size(), is(equalTo(events.size())));
        assertThat(argument.get(0).getId(), not(equalToObject(events.get(0).getId())));
        assertThat(argument.get(0).getSummary(), is(equalToObject(events.get(0).getSummary())));
        assertThat(argument.get(0).getDescription(), is(equalToObject(events.get(0).getDescription())));
        assertThat(argument.get(0).getActivity(), is(equalToObject(events.get(0).getActivity())));
        assertThat(argument.get(0).getStatus(), is(equalToObject(events.get(0).getStatus())));
        assertThat(argument.get(0).getLocation(), is(equalToObject(events.get(0).getLocation())));
        assertThat(argument.get(0).getCreator().getId(), is(equalToObject(events.get(0).getCreator().getId())));
        assertThat(argument.get(0).getCreator().getEmail(), is(equalToObject(events.get(0).getCreator().getEmail())));
        assertThat(argument.get(0).getCreator().getDisplayName(), is(equalToObject(events.get(0).getCreator().getDisplayName())));
        assertThat(argument.get(0).getCreator().getSelf(), is(equalToObject(events.get(0).getCreator().getSelf())));
        assertThat(argument.get(0).getCreated(), is(equalToObject(events.get(0).getCreated())));
        assertThat(argument.get(0).getUpdated(), is(equalToObject(events.get(0).getUpdated())));
        assertThat(argument.get(0).getStart(), is(equalToObject(events.get(0).getStart())));
        assertThat(argument.get(0).getEnd(), is(equalToObject(events.get(0).getEnd())));
        assertThat(argument.get(0).getEndTimeUnspecified(), is(equalToObject(events.get(0).getEndTimeUnspecified())));
        assertThat(argument.get(0).getRecurrence(), is(hasItems(events.get(0).getRecurrence().toArray(new String[events.get(0).getRecurrence().size()]))));

        assertThat(argument.get(1).getId(), not(equalToObject(events.get(1).getId())));
        assertThat(argument.get(1).getSummary(), is(equalToObject(events.get(1).getSummary())));
        assertThat(argument.get(1).getDescription(), is(equalToObject(events.get(1).getDescription())));
        assertThat(argument.get(1).getActivity(), is(equalToObject(events.get(1).getActivity())));
        assertThat(argument.get(1).getStatus(), is(equalToObject(events.get(1).getStatus())));
        assertThat(argument.get(1).getLocation(), is(equalToObject(events.get(1).getLocation())));
        assertThat(argument.get(1).getCreator().getId(), is(equalToObject(events.get(1).getCreator().getId())));
        assertThat(argument.get(1).getCreator().getEmail(), is(equalToObject(events.get(1).getCreator().getEmail())));
        assertThat(argument.get(1).getCreator().getDisplayName(), is(equalToObject(events.get(1).getCreator().getDisplayName())));
        assertThat(argument.get(1).getCreator().getSelf(), is(equalToObject(events.get(1).getCreator().getSelf())));
        assertThat(argument.get(1).getCreated(), is(equalToObject(events.get(1).getCreated())));
        assertThat(argument.get(1).getUpdated(), is(equalToObject(events.get(1).getUpdated())));
        assertThat(argument.get(1).getStart(), is(equalToObject(events.get(1).getStart())));
        assertThat(argument.get(1).getEnd(), is(equalToObject(events.get(1).getEnd())));
        assertThat(argument.get(1).getEndTimeUnspecified(), is(equalToObject(events.get(1).getEndTimeUnspecified())));
        assertThat(argument.get(1).getRecurrence(), is(hasItems(events.get(1).getRecurrence().toArray(new String[events.get(1).getRecurrence().size()]))));

        assertThat(argument.get(2).getId(), not(equalToObject(events.get(2).getId())));
        assertThat(argument.get(2).getSummary(), is(equalToObject(events.get(2).getSummary())));
        assertThat(argument.get(2).getDescription(), is(equalToObject(events.get(2).getDescription())));
        assertThat(argument.get(2).getActivity(), is(equalToObject(events.get(2).getActivity())));
        assertThat(argument.get(2).getStatus(), is(equalToObject(events.get(2).getStatus())));
        assertThat(argument.get(2).getLocation(), is(equalToObject(events.get(2).getLocation())));
        assertThat(argument.get(2).getCreator().getId(), is(equalToObject(events.get(2).getCreator().getId())));
        assertThat(argument.get(2).getCreator().getEmail(), is(equalToObject(events.get(2).getCreator().getEmail())));
        assertThat(argument.get(2).getCreator().getDisplayName(), is(equalToObject(events.get(2).getCreator().getDisplayName())));
        assertThat(argument.get(2).getCreator().getSelf(), is(equalToObject(events.get(2).getCreator().getSelf())));
        assertThat(argument.get(2).getCreated(), is(equalToObject(events.get(2).getCreated())));
        assertThat(argument.get(2).getUpdated(), is(equalToObject(events.get(2).getUpdated())));
        assertThat(argument.get(2).getStart(), is(equalToObject(events.get(2).getStart())));
        assertThat(argument.get(2).getEnd(), is(equalToObject(events.get(2).getEnd())));
        assertThat(argument.get(2).getEndTimeUnspecified(), is(equalToObject(events.get(2).getEndTimeUnspecified())));
        assertThat(argument.get(2).getRecurrence(), is(hasItems(events.get(2).getRecurrence().toArray(new String[events.get(2).getRecurrence().size()]))));
    }

    @Test
    public void createEvent_shouldReturnBadRequestMessageIfMissingSummary() throws Exception {
        Event event = EventUtil.anyEvent();
        event.setSummary(null);

        verifyBadRequestMessage(Arrays.asList(new Event[] {
                event
        }));
    }

    @Test
    public void createEvent_shouldReturnBadRequestMessageIfMissingCreator() throws Exception {
        Event event = EventUtil.anyEvent();
        event.setCreator(null);

        verifyBadRequestMessage(Arrays.asList(new Event[] {
                event
        }));
    }

    @Test
    public void createEvent_shouldReturnBadRequestMessageIfCreatorInvalid() throws Exception {
        Event event = EventUtil.anyEvent();

        User creator = new User();

        event.setCreator(creator);

        verifyBadRequestMessage(Arrays.asList(new Event[] {
                event
        }));
    }

    @Test
    public void createEvent_shouldReturnBadRequestMessageIfMissingActivity() throws Exception {
        Event event = EventUtil.anyEvent();
        event.setActivity(null);

        verifyBadRequestMessage(Arrays.asList(new Event[] {
                event
        }));
    }

    @Test
    public void createEvent_shouldReturnBadRequestMessageIfMissingStartTime() throws Exception {
        Event event = EventUtil.anyEvent();
        event.setStart(null);

        verifyBadRequestMessage(Arrays.asList(new Event[] {
                event
        }));
    }

    private void verifyBadRequestMessage(List<Event> events) throws Exception {
        mockMvc.perform(post("/events")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JsonUtil.toJsonString(events)))
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
    public void createEvent_shouldReturnInternalServerErrorMessageIfAmazonServiceExceptionIsThrown() throws Exception {
        Event event = EventUtil.anyEvent();

        AmazonDynamoDBException amazonDynamoDBException = new AmazonDynamoDBException("dynamo db error");
        amazonDynamoDBException.setStatusCode(HttpStatus.NOT_FOUND.value());
        amazonDynamoDBException.setErrorCode("AWSERR");
        amazonDynamoDBException.setServiceName("PutItem");
        amazonDynamoDBException.setRequestId("request1");
        amazonDynamoDBException.setErrorMessage("error message");

        doThrow(amazonDynamoDBException).when(eventDataServiceMock).addEvents(any(List.class));

        mockMvc.perform(post("/events")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JsonUtil.toJsonString(Arrays.asList(new Event[] {
                        event
                }))))
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
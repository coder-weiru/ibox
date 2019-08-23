package ibox.iplanner.api.controller;

import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import ibox.iplanner.api.model.Event;
import ibox.iplanner.api.service.EventDataService;
import ibox.iplanner.api.service.EventUtil;
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

import java.util.UUID;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsIterableContaining.hasItems;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(EventController.class)
@AutoConfigureMockMvc
public class EventControllerGetEventTest {

    @MockBean
    private EventDataService eventDataServiceMock;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getEvent_shouldInvokeEventDateServiceWithEventId() throws Exception {
        Event event = EventUtil.anyEvent();
        event.setId(UUID.randomUUID().toString());

        when(eventDataServiceMock.getEvent(any(String.class))).thenReturn(event);

        mockMvc.perform(get("/events/" + event.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(jsonPath("$.id").value(event.getId()))
                .andExpect(jsonPath("$.summary").value(event.getSummary()))
                .andExpect(jsonPath("$.description").value(event.getDescription()))
                .andExpect(jsonPath("$.activity").value(event.getActivity()))
                .andExpect(jsonPath("$.status").value(event.getStatus()))
                .andExpect(jsonPath("$.location").value(event.getLocation()))
                .andExpect(jsonPath("$.creator.id").value(event.getCreator().getId()))
                .andExpect(jsonPath("$.creator.email").value(event.getCreator().getEmail()))
                .andExpect(jsonPath("$.creator.displayName").value(event.getCreator().getDisplayName()))
                .andExpect(jsonPath("$.creator.self").value(event.getCreator().getSelf()))
                .andExpect(jsonPath("$.created").value(event.getCreated().toString()))
                .andExpect(jsonPath("$.updated").value(event.getUpdated().toString()))
                .andExpect(jsonPath("$.start").value(event.getStart().toString()))
                .andExpect(jsonPath("$.end").value(event.getEnd().toString()))
                .andExpect(jsonPath("$.endTimeUnspecified").value(event.getEndTimeUnspecified()))
                .andExpect(jsonPath("$.recurrence", hasSize(event.getRecurrence().size())))
                .andExpect(jsonPath("$.recurrence", hasItems(event.getRecurrence().toArray(new String[event.getRecurrence().size()]))));

        ArgumentCaptor<String> eventIdCaptor = ArgumentCaptor.forClass(String.class);

        verify(eventDataServiceMock, times(1)).getEvent(eventIdCaptor.capture());

        verifyNoMoreInteractions(eventDataServiceMock);

        String argument = eventIdCaptor.getValue();

        assertThat(argument, is(equalTo(event.getId())));
    }

    @Test
    public void getEvent_shouldReturnBadRequestMessageIfEventIdInvalid() throws Exception {
        mockMvc.perform(get("/events/abc")
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
    public void getEvent_shouldReturnInternalServerErrorMessageIfAmazonServiceExceptionIsThrown() throws Exception {
        Event event = EventUtil.anyEvent();
        event.setId(UUID.randomUUID().toString());

        AmazonDynamoDBException amazonDynamoDBException = new AmazonDynamoDBException("dynamo db error");
        amazonDynamoDBException.setStatusCode(HttpStatus.NOT_FOUND.value());
        amazonDynamoDBException.setErrorCode("AWSERR");
        amazonDynamoDBException.setServiceName("GetItem");
        amazonDynamoDBException.setRequestId("request1");
        amazonDynamoDBException.setErrorMessage("error message");

        doThrow(amazonDynamoDBException).when(eventDataServiceMock).getEvent(any(String.class));

        mockMvc.perform(get("/events/" + event.getId())
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
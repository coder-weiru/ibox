package ibox.iplanner.api.controller;


import ibox.iplanner.api.model.Event;
import ibox.iplanner.api.service.EventDataService;
import ibox.iplanner.api.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;


@RestController
@Validated
public class EventController {

    private EventDataService eventDataService;

    @Autowired
    public EventController(final EventDataService eventDataService) {
        this.eventDataService = eventDataService;
    }

    @RequestMapping(path = "/events", method = RequestMethod.POST)
    public List<Event> createEvent(@Valid @RequestBody final List<Event> newEvents) {

        List<Event> dbEvents = newEvents.stream().map(e-> {
            Event dbEvent = e;
            dbEvent.setId(UUID.randomUUID().toString());
            return dbEvent;
        }).collect(Collectors.toList());

        eventDataService.addEvents(dbEvents);

        return dbEvents;
    }

    @RequestMapping(path = "/events/createdBy/{creatorId}", method = RequestMethod.GET)
    public List<Event> listEvents(@PathVariable("creatorId") @NotBlank final Optional<String> creatorId,
                                  @RequestParam("start") final Optional<String> start,
                                  @RequestParam("end") final Optional<String> end,
                                  @RequestParam("limit") final Optional<Integer> limit) {
        int queryLimit = 100;
        if (limit.isPresent()) {
            queryLimit = limit.get();
        }

        Instant timeWindowStart = Instant.now();
        if (start.isPresent()) {
            timeWindowStart = DateTimeUtil.parseUTCDatetime(start.get());
        }
        Instant timeWindowEnd = timeWindowStart.plus(365, DAYS);
        if (end.isPresent()) {
            timeWindowEnd = DateTimeUtil.parseUTCDatetime(end.get());
        }

        List<Event> events = Arrays.asList(new Event[]{});
        if (creatorId.isPresent()) {
            events = eventDataService.getMyEventsWithinTime(creatorId.get(), timeWindowStart, timeWindowEnd, queryLimit);
        }
        return events;
    }

    @RequestMapping(path = "/events/{eventId}", method = RequestMethod.GET)
    public Event getEvent(@PathVariable("eventId")
                          @Pattern(regexp="[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}") final String eventId) {
        return eventDataService.getEvent(eventId);
    }

}

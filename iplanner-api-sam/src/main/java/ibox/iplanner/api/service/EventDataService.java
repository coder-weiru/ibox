package ibox.iplanner.api.service;

import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import ibox.iplanner.api.model.Event;
import ibox.iplanner.api.model.User;
import ibox.iplanner.api.util.DateTimeUtil;
import ibox.iplanner.api.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class EventDataService {

    private DynamoDB dynamoDb;
    private static final String EVENTS_TABLE_NAME = "iplanner_events";

    @Autowired
    public EventDataService(final DynamoDB dynamoDB) {
        this.dynamoDb = dynamoDB;
    }

    public void addEvent(final Event event) {
        Table eventsTable = this.dynamoDb.getTable(EVENTS_TABLE_NAME);

        eventsTable.putItem(new PutItemSpec().withItem(convertToItem(event)));
    }

    public void addEvents(List<Event> events) {
        events.stream().forEach(e-> addEvent(e));
    }

    public Event getEvent(String eventId) {
        Table eventsTable = this.dynamoDb.getTable(EVENTS_TABLE_NAME);
        Item item = eventsTable.getItem(new GetItemSpec()
                .withPrimaryKey("Id", eventId)
                .withProjectionExpression("Id, Summary, Description, Creator, CreatedBy, CreatedTime, UpdatedTime, StartTime, EndTime, Activity, EventStatus, EventLocation, EndTimeUnspecified, Recurrence")
                .withConsistentRead(true));

        return convertToEvent(item);
    }

    public List<Event> getMyEventsWithinTime(String creatorId, Instant timeWindowStart, Instant timeWindowEnd, Integer limit) {
        Table eventsTable = this.dynamoDb.getTable(EVENTS_TABLE_NAME);
        Index index = eventsTable.getIndex("CreatorEventsByStart-GSI");

        String timeWindowStartStr = DateTimeUtil.formatUTCDatetime(timeWindowStart);
        String timeWindowEndStr = DateTimeUtil.formatUTCDatetime(timeWindowEnd);

        QuerySpec spec = new QuerySpec()
                .withMaxResultSize(limit)
                .withKeyConditionExpression("CreatedBy = :v_creator_id AND StartTime BETWEEN :v_time_window_start AND :v_time_window_end")
                .withValueMap(new ValueMap()
                        .withString(":v_creator_id", creatorId)
                        .withString(":v_time_window_start", timeWindowStartStr)
                        .withString(":v_time_window_end", timeWindowEndStr));

        ItemCollection<QueryOutcome> items = index.query(spec);
        List<Event> events = new ArrayList<>();
        items.forEach(e-> {
            events.add(convertToEvent(e));
        });

        return events;
    }

    private Item convertToItem(Event event) {

        Optional<Instant> created = Optional.of(event.getCreated());
        Optional<Instant> updated = Optional.of(event.getUpdated());
        Optional<Instant> start = Optional.of(event.getStart());
        Optional<Instant> end = Optional.of(event.getEnd());

        Item item = new Item()
                .withString("Id", event.getId())
                .withString("Summary", event.getSummary())
                .withString("Description", event.getDescription())
                .withString("CreatedBy", event.getCreator().getId())
                .withJSON("Creator", JsonUtil.toJsonString(event.getCreator()))
                .withString("Activity", event.getActivity())
                .withString("EventStatus", event.getStatus())
                .withString("EventLocation", event.getLocation())
                .withBoolean("EndTimeUnspecified", event.getEndTimeUnspecified());

        if (created.isPresent()) {
            item.withString("CreatedTime", created.get().toString());
        }
        if (updated.isPresent()) {
            item.withString("UpdatedTime", updated.get().toString());
        }
        if (start.isPresent()) {
            item.withString("StartTime", start.get().toString());
        }
        if (end.isPresent()) {
            item.withString("EndTime", end.get().toString());
        }
        if (event.getRecurrence()!=null && !event.getRecurrence().isEmpty()) {
            item.withStringSet("Recurrence", event.getRecurrence());
        }
        return item;
    }

    private Event convertToEvent(Item item) {
        Event event = new Event();
        event.setId(item.getString("Id"));
        event.setSummary(item.getString("Summary"));
        event.setDescription(item.getString("Description"));
        event.setCreator(JsonUtil.fromJsonString(item.getJSON("Creator"), User.class));
        if (Optional.ofNullable(item.getString("CreatedTime")).isPresent()) {
            event.setCreated(Instant.parse(item.getString("CreatedTime")));
        }
        if (Optional.ofNullable(item.getString("UpdatedTime")).isPresent()) {
            event.setUpdated(Instant.parse(item.getString("UpdatedTime")));
        }
        if (Optional.ofNullable(item.getString("StartTime")).isPresent()) {
            event.setStart(Instant.parse(item.getString("StartTime")));
        }
        if (Optional.ofNullable(item.getString("EndTime")).isPresent()) {
            event.setEnd(Instant.parse(item.getString("EndTime")));
        }
        event.setActivity(item.getString("Activity"));
        event.setStatus(item.getString("EventStatus"));
        event.setLocation(item.getString("EventLocation"));
        event.setEndTimeUnspecified(item.getBoolean("EndTimeUnspecified"));
        event.setRecurrence(item.getStringSet("Recurrence"));

        return event;
    }
}

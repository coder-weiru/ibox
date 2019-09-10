package ibox.iplanner.api.service;

import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.util.StringUtils;
import ibox.iplanner.api.model.Event;
import ibox.iplanner.api.model.User;
import ibox.iplanner.api.util.DateTimeUtil;
import ibox.iplanner.api.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ibox.iplanner.api.service.dbmodel.EventDefinition.*;

@Slf4j
public class EventDataService {

    private final DynamoDB dynamoDb;

    public EventDataService(final DynamoDB dynamoDB) {
        this.dynamoDb = dynamoDB;
    }

    public void addEvent(final Event event) {
        Table eventsTable = this.dynamoDb.getTable(TABLE_NAME_EVENTS);

        eventsTable.putItem(new PutItemSpec().withItem(convertToItem(event)));
    }

    public void addEvents(List<Event> events) {
        events.stream().forEach(e-> addEvent(e));
    }

    public Event getEvent(String eventId) {
        Table eventsTable = this.dynamoDb.getTable(TABLE_NAME_EVENTS);
        String projectionExpression = String.format("%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s",
                FIELD_NAME_ID,
                FIELD_NAME_SUMMARY,
                FIELD_NAME_DESCRIPTION,
                FIELD_NAME_CREATOR,
                FIELD_NAME_CREATED_BY,
                FIELD_NAME_CREATED_TIME,
                FIELD_NAME_UPDATED_TIME,
                FIELD_NAME_START_TIME,
                FIELD_NAME_END_TIME,FIELD_NAME_ACTIVITY,
                FIELD_NAME_EVENT_STATUS,
                FIELD_NAME_EVENT_LOCATION,
                FIELD_NAME_EVENT_END_TIME_UNSPECIFIED,
                FIELD_NAME_EVENT_RECURRENCE);
        Item item = eventsTable.getItem(new GetItemSpec()
                .withPrimaryKey(FIELD_NAME_ID, eventId)
                .withProjectionExpression(projectionExpression)
                .withConsistentRead(true));

        return convertToEvent(item);
    }

    public List<Event> getMyEventsWithinTime(String creatorId, Instant timeWindowStart, Instant timeWindowEnd, String status, Integer limit) {
        Table eventsTable = this.dynamoDb.getTable(TABLE_NAME_EVENTS);
        Index index = eventsTable.getIndex(GSI_CREATOR_EVENTS_SORT_BY_START_TIME);

        String timeWindowStartStr = DateTimeUtil.formatUTCDatetime(timeWindowStart);
        String timeWindowEndStr = DateTimeUtil.formatUTCDatetime(timeWindowEnd);

        String keyConditionExpression = String.format("%s = :v_creator_id AND %s BETWEEN :v_time_window_start AND :v_time_window_end ",
                FIELD_NAME_CREATED_BY,
                FIELD_NAME_START_TIME);

        QuerySpec spec = new QuerySpec()
                .withMaxResultSize(limit)
                .withKeyConditionExpression(keyConditionExpression)
                .withValueMap(new ValueMap()
                        .withString(":v_creator_id", creatorId)
                        .withString(":v_time_window_start", timeWindowStartStr)
                        .withString(":v_time_window_end", timeWindowEndStr));

        ItemCollection<QueryOutcome> items = index.query(spec);
        List<Event> events = new ArrayList<>();
        items.forEach(e-> {
            Event event = convertToEvent(e);
            if ((StringUtils.isNullOrEmpty(status) && StringUtils.isNullOrEmpty(event.getStatus())) ||
                (!StringUtils.isNullOrEmpty(status) && status.equals(event.getStatus()))) {
                events.add(event);
            }
        });

        return events;
    }

    private Item convertToItem(Event event) {

        Optional<Instant> created = Optional.of(event.getCreated());
        Optional<Instant> updated = Optional.of(event.getUpdated());
        Optional<Instant> start = Optional.of(event.getStart());
        Optional<Instant> end = Optional.of(event.getEnd());

        Item item = new Item()
                .withString(FIELD_NAME_ID, event.getId())
                .withString(FIELD_NAME_SUMMARY, event.getSummary())
                .withString(FIELD_NAME_DESCRIPTION, event.getDescription())
                .withString(FIELD_NAME_CREATED_BY, event.getCreator().getId())
                .withJSON(FIELD_NAME_CREATOR, JsonUtil.toJsonString(event.getCreator()))
                .withString(FIELD_NAME_ACTIVITY, event.getActivity())
                .withString(FIELD_NAME_EVENT_STATUS, event.getStatus())
                .withString(FIELD_NAME_EVENT_LOCATION, event.getLocation())
                .withBoolean(FIELD_NAME_EVENT_END_TIME_UNSPECIFIED, event.getEndTimeUnspecified());

        if (created.isPresent()) {
            item.withString(FIELD_NAME_CREATED_TIME, created.get().toString());
        }
        if (updated.isPresent()) {
            item.withString(FIELD_NAME_UPDATED_TIME, updated.get().toString());
        }
        if (start.isPresent()) {
            item.withString(FIELD_NAME_START_TIME, start.get().toString());
        }
        if (end.isPresent()) {
            item.withString(FIELD_NAME_END_TIME, end.get().toString());
        }
        if (event.getRecurrence()!=null && !event.getRecurrence().isEmpty()) {
            item.withStringSet(FIELD_NAME_EVENT_RECURRENCE, event.getRecurrence());
        }
        return item;
    }

    private Event convertToEvent(Item item) {
        Event event = new Event();
        event.setId(item.getString(FIELD_NAME_ID));
        event.setSummary(item.getString(FIELD_NAME_SUMMARY));
        event.setDescription(item.getString(FIELD_NAME_DESCRIPTION));
        event.setCreator(JsonUtil.fromJsonString(item.getJSON(FIELD_NAME_CREATOR), User.class));
        if (Optional.ofNullable(item.getString(FIELD_NAME_CREATED_TIME)).isPresent()) {
            event.setCreated(Instant.parse(item.getString(FIELD_NAME_CREATED_TIME)));
        }
        if (Optional.ofNullable(item.getString(FIELD_NAME_UPDATED_TIME)).isPresent()) {
            event.setUpdated(Instant.parse(item.getString(FIELD_NAME_UPDATED_TIME)));
        }
        if (Optional.ofNullable(item.getString(FIELD_NAME_START_TIME)).isPresent()) {
            event.setStart(Instant.parse(item.getString(FIELD_NAME_START_TIME)));
        }
        if (Optional.ofNullable(item.getString(FIELD_NAME_END_TIME)).isPresent()) {
            event.setEnd(Instant.parse(item.getString(FIELD_NAME_END_TIME)));
        }
        event.setActivity(item.getString(FIELD_NAME_ACTIVITY));
        event.setStatus(item.getString(FIELD_NAME_EVENT_STATUS));
        event.setLocation(item.getString(FIELD_NAME_EVENT_LOCATION));
        event.setEndTimeUnspecified(item.getBoolean(FIELD_NAME_EVENT_END_TIME_UNSPECIFIED));
        event.setRecurrence(item.getStringSet(FIELD_NAME_EVENT_RECURRENCE));

        return event;
    }
}

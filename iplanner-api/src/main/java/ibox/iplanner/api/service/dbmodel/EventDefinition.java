package ibox.iplanner.api.service.dbmodel;

public final class EventDefinition {

    private EventDefinition() {
        // restrict instantiation
    }

    public static final String TABLE_NAME_EVENTS = "iplanner-events";
    public static final String FIELD_NAME_ID = "Id";
    public static final String FIELD_NAME_SUMMARY = "Summary";
    public static final String FIELD_NAME_DESCRIPTION = "Description";
    public static final String FIELD_NAME_CREATOR = "Creator";
    public static final String FIELD_NAME_CREATED_BY = "CreatedBy";
    public static final String FIELD_NAME_CREATED_TIME = "CreatedTime";
    public static final String FIELD_NAME_UPDATED_TIME = "UpdatedTime";
    public static final String FIELD_NAME_START_TIME = "StartTime";
    public static final String FIELD_NAME_END_TIME = "EndTime";
    public static final String FIELD_NAME_ACTIVITY = "Activity";
    public static final String FIELD_NAME_EVENT_STATUS = "EventStatus";
    public static final String FIELD_NAME_EVENT_LOCATION = "EventLocation";
    public static final String FIELD_NAME_EVENT_END_TIME_UNSPECIFIED = "EndTimeUnspecified";
    public static final String FIELD_NAME_EVENT_RECURRENCE = "Recurrence";

    public static final String GSI_CREATOR_EVENTS_SORT_BY_START_TIME = "CreatorEventsByStart-GSI";

}
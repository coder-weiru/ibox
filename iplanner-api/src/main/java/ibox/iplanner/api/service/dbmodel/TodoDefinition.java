package ibox.iplanner.api.service.dbmodel;

public final class TodoDefinition {

    private TodoDefinition() {
        // restrict instantiation
    }

    public static final String TABLE_NAME_TODOS = "iplanner-todos";
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
    public static final String FIELD_NAME_TODO_STATUS = "TodoStatus";
    public static final String FIELD_NAME_TODO_LOCATION = "TodoLocation";
    public static final String FIELD_NAME_TODO_END_TIME_UNSPECIFIED = "EndTimeUnspecified";
    public static final String FIELD_NAME_TODO_RECURRENCE = "Recurrence";

    public static final String GSI_CREATOR_TODOS_SORT_BY_START_TIME = "CreatorTodosByStart-GSI";

}
package ibox.iplanner.api.service.dbmodel;

public final class TodoDefinition {

    private TodoDefinition() {
        // restrict instantiation
    }

    public static final String TABLE_NAME_TODO_LIST = "iplanner-todolist";
    public static final String FIELD_NAME_ID = "Id";
    public static final String FIELD_NAME_SUMMARY = "Summary";
    public static final String FIELD_NAME_DESCRIPTION = "Description";
    public static final String FIELD_NAME_CREATOR = "Creator";
    public static final String FIELD_NAME_CREATED_TIME = "CreatedTime";
    public static final String FIELD_NAME_UPDATED_TIME = "UpdatedTime";
    public static final String FIELD_NAME_CREATED_BY = "CreatedBy";
    public static final String FIELD_NAME_ACTIVITY_ID = "ActivityId";
    public static final String FIELD_NAME_ACTIVITY_TYPE = "ActivityType";
    public static final String FIELD_NAME_TODO_STATUS = "TodoStatus";
    public static final String FIELD_NAME_ATTRIBUTES = "Attributes";
    public static final String GSI_CREATOR_TODO_LIST = "CreatorTodoList-GSI";
}
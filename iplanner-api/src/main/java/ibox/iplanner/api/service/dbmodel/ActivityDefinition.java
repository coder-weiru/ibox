package ibox.iplanner.api.service.dbmodel;

public final class ActivityDefinition {

    private ActivityDefinition() {
        // restrict instantiation
    }

    public static final String TABLE_NAME_ACTIVITIES = "iplanner-activities";
    public static final String FIELD_NAME_ID = "Id";
    public static final String FIELD_NAME_TITLE = "Title";
    public static final String FIELD_NAME_DESCRIPTION = "Description";
    public static final String FIELD_NAME_CREATOR = "Creator";
    public static final String FIELD_NAME_CREATED_BY = "CreatedBy";
    public static final String FIELD_NAME_CREATED_TIME = "CreatedTime";
    public static final String FIELD_NAME_UPDATED_TIME = "UpdatedTime";
    public static final String FIELD_NAME_ACTIVITY_TYPE = "ActivityType";
    public static final String FIELD_NAME_ACTIVITY_STATUS = "ActivityStatus";

    public static final String GSI_CREATOR_ACTIVITIES = "CreatorActivities-GSI";

}
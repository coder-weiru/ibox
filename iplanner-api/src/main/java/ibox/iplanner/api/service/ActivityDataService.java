package ibox.iplanner.api.service;

import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import ibox.iplanner.api.model.Activity;
import ibox.iplanner.api.model.ActivityStatus;
import ibox.iplanner.api.model.User;
import ibox.iplanner.api.model.updatable.Updatable;
import ibox.iplanner.api.service.util.DynamoDBUtil;
import ibox.iplanner.api.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ibox.iplanner.api.service.dbmodel.ActivityDefinition.*;

@Slf4j
public class ActivityDataService {

    private final DynamoDB dynamoDb;

    public ActivityDataService(final DynamoDB dynamoDB) {
        this.dynamoDb = dynamoDB;
    }

    public void addActivity(final Activity activity) {
        Table activitiesTable = this.dynamoDb.getTable(TABLE_NAME_ACTIVITIES);

        activitiesTable.putItem(new PutItemSpec().withItem(convertToItem(activity)));
    }

    public void addActivities(List<Activity> activities) {
        activities.stream().forEach(e-> addActivity(e));
    }

    public Activity getActivity(String activityId) {
        Table activitiesTable = this.dynamoDb.getTable(TABLE_NAME_ACTIVITIES);
        String projectionExpression = String.format("%s, %s, %s, %s, %s, %s, %s, %s, %s",
                FIELD_NAME_ID,
                FIELD_NAME_TITLE,
                FIELD_NAME_DESCRIPTION,
                FIELD_NAME_CREATOR,
                FIELD_NAME_CREATED_BY,
                FIELD_NAME_CREATED_TIME,
                FIELD_NAME_UPDATED_TIME,
                FIELD_NAME_ACTIVITY_TYPE,
                FIELD_NAME_ACTIVITY_STATUS);
        Item item = activitiesTable.getItem(new GetItemSpec()
                .withPrimaryKey(FIELD_NAME_ID, activityId)
                .withProjectionExpression(projectionExpression)
                .withConsistentRead(true));

        return convertToActivity(item);
    }

    public List<Activity> getMyActivities(String creatorId, String status, Integer limit) {
        Table activitiesTable = this.dynamoDb.getTable(TABLE_NAME_ACTIVITIES);
        Index index = activitiesTable.getIndex(GSI_CREATOR_ACTIVITIES);

        String keyConditionExpression = String.format("%s = :v_creator_id AND %s = :v_status ",
                FIELD_NAME_CREATED_BY,
                FIELD_NAME_ACTIVITY_STATUS);

        QuerySpec spec = new QuerySpec()
                .withMaxResultSize(limit)
                .withKeyConditionExpression(keyConditionExpression)
                .withValueMap(new ValueMap()
                        .withString(":v_creator_id", creatorId)
                        .withString(":v_status", status));

        ItemCollection<QueryOutcome> items = index.query(spec);
        List<Activity> activities = new ArrayList<>();
        items.forEach(e-> {
            activities.add(convertToActivity(e));
        });

        return activities;
    }

    public Activity updateActivity(final Updatable updatable) {
        Table activitiesTable = this.dynamoDb.getTable(TABLE_NAME_ACTIVITIES);

        UpdateItemOutcome outcome = activitiesTable.updateItem(new UpdateItemSpec()
                .withPrimaryKey(DynamoDBUtil.buildPrimaryKey(updatable.getPrimaryKey()))
                .withAttributeUpdate(DynamoDBUtil.buildAttributeUpdateList(updatable.getUpdatableAttributes()))
                .withReturnValues(ReturnValue.UPDATED_NEW));

        return convertToActivity(outcome.getItem());
    }

    public Activity deleteActivity(String activityId) {
        Table activitiesTable = this.dynamoDb.getTable(TABLE_NAME_ACTIVITIES);

        UpdateItemOutcome outcome = activitiesTable.updateItem(new UpdateItemSpec()
                .withPrimaryKey(FIELD_NAME_ID, activityId)
                .withAttributeUpdate(new AttributeUpdate(FIELD_NAME_ACTIVITY_STATUS).put(ActivityStatus.INACTIVE.name()))
                .withReturnValues(ReturnValue.UPDATED_NEW));

        return convertToActivity(outcome.getItem());
    }

    private Item convertToItem(Activity activity) {

        Optional<Instant> created = Optional.of(activity.getCreated());
        Optional<Instant> updated = Optional.of(activity.getUpdated());

        Item item = new Item()
                .withString(FIELD_NAME_ID, activity.getId())
                .withString(FIELD_NAME_TITLE, activity.getTitle())
                .withString(FIELD_NAME_DESCRIPTION, activity.getDescription())
                .withString(FIELD_NAME_CREATED_BY, activity.getCreator().getId())
                .withJSON(FIELD_NAME_CREATOR, JsonUtil.toJsonString(activity.getCreator()))
                .withString(FIELD_NAME_ACTIVITY_TYPE, activity.getType())
                .withString(FIELD_NAME_ACTIVITY_STATUS, activity.getStatus());

        if (created.isPresent()) {
            item.withString(FIELD_NAME_CREATED_TIME, created.get().toString());
        }
        if (updated.isPresent()) {
            item.withString(FIELD_NAME_UPDATED_TIME, updated.get().toString());
        }
        return item;
    }

    private Activity convertToActivity(Item item) {
        Activity activity = new Activity();
        activity.setId(item.getString(FIELD_NAME_ID));
        activity.setTitle(item.getString(FIELD_NAME_TITLE));
        activity.setDescription(item.getString(FIELD_NAME_DESCRIPTION));
        activity.setCreator(JsonUtil.fromJsonString(item.getJSON(FIELD_NAME_CREATOR), User.class));
        if (Optional.ofNullable(item.getString(FIELD_NAME_CREATED_TIME)).isPresent()) {
            activity.setCreated(Instant.parse(item.getString(FIELD_NAME_CREATED_TIME)));
        }
        if (Optional.ofNullable(item.getString(FIELD_NAME_UPDATED_TIME)).isPresent()) {
            activity.setUpdated(Instant.parse(item.getString(FIELD_NAME_UPDATED_TIME)));
        }
        activity.setType(item.getString(FIELD_NAME_ACTIVITY_TYPE));
        activity.setStatus(item.getString(FIELD_NAME_ACTIVITY_STATUS));

        return activity;
    }
}

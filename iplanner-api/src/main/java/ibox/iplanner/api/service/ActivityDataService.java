package ibox.iplanner.api.service;

import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import ibox.iplanner.api.lambda.exception.RecordNotFoundException;
import ibox.iplanner.api.model.*;
import ibox.iplanner.api.service.util.DynamoDBUtil;
import ibox.iplanner.api.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.*;

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

    public void addActivities(final List<Activity> activities) {
        activities.stream().forEach(e-> addActivity(e));
    }

    public Activity getActivity(final String activityId) {
        Table activitiesTable = this.dynamoDb.getTable(TABLE_NAME_ACTIVITIES);
        StringBuilder projectionExpressionBuilder = new StringBuilder(String.format("%s, %s, %s, %s, %s, %s, %s, %s, %s",
                FIELD_NAME_ID,
                FIELD_NAME_TITLE,
                FIELD_NAME_DESCRIPTION,
                FIELD_NAME_CREATOR,
                FIELD_NAME_CREATED_BY,
                FIELD_NAME_CREATED_TIME,
                FIELD_NAME_UPDATED_TIME,
                FIELD_NAME_ACTIVITY_TYPE,
                FIELD_NAME_ACTIVITY_STATUS));

        Set<TodoFeature> supported = Activities.getAllFeatures();
        Iterator<TodoFeature> iter = supported.iterator();
        while (iter.hasNext()) {
            projectionExpressionBuilder.append(", ");
            projectionExpressionBuilder.append(iter.next().getValue());
        }
        Item item = activitiesTable.getItem(new GetItemSpec()
                .withPrimaryKey(FIELD_NAME_ID, activityId)
                .withProjectionExpression(projectionExpressionBuilder.toString())
                .withConsistentRead(true));

        return convertToActivity(item);
    }

    public List<Activity> getMyActivities(final String creatorId, final String status, final Integer limit) {
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

    public Activity updateActivity(final Activity updatable) {
        Activity dbActivity = this.getActivity(updatable.getId());
        if (dbActivity==null) {
            throw new RecordNotFoundException("The activity to be updated is not found");
        }
        StringBuilder addStatementBuilder = new StringBuilder("");
        StringBuilder setStatementBuilder = new StringBuilder("");
        ValueMap valueMap = new ValueMap();
        Optional<String> title = Optional.ofNullable(dbActivity.getTitle());
        if (title.isPresent()) {
            if (!title.get().equals(updatable.getTitle())) {
                if (setStatementBuilder.length()>0) {
                    setStatementBuilder.append(", ");
                }
                setStatementBuilder.append(String.format("%s = :val%s", FIELD_NAME_TITLE, FIELD_NAME_TITLE));
                valueMap.withString(String.format(":val%s", FIELD_NAME_TITLE), updatable.getTitle());
            }
        } else {
            if (addStatementBuilder.length()>0) {
                addStatementBuilder.append(", ");
            }
            addStatementBuilder.append(String.format("%s = :val%s", FIELD_NAME_TITLE, FIELD_NAME_TITLE));
            valueMap.withString(String.format(":val%s", FIELD_NAME_TITLE), updatable.getTitle());
        }
        Optional<String> description = Optional.ofNullable(dbActivity.getDescription());
        if (description.isPresent()) {
            String n = updatable.getDescription();
            String o = description.get();
            if (!o.equals(n)) {
                if (setStatementBuilder.length()>0) {
                    setStatementBuilder.append(", ");
                }
                setStatementBuilder.append(String.format("%s = :val%s", FIELD_NAME_DESCRIPTION, FIELD_NAME_DESCRIPTION));
                valueMap.withString(String.format(":val%s", FIELD_NAME_DESCRIPTION), updatable.getDescription());
            }
        } else {
            if (addStatementBuilder.length()>0) {
                addStatementBuilder.append(", ");
            }
            addStatementBuilder.append(String.format("%s = :val%s", FIELD_NAME_DESCRIPTION, FIELD_NAME_DESCRIPTION));
            valueMap.withString(String.format(":val%s", FIELD_NAME_DESCRIPTION), updatable.getDescription());
        }
        Optional<Instant> updated = Optional.ofNullable(dbActivity.getUpdated());
        if (updated.isPresent()) {
            if (setStatementBuilder.length()>0) {
                setStatementBuilder.append(", ");
            }
            setStatementBuilder.append(String.format("%s = :val%s", FIELD_NAME_UPDATED_TIME, FIELD_NAME_UPDATED_TIME));
            valueMap.withString(String.format(":val%s", FIELD_NAME_UPDATED_TIME), JsonUtil.toJsonString(Instant.now()));
        } else {
            if (addStatementBuilder.length()>0) {
                addStatementBuilder.append(", ");
            }
            addStatementBuilder.append(String.format("%s = :val%s", FIELD_NAME_UPDATED_TIME, FIELD_NAME_UPDATED_TIME));
            valueMap.withString(String.format(":val%s", FIELD_NAME_UPDATED_TIME), JsonUtil.toJsonString(Instant.now()));
        }
        Optional<ActivityStatus> status = Optional.ofNullable(dbActivity.getStatus());
        if (status.isPresent()) {
            if (!status.get().equals(updatable.getStatus())) {
                if (setStatementBuilder.length()>0) {
                    setStatementBuilder.append(", ");
                }
                setStatementBuilder.append(String.format("%s = :val%s", FIELD_NAME_ACTIVITY_STATUS, FIELD_NAME_ACTIVITY_STATUS));
                valueMap.withString(String.format(":val%s", FIELD_NAME_ACTIVITY_STATUS), updatable.getStatus().name());
            }
        } else {
            if (addStatementBuilder.length()>0) {
                addStatementBuilder.append(", ");
            }
            addStatementBuilder.append(String.format("%s = :val%s", FIELD_NAME_ACTIVITY_STATUS, FIELD_NAME_ACTIVITY_STATUS));
            valueMap.withString(String.format(":val%s", FIELD_NAME_ACTIVITY_STATUS), updatable.getStatus().name());
        }
        Set<TodoFeature> supported = Activities.getAllFeatures();
        Iterator<TodoFeature> iter = supported.iterator();
        while (iter.hasNext()) {
            TodoFeature feature = iter.next();
            TodoAttribute attribute = updatable.getAttribute(feature);
            if (attribute!=null) {
                if (setStatementBuilder.length()>0) {
                    setStatementBuilder.append(",");
                }
                setStatementBuilder.append(String.format("%s = :val%s", feature.getValue(), feature.getValue()));
                valueMap.withJSON(String.format(":val%s", feature.getValue()), JsonUtil.toJsonString(attribute));
            }
        }
        Table activitiesTable = this.dynamoDb.getTable(TABLE_NAME_ACTIVITIES);
        StringBuilder updateExpressionBuilder = new StringBuilder("");
        if (!addStatementBuilder.toString().isEmpty()) {
            updateExpressionBuilder.append(String.format("add %s ", addStatementBuilder.toString()));
        }
        if (!setStatementBuilder.toString().isEmpty()) {
            updateExpressionBuilder.append(String.format("set %s ", setStatementBuilder.toString()));
        }
        UpdateItemOutcome outcome = activitiesTable.updateItem(new UpdateItemSpec()
                .withPrimaryKey(DynamoDBUtil.primaryKeyBuilder().addComponent(FIELD_NAME_ID, dbActivity.getId()).build())
                .withUpdateExpression(updateExpressionBuilder.toString())
                .withValueMap(valueMap)
                .withReturnValues(ReturnValue.ALL_NEW));

        return convertToActivity(outcome.getItem());
    }

    public Activity deleteActivity(final String activityId) {
        Table activitiesTable = this.dynamoDb.getTable(TABLE_NAME_ACTIVITIES);

        UpdateItemOutcome outcome = activitiesTable.updateItem(new UpdateItemSpec()
                .withPrimaryKey(FIELD_NAME_ID, activityId)
                .withAttributeUpdate(new AttributeUpdate(FIELD_NAME_ACTIVITY_STATUS).put(ActivityStatus.INACTIVE.name()))
                .withReturnValues(ReturnValue.ALL_NEW));

        return convertToActivity(outcome.getItem());
    }

    private Item convertToItem(final Activity activity) {

        Optional<Instant> created = Optional.of(activity.getCreated());
        Optional<Instant> updated = Optional.of(activity.getUpdated());

        Item item = new Item()
                .withString(FIELD_NAME_ID, activity.getId())
                .withString(FIELD_NAME_TITLE, activity.getTitle())
                .withString(FIELD_NAME_DESCRIPTION, activity.getDescription())
                .withString(FIELD_NAME_CREATED_BY, activity.getCreator().getId())
                .withJSON(FIELD_NAME_CREATOR, JsonUtil.toJsonString(activity.getCreator()))
                .withString(FIELD_NAME_ACTIVITY_TYPE, activity.getActivityType())
                .withString(FIELD_NAME_ACTIVITY_STATUS, activity.getStatus().name());

        Set<TodoFeature> supported = activity.getSupportedFeatures();
        supported.stream().forEach( feature -> {
           item.withJSON(feature.getValue(), JsonUtil.toJsonString(activity.getAttribute(feature)));
        });

        if (created.isPresent()) {
            item.withString(FIELD_NAME_CREATED_TIME, JsonUtil.toJsonString(created.get()));
        }
        if (updated.isPresent()) {
            item.withString(FIELD_NAME_UPDATED_TIME, JsonUtil.toJsonString(updated.get()));
        }
        return item;
    }

    private Activity convertToActivity(final Item item) {
        if (item==null) {
            return null;
        }
        String activityType = item.getString(FIELD_NAME_ACTIVITY_TYPE);
        Activity activity = Activities.newInstanceOf(activityType);
        activity.setId(item.getString(FIELD_NAME_ID));
        activity.setTitle(item.getString(FIELD_NAME_TITLE));
        activity.setDescription(item.getString(FIELD_NAME_DESCRIPTION));
        activity.setCreator(JsonUtil.fromJsonString(item.getJSON(FIELD_NAME_CREATOR), User.class));
        if (Optional.ofNullable(item.getString(FIELD_NAME_CREATED_TIME)).isPresent()) {
            activity.setCreated(JsonUtil.fromJsonString(item.getString(FIELD_NAME_CREATED_TIME), Instant.class));
        }
        if (Optional.ofNullable(item.getString(FIELD_NAME_UPDATED_TIME)).isPresent()) {
            activity.setUpdated(JsonUtil.fromJsonString(item.getString(FIELD_NAME_UPDATED_TIME), Instant.class));
        }
        activity.setActivityType(item.getString(FIELD_NAME_ACTIVITY_TYPE));
        activity.setStatus(ActivityStatus.of(item.getString(FIELD_NAME_ACTIVITY_STATUS)));

        Set<TodoFeature> supported = activity.getSupportedFeatures();
        supported.stream().forEach( feature -> {
            activity.setAttribute(JsonUtil.fromJsonString(item.getJSON(feature.getValue()), TodoAttribute.class));
        });
        return activity;
    }

}

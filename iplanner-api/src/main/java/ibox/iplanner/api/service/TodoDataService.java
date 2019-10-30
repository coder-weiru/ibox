package ibox.iplanner.api.service;

import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.util.StringUtils;
import ibox.iplanner.api.model.Todo;
import ibox.iplanner.api.model.TodoStatus;
import ibox.iplanner.api.model.User;
import ibox.iplanner.api.model.updatable.Updatable;
import ibox.iplanner.api.service.util.DynamoDBUtil;
import ibox.iplanner.api.util.DateTimeUtil;
import ibox.iplanner.api.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ibox.iplanner.api.service.dbmodel.TodoDefinition.*;

@Slf4j
public class TodoDataService {

    private final DynamoDB dynamoDb;

    public TodoDataService(final DynamoDB dynamoDB) {
        this.dynamoDb = dynamoDB;
    }

    public void addTodo(final Todo todo) {
        Table todosTable = this.dynamoDb.getTable(TABLE_NAME_TODOS);

        todosTable.putItem(new PutItemSpec().withItem(convertToItem(todo)));
    }

    public void addTodos(List<Todo> todos) {
        todos.stream().forEach(e-> addTodo(e));
    }

    public Todo getTodo(String todoId) {
        Table todosTable = this.dynamoDb.getTable(TABLE_NAME_TODOS);
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
                FIELD_NAME_TODO_STATUS,
                FIELD_NAME_TODO_LOCATION,
                FIELD_NAME_TODO_END_TIME_UNSPECIFIED,
                FIELD_NAME_TODO_RECURRENCE);
        Item item = todosTable.getItem(new GetItemSpec()
                .withPrimaryKey(FIELD_NAME_ID, todoId)
                .withProjectionExpression(projectionExpression)
                .withConsistentRead(true));

        return convertToTodo(item);
    }

    public List<Todo> getMyTodosWithinTime(String creatorId, Instant timeWindowStart, Instant timeWindowEnd, String status, Integer limit) {
        Table todosTable = this.dynamoDb.getTable(TABLE_NAME_TODOS);
        Index index = todosTable.getIndex(GSI_CREATOR_TODOS_SORT_BY_START_TIME);

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
        List<Todo> todos = new ArrayList<>();
        items.forEach(e-> {
            Todo todo = convertToTodo(e);
            if ((StringUtils.isNullOrEmpty(status) && StringUtils.isNullOrEmpty(todo.getStatus())) ||
                (!StringUtils.isNullOrEmpty(status) && status.equals(todo.getStatus()))) {
                todos.add(todo);
            }
        });

        return todos;
    }

    public Todo updateTodo(final Updatable updatable) {
        Table todosTable = this.dynamoDb.getTable(TABLE_NAME_TODOS);

        UpdateItemOutcome outcome = todosTable.updateItem(new UpdateItemSpec()
                .withPrimaryKey(DynamoDBUtil.buildPrimaryKey(updatable.getPrimaryKey()))
                .withAttributeUpdate(DynamoDBUtil.buildAttributeUpdateList(updatable.getUpdatableAttributes()))
                .withReturnValues(ReturnValue.UPDATED_NEW));

        return convertToTodo(outcome.getItem());
    }

    public Todo deleteTodo(String activityId) {
        Table todosTable = this.dynamoDb.getTable(TABLE_NAME_TODOS);

        UpdateItemOutcome outcome = todosTable.updateItem(new UpdateItemSpec()
                .withPrimaryKey(FIELD_NAME_ID, activityId)
                .withAttributeUpdate(new AttributeUpdate(FIELD_NAME_TODO_STATUS).put(TodoStatus.CLOSED.name()))
                .withReturnValues(ReturnValue.UPDATED_NEW));

        return convertToTodo(outcome.getItem());
    }

    private Item convertToItem(Todo todo) {

        Optional<Instant> created = Optional.of(todo.getCreated());
        Optional<Instant> updated = Optional.of(todo.getUpdated());
        Optional<Instant> start = Optional.of(todo.getStart());
        Optional<Instant> end = Optional.of(todo.getEnd());

        Item item = new Item()
                .withString(FIELD_NAME_ID, todo.getId())
                .withString(FIELD_NAME_SUMMARY, todo.getSummary())
                .withString(FIELD_NAME_DESCRIPTION, todo.getDescription())
                .withString(FIELD_NAME_CREATED_BY, todo.getCreator().getId())
                .withJSON(FIELD_NAME_CREATOR, JsonUtil.toJsonString(todo.getCreator()))
                .withString(FIELD_NAME_ACTIVITY, todo.getActivity())
                .withString(FIELD_NAME_TODO_STATUS, todo.getStatus())
                .withString(FIELD_NAME_TODO_LOCATION, todo.getLocation())
                .withBoolean(FIELD_NAME_TODO_END_TIME_UNSPECIFIED, todo.getEndTimeUnspecified());

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
        if (todo.getRecurrence()!=null && !todo.getRecurrence().isEmpty()) {
            item.withStringSet(FIELD_NAME_TODO_RECURRENCE, todo.getRecurrence());
        }
        return item;
    }

    private Todo convertToTodo(Item item) {
        Todo todo = new Todo();
        todo.setId(item.getString(FIELD_NAME_ID));
        todo.setSummary(item.getString(FIELD_NAME_SUMMARY));
        todo.setDescription(item.getString(FIELD_NAME_DESCRIPTION));
        todo.setCreator(JsonUtil.fromJsonString(item.getJSON(FIELD_NAME_CREATOR), User.class));
        if (Optional.ofNullable(item.getString(FIELD_NAME_CREATED_TIME)).isPresent()) {
            todo.setCreated(Instant.parse(item.getString(FIELD_NAME_CREATED_TIME)));
        }
        if (Optional.ofNullable(item.getString(FIELD_NAME_UPDATED_TIME)).isPresent()) {
            todo.setUpdated(Instant.parse(item.getString(FIELD_NAME_UPDATED_TIME)));
        }
        if (Optional.ofNullable(item.getString(FIELD_NAME_START_TIME)).isPresent()) {
            todo.setStart(Instant.parse(item.getString(FIELD_NAME_START_TIME)));
        }
        if (Optional.ofNullable(item.getString(FIELD_NAME_END_TIME)).isPresent()) {
            todo.setEnd(Instant.parse(item.getString(FIELD_NAME_END_TIME)));
        }
        todo.setActivity(item.getString(FIELD_NAME_ACTIVITY));
        todo.setStatus(item.getString(FIELD_NAME_TODO_STATUS));
        todo.setLocation(item.getString(FIELD_NAME_TODO_LOCATION));
        todo.setEndTimeUnspecified((Boolean) Optional.ofNullable(item.get(FIELD_NAME_TODO_END_TIME_UNSPECIFIED)).orElse(Boolean.valueOf(todo.getEnd()==null)));
        todo.setRecurrence(item.getStringSet(FIELD_NAME_TODO_RECURRENCE));

        return todo;
    }
}

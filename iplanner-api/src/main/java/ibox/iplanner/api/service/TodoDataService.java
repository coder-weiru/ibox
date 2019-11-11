package ibox.iplanner.api.service;

import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import ibox.iplanner.api.model.AttributeSet;
import ibox.iplanner.api.model.Todo;
import ibox.iplanner.api.model.TodoStatus;
import ibox.iplanner.api.model.User;
import ibox.iplanner.api.model.updatable.Updatable;
import ibox.iplanner.api.service.util.DynamoDBUtil;
import ibox.iplanner.api.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static ibox.iplanner.api.service.dbmodel.TodoDefinition.*;

@Slf4j
public class TodoDataService {

    private final DynamoDB dynamoDb;

    public TodoDataService(final DynamoDB dynamoDB) {
        this.dynamoDb = dynamoDB;
    }

    public void addTodo(final Todo todo) {
        Table todolistTable = this.dynamoDb.getTable(TABLE_NAME_TODO_LIST);

        todolistTable.putItem(new PutItemSpec().withItem(convertToItem(todo)));
    }

    public void addTodoList(List<Todo> todoList) {
        todoList.stream().forEach(e -> addTodo(e));
    }

    public Todo getTodo(String todoId) {
        Table todolistTable = this.dynamoDb.getTable(TABLE_NAME_TODO_LIST);
        String projectionExpression = String.format("%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s",
                FIELD_NAME_ID,
                FIELD_NAME_SUMMARY,
                FIELD_NAME_DESCRIPTION,
                FIELD_NAME_ACTIVITY_ID,
                FIELD_NAME_ACTIVITY_TYPE,
                FIELD_NAME_CREATOR,
                FIELD_NAME_CREATED_BY,
                FIELD_NAME_CREATED_TIME,
                FIELD_NAME_UPDATED_TIME,
                FIELD_NAME_TODO_STATUS,
                FIELD_NAME_ATTRIBUTES);
        Item item = todolistTable.getItem(new GetItemSpec()
                .withPrimaryKey(FIELD_NAME_ID, todoId)
                .withProjectionExpression(projectionExpression)
                .withConsistentRead(true));

        return convertToTodo(item);
    }

    public List<Todo> getMyTodoListByFilter(String creatorId, Set activities, String status, Integer limit) {
        Table todolistTable = this.dynamoDb.getTable(TABLE_NAME_TODO_LIST);
        Index index = todolistTable.getIndex(GSI_CREATOR_TODO_LIST);

        QuerySpec spec = new QuerySpec()
                .withMaxResultSize(limit)
                .withHashKey(new KeyAttribute(FIELD_NAME_CREATED_BY, creatorId))
                .withRangeKeyCondition(new RangeKeyCondition(FIELD_NAME_TODO_STATUS).eq(status))
                .withQueryFilters(new QueryFilter(FIELD_NAME_ACTIVITY_ID).in(activities.toArray()));

        ItemCollection<QueryOutcome> items = index.query(spec);
        List<Todo> todolist = new ArrayList<>();
        items.forEach(e -> todolist.add(convertToTodo(e)));

        return todolist;
    }

    public Todo updateTodo(final Updatable updatable) {
        Table todolistTable = this.dynamoDb.getTable(TABLE_NAME_TODO_LIST);

        UpdateItemOutcome outcome = todolistTable.updateItem(new UpdateItemSpec()
                .withPrimaryKey(DynamoDBUtil.buildPrimaryKey(updatable.getPrimaryKey()))
                .withAttributeUpdate(DynamoDBUtil.buildAttributeUpdateList(updatable.getUpdatableAttributes()))
                .withReturnValues(ReturnValue.UPDATED_NEW));

        return convertToTodo(outcome.getItem());
    }

    public Todo deleteTodo(String todoId) {
        Table todolistTable = this.dynamoDb.getTable(TABLE_NAME_TODO_LIST);

        UpdateItemOutcome outcome = todolistTable.updateItem(new UpdateItemSpec()
                .withPrimaryKey(FIELD_NAME_ID, todoId)
                .withAttributeUpdate(new AttributeUpdate(FIELD_NAME_TODO_STATUS).put(TodoStatus.CLOSED.name()))
                .withReturnValues(ReturnValue.UPDATED_NEW));

        return convertToTodo(outcome.getItem());
    }

    private Item convertToItem(Todo todo) {

        Optional<Instant> created = Optional.of(todo.getCreated());
        Optional<Instant> updated = Optional.of(todo.getUpdated());
        Optional<String> creatorId = Optional.ofNullable(todo.getCreator().getId());
        Item item = new Item()
                .withString(FIELD_NAME_ID, todo.getId())
                .withString(FIELD_NAME_SUMMARY, todo.getSummary())
                .withString(FIELD_NAME_DESCRIPTION, todo.getDescription())
                .withJSON(FIELD_NAME_CREATOR, JsonUtil.toJsonString(todo.getCreator()))
                .withString(FIELD_NAME_CREATED_BY, creatorId.orElse(""))
                .withString(FIELD_NAME_ACTIVITY_ID, todo.getActivityId())
                .withString(FIELD_NAME_ACTIVITY_TYPE, todo.getActivityType())
                .withJSON(FIELD_NAME_ATTRIBUTES, JsonUtil.toJsonString(todo.getAttributeSet()))
                .withString(FIELD_NAME_TODO_STATUS, todo.getStatus().name());

        if (created.isPresent()) {
            item.withString(FIELD_NAME_CREATED_TIME, created.get().toString());
        }
        if (updated.isPresent()) {
            item.withString(FIELD_NAME_UPDATED_TIME, updated.get().toString());
        }

        return item;
    }

    private Todo convertToTodo(Item item) {

        Todo todo = new Todo();
        todo.setId(item.getString(FIELD_NAME_ID));
        todo.setSummary(item.getString(FIELD_NAME_SUMMARY));
        todo.setDescription(item.getString(FIELD_NAME_DESCRIPTION));
        todo.setCreator(JsonUtil.fromJsonString(item.getJSON(FIELD_NAME_CREATOR), User.class));
        todo.setActivityId(item.getString(FIELD_NAME_ACTIVITY_ID));
        todo.setActivityType(item.getString(FIELD_NAME_ACTIVITY_TYPE));
        if (Optional.ofNullable(item.getString(FIELD_NAME_CREATED_TIME)).isPresent()) {
            todo.setCreated(Instant.parse(item.getString(FIELD_NAME_CREATED_TIME)));
        }
        if (Optional.ofNullable(item.getString(FIELD_NAME_UPDATED_TIME)).isPresent()) {
            todo.setUpdated(Instant.parse(item.getString(FIELD_NAME_UPDATED_TIME)));
        }
        todo.setStatus(TodoStatus.of(item.getString(FIELD_NAME_TODO_STATUS)));
        todo.setAttributeSet(JsonUtil.fromJsonString(item.getJSON(FIELD_NAME_ATTRIBUTES), AttributeSet.class));

        return todo;
    }
}

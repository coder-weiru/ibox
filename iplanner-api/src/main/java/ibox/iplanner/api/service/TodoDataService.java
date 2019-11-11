package ibox.iplanner.api.service;

import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import ibox.iplanner.api.lambda.exception.InvalidInputException;
import ibox.iplanner.api.model.*;
import ibox.iplanner.api.service.util.DynamoDBUtil;
import ibox.iplanner.api.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static ibox.iplanner.api.service.dbmodel.TodoDefinition.*;
import static ibox.iplanner.api.util.ApiErrorConstants.ERROR_BAD_REQUEST;
import static ibox.iplanner.api.util.ApiErrorConstants.SC_BAD_REQUEST;

@Slf4j
public class TodoDataService {

    private static final String TODO_NOT_FOUND_ERROR_MESSAGE = "The specified todo is not found";

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

    public List<Todo> getMyTodoListByFilter(String creatorId, String status, Integer limit) {
        Table todolistTable = this.dynamoDb.getTable(TABLE_NAME_TODO_LIST);
        Index index = todolistTable.getIndex(GSI_CREATOR_TODO_LIST);

        QuerySpec spec = new QuerySpec()
                .withMaxResultSize(limit)
                .withHashKey(new KeyAttribute(FIELD_NAME_CREATED_BY, creatorId))
                .withRangeKeyCondition(new RangeKeyCondition(FIELD_NAME_TODO_STATUS).eq(status));

        ItemCollection<QueryOutcome> items = index.query(spec);
        List<Todo> todolist = new ArrayList<>();
        items.forEach(e -> todolist.add(convertToTodo(e)));

        return todolist;
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

    public Todo updateTodo(final Todo updatable) {
        Todo dbTodo = this.getTodo(updatable.getId());
        if (dbTodo==null) {
            ApiError error = ApiError.builder()
                    .error(ERROR_BAD_REQUEST)
                    .message(TODO_NOT_FOUND_ERROR_MESSAGE)
                    .status(SC_BAD_REQUEST)
                    .build();
            throw new InvalidInputException(String.format(TODO_NOT_FOUND_ERROR_MESSAGE), error);
        }
        List<AttributeUpdate> attributeUpdates = new ArrayList<>();
        Optional<String> summary = Optional.ofNullable(dbTodo.getSummary());
        if (summary.isPresent()) {
            if (!summary.get().equals(updatable.getSummary())) {
                attributeUpdates.add(DynamoDBUtil.updateAttributeUpdate(FIELD_NAME_SUMMARY, updatable.getSummary()));
            }
            // Does not allow deleting summary
        } else {
            attributeUpdates.add(DynamoDBUtil.addAttributeUpdate(FIELD_NAME_SUMMARY, updatable.getSummary()));
        }
        Optional<String> description = Optional.ofNullable(dbTodo.getDescription());
        if (description.isPresent()) {
            String n = updatable.getDescription();
            String o = description.get();
            if (!o.equals(n)) {
                if (n==null) {
                    attributeUpdates.add(DynamoDBUtil.deleteAttributeUpdate(FIELD_NAME_DESCRIPTION, updatable.getDescription()));
                } else {
                    attributeUpdates.add(DynamoDBUtil.updateAttributeUpdate(FIELD_NAME_DESCRIPTION, updatable.getDescription()));
                }
            }
        } else {
            attributeUpdates.add(DynamoDBUtil.addAttributeUpdate(FIELD_NAME_DESCRIPTION, updatable.getDescription()));
        }
        Optional<Instant> updated = Optional.ofNullable(dbTodo.getUpdated());
        if (updated.isPresent()) {
            attributeUpdates.add(DynamoDBUtil.updateAttributeUpdate(FIELD_NAME_UPDATED_TIME, JsonUtil.toJsonString(Instant.now())));
        } else {
            attributeUpdates.add(DynamoDBUtil.addAttributeUpdate(FIELD_NAME_UPDATED_TIME, JsonUtil.toJsonString(Instant.now())));
        }
        Optional<TodoStatus> status = Optional.ofNullable(dbTodo.getStatus());
        if (status.isPresent()) {
            if (!status.get().equals(updatable.getStatus())) {
                attributeUpdates.add(DynamoDBUtil.updateAttributeUpdate(FIELD_NAME_TODO_STATUS, updatable.getStatus()));
            }
        } else {
            attributeUpdates.add(DynamoDBUtil.addAttributeUpdate(FIELD_NAME_TODO_STATUS, updatable.getStatus()));
        }
        Optional<AttributeSet> attributeSet = Optional.ofNullable(dbTodo.getAttributeSet());
        if (attributeSet.isPresent()) {
            if (!attributeSet.get().equals(updatable.getAttributeSet())) {
                attributeUpdates.add(DynamoDBUtil.updateAttributeUpdate(FIELD_NAME_ATTRIBUTES, JsonUtil.toJsonString(updatable.getAttributeSet())));
            }
        } else {
            attributeUpdates.add(DynamoDBUtil.addAttributeUpdate(FIELD_NAME_ATTRIBUTES, JsonUtil.toJsonString(updatable.getAttributeSet())));
        }

        Table todolistTable = this.dynamoDb.getTable(TABLE_NAME_TODO_LIST);

        UpdateItemOutcome outcome = todolistTable.updateItem(new UpdateItemSpec()
                .withPrimaryKey(DynamoDBUtil.primaryKeyBuilder().addComponent(FIELD_NAME_ID, dbTodo.getId()).build())
                .withAttributeUpdate(attributeUpdates)
                .withReturnValues(ReturnValue.ALL_NEW));

        return convertToTodo(outcome.getItem());
    }

    public Todo deleteTodo(String todoId) {
        Table todolistTable = this.dynamoDb.getTable(TABLE_NAME_TODO_LIST);

        UpdateItemOutcome outcome = todolistTable.updateItem(new UpdateItemSpec()
                .withPrimaryKey(FIELD_NAME_ID, todoId)
                .withAttributeUpdate(new AttributeUpdate(FIELD_NAME_TODO_STATUS).put(TodoStatus.CLOSED.name()))
                .withReturnValues(ReturnValue.ALL_NEW));

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
            item.withString(FIELD_NAME_CREATED_TIME, JsonUtil.toJsonString(created.get()));
        }
        if (updated.isPresent()) {
            item.withString(FIELD_NAME_UPDATED_TIME, JsonUtil.toJsonString(updated.get()));
        }

        return item;
    }

    private Todo convertToTodo(Item item) {
        if (item==null) {
            return null;
        }
        Todo todo = new Todo();
        todo.setId(item.getString(FIELD_NAME_ID));
        todo.setSummary(item.getString(FIELD_NAME_SUMMARY));
        todo.setDescription(item.getString(FIELD_NAME_DESCRIPTION));
        todo.setCreator(JsonUtil.fromJsonString(item.getJSON(FIELD_NAME_CREATOR), User.class));
        todo.setActivityId(item.getString(FIELD_NAME_ACTIVITY_ID));
        todo.setActivityType(item.getString(FIELD_NAME_ACTIVITY_TYPE));
        if (Optional.ofNullable(item.getString(FIELD_NAME_CREATED_TIME)).isPresent()) {
            todo.setCreated(JsonUtil.fromJsonString(item.getString(FIELD_NAME_CREATED_TIME), Instant.class));
        }
        if (Optional.ofNullable(item.getString(FIELD_NAME_UPDATED_TIME)).isPresent()) {
            todo.setUpdated(JsonUtil.fromJsonString(item.getString(FIELD_NAME_UPDATED_TIME), Instant.class));
        }
        todo.setStatus(TodoStatus.of(item.getString(FIELD_NAME_TODO_STATUS)));
        todo.setAttributeSet(JsonUtil.fromJsonString(item.getJSON(FIELD_NAME_ATTRIBUTES), AttributeSet.class));

        return todo;
    }
}

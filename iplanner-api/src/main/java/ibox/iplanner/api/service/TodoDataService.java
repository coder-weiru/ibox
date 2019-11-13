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
        StringBuilder projectionExpressionBuilder = new StringBuilder(String.format("%s, %s, %s, %s, %s, %s, %s, %s, %s, %s",
                FIELD_NAME_ID,
                FIELD_NAME_SUMMARY,
                FIELD_NAME_DESCRIPTION,
                FIELD_NAME_ACTIVITY_ID,
                FIELD_NAME_ACTIVITY_TYPE,
                FIELD_NAME_CREATOR,
                FIELD_NAME_CREATED_BY,
                FIELD_NAME_CREATED_TIME,
                FIELD_NAME_UPDATED_TIME,
                FIELD_NAME_TODO_STATUS));

        Set<TodoFeature> supported = Activities.getAllFeatures();
        Iterator<TodoFeature> iter = supported.iterator();
        while (iter.hasNext()) {
            projectionExpressionBuilder.append(", ");
            projectionExpressionBuilder.append(iter.next().getValue());
        }

        Item item = todolistTable.getItem(new GetItemSpec()
                .withPrimaryKey(FIELD_NAME_ID, todoId)
                .withProjectionExpression(projectionExpressionBuilder.toString())
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
            throw new RecordNotFoundException("The todo to be updated is not found");
        }
        StringBuilder addStatementBuilder = new StringBuilder("");
        StringBuilder setStatementBuilder = new StringBuilder("");
        ValueMap valueMap = new ValueMap();
        Optional<String> summary = Optional.ofNullable(dbTodo.getSummary());
        if (summary.isPresent()) {
            if (!summary.get().equals(updatable.getSummary())) {
                if (setStatementBuilder.length()>0) {
                    setStatementBuilder.append(", ");
                }
                setStatementBuilder.append(String.format("%s = :val%s", FIELD_NAME_SUMMARY, FIELD_NAME_SUMMARY));
                valueMap.withString(String.format(":val%s", FIELD_NAME_SUMMARY), updatable.getSummary());
            }
        } else {
            if (addStatementBuilder.length()>0) {
                addStatementBuilder.append(", ");
            }
            addStatementBuilder.append(String.format("%s = :val%s", FIELD_NAME_SUMMARY, FIELD_NAME_SUMMARY));
            valueMap.withString(String.format(":val%s", FIELD_NAME_SUMMARY), updatable.getSummary());
        }
        Optional<String> description = Optional.ofNullable(dbTodo.getDescription());
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
        Optional<Instant> updated = Optional.ofNullable(dbTodo.getUpdated());
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
        Optional<TodoStatus> status = Optional.ofNullable(dbTodo.getStatus());
        if (status.isPresent()) {
            if (!status.get().equals(updatable.getStatus())) {
                if (setStatementBuilder.length()>0) {
                    setStatementBuilder.append(", ");
                }
                setStatementBuilder.append(String.format("%s = :val%s", FIELD_NAME_TODO_STATUS, FIELD_NAME_TODO_STATUS));
                valueMap.withString(String.format(":val%s", FIELD_NAME_TODO_STATUS), updatable.getStatus().name());
            }
        } else {
            if (addStatementBuilder.length()>0) {
                addStatementBuilder.append(", ");
            }
            addStatementBuilder.append(String.format("%s = :val%s", FIELD_NAME_TODO_STATUS, FIELD_NAME_TODO_STATUS));
            valueMap.withString(String.format(":val%s", FIELD_NAME_TODO_STATUS), updatable.getStatus().name());
        }
        Set<TodoFeature> supported = dbTodo.getSupportedFeatures();
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

        Table todolistTable = this.dynamoDb.getTable(TABLE_NAME_TODO_LIST);
        StringBuilder updateExpressionBuilder = new StringBuilder("");
        if (!addStatementBuilder.toString().isEmpty()) {
            updateExpressionBuilder.append(String.format("add %s ", addStatementBuilder.toString()));
        }
        if (!setStatementBuilder.toString().isEmpty()) {
            updateExpressionBuilder.append(String.format("set %s ", setStatementBuilder.toString()));
        }
        UpdateItemOutcome outcome = todolistTable.updateItem(new UpdateItemSpec()
                .withPrimaryKey(DynamoDBUtil.primaryKeyBuilder().addComponent(FIELD_NAME_ID, dbTodo.getId()).build())
                .withUpdateExpression(updateExpressionBuilder.toString())
                .withValueMap(valueMap)
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
                .withString(FIELD_NAME_TODO_STATUS, todo.getStatus().name());

        Set<TodoFeature> supported = todo.getSupportedFeatures();
        supported.stream().forEach( feature -> {
            item.withJSON(feature.getValue(), JsonUtil.toJsonString(todo.getAttribute(feature)));
        });

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

        Set<TodoFeature> supported = todo.getSupportedFeatures();
        supported.stream().forEach( feature -> {
            todo.setAttribute(JsonUtil.fromJsonString(item.getJSON(feature.getValue()), TodoAttribute.class));
        });
        return todo;
    }
}

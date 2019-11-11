package ibox.iplanner.api.setup;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import ibox.iplanner.api.service.dbmodel.ActivityDefinition;
import ibox.iplanner.api.service.dbmodel.TodoDefinition;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Slf4j
public class DynamoDBSetup {

    private DynamoDB dynamoDB;

    private DynamoDBSetup() {
    }

    public static DynamoDBSetup of(DynamoDB dynamoDB) {
        DynamoDBSetup dynamoDBSetup = new DynamoDBSetup();
        dynamoDBSetup.dynamoDB = dynamoDB;
        return dynamoDBSetup;
    }

    public void createTodoTable(long readCapacityUnits, long writeCapacityUnits) {

        try {
            ArrayList<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
            // Partition Key
            keySchema.add(new KeySchemaElement().withAttributeName(TodoDefinition.FIELD_NAME_ID).withKeyType(KeyType.HASH));

            ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
            attributeDefinitions.add(new AttributeDefinition().withAttributeName(TodoDefinition.FIELD_NAME_ID).withAttributeType("S"));
            attributeDefinitions.add(new AttributeDefinition().withAttributeName(TodoDefinition.FIELD_NAME_CREATED_BY).withAttributeType("S"));
            attributeDefinitions.add(new AttributeDefinition().withAttributeName(TodoDefinition.FIELD_NAME_TODO_STATUS).withAttributeType("S"));

            CreateTableRequest request = new CreateTableRequest().withTableName(TodoDefinition.TABLE_NAME_TODO_LIST).withKeySchema(keySchema)
                    .withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(readCapacityUnits)
                            .withWriteCapacityUnits(writeCapacityUnits));

            request.setAttributeDefinitions(attributeDefinitions);

            // Global Secondary Index
            ArrayList<GlobalSecondaryIndex> globalSecondaryIndexes = new ArrayList<GlobalSecondaryIndex>();
            globalSecondaryIndexes.add(new GlobalSecondaryIndex().withIndexName(TodoDefinition.GSI_CREATOR_TODO_LIST)
                    .withKeySchema(
                        new KeySchemaElement().withAttributeName(TodoDefinition.FIELD_NAME_CREATED_BY).withKeyType(KeyType.HASH),
                        new KeySchemaElement().withAttributeName(TodoDefinition.FIELD_NAME_TODO_STATUS).withKeyType(KeyType.RANGE)
                    )
                    .withProjection(new Projection().withProjectionType(ProjectionType.ALL))
                    .withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(readCapacityUnits)
                            .withWriteCapacityUnits(writeCapacityUnits)));

            request.setGlobalSecondaryIndexes(globalSecondaryIndexes);

            log.info("Issuing CreateTable request for " + TodoDefinition.TABLE_NAME_TODO_LIST);

            Table table = dynamoDB.createTable(request);

            log.info("Waiting for " + TodoDefinition.TABLE_NAME_TODO_LIST + " to be created...this may take a while...");

            table.waitForActive();

        }
        catch (Exception e) {
            log.error("CreateTable request failed for " + TodoDefinition.TABLE_NAME_TODO_LIST);
            log.error(e.getMessage());
        }
    }

    public void createActivityTable(long readCapacityUnits, long writeCapacityUnits) {

        try {
            ArrayList<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
            // Partition Key
            keySchema.add(new KeySchemaElement().withAttributeName(ActivityDefinition.FIELD_NAME_ID).withKeyType(KeyType.HASH));

            ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
            attributeDefinitions.add(new AttributeDefinition().withAttributeName(ActivityDefinition.FIELD_NAME_ID).withAttributeType("S"));
            attributeDefinitions.add(new AttributeDefinition().withAttributeName(ActivityDefinition.FIELD_NAME_CREATED_BY).withAttributeType("S"));
            attributeDefinitions.add(new AttributeDefinition().withAttributeName(ActivityDefinition.FIELD_NAME_ACTIVITY_STATUS).withAttributeType("S"));

            CreateTableRequest request = new CreateTableRequest().withTableName(ActivityDefinition.TABLE_NAME_ACTIVITIES).withKeySchema(keySchema)
                    .withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(readCapacityUnits)
                            .withWriteCapacityUnits(writeCapacityUnits));

            request.setAttributeDefinitions(attributeDefinitions);

            // Global Secondary Index
            ArrayList<GlobalSecondaryIndex> globalSecondaryIndexes = new ArrayList<GlobalSecondaryIndex>();
            globalSecondaryIndexes.add(new GlobalSecondaryIndex().withIndexName(ActivityDefinition.GSI_CREATOR_ACTIVITIES)
                    .withKeySchema(
                            new KeySchemaElement().withAttributeName(ActivityDefinition.FIELD_NAME_CREATED_BY).withKeyType(KeyType.HASH),
                            new KeySchemaElement().withAttributeName(ActivityDefinition.FIELD_NAME_ACTIVITY_STATUS).withKeyType(KeyType.RANGE)
                    )
                    .withProjection(new Projection().withProjectionType(ProjectionType.ALL))
                    .withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(readCapacityUnits)
                            .withWriteCapacityUnits(writeCapacityUnits)));

            request.setGlobalSecondaryIndexes(globalSecondaryIndexes);

            log.info("Issuing CreateTable request for " + ActivityDefinition.TABLE_NAME_ACTIVITIES);

            Table table = dynamoDB.createTable(request);

            log.info("Waiting for " + ActivityDefinition.TABLE_NAME_ACTIVITIES + " to be created...this may take a while...");

            table.waitForActive();

        }
        catch (Exception e) {
            log.error("CreateTable request failed for " + ActivityDefinition.TABLE_NAME_ACTIVITIES);
            log.error(e.getMessage());
        }
    }
}

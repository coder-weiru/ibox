package ibox.iplanner.api.setup;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Slf4j
public class DynamoDBSetup {

    private static final String EVENTS_TABLE_NAME = "iplanner_events";
    private DynamoDB dynamoDB;

    private DynamoDBSetup() {
    }

    public static DynamoDBSetup of(DynamoDB dynamoDB) {
        DynamoDBSetup util = new DynamoDBSetup();
        util.dynamoDB = dynamoDB;
        return util;
    }

    public void createEventTable(long readCapacityUnits, long writeCapacityUnits) {

        try {
            ArrayList<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
            // Partition Key
            keySchema.add(new KeySchemaElement().withAttributeName("Id").withKeyType(KeyType.HASH));

            ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
            attributeDefinitions.add(new AttributeDefinition().withAttributeName("Id").withAttributeType("S"));
            attributeDefinitions.add(new AttributeDefinition().withAttributeName("CreatedBy").withAttributeType("S"));
            attributeDefinitions.add(new AttributeDefinition().withAttributeName("StartTime").withAttributeType("S"));

            CreateTableRequest request = new CreateTableRequest().withTableName(EVENTS_TABLE_NAME).withKeySchema(keySchema)
                    .withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(readCapacityUnits)
                            .withWriteCapacityUnits(writeCapacityUnits));

            request.setAttributeDefinitions(attributeDefinitions);

            // Global Secondary Index
            ArrayList<GlobalSecondaryIndex> globalSecondaryIndexes = new ArrayList<GlobalSecondaryIndex>();
            globalSecondaryIndexes.add(new GlobalSecondaryIndex().withIndexName("CreatorEventsByStart-GSI")
                    .withKeySchema(
                        new KeySchemaElement().withAttributeName("CreatedBy").withKeyType(KeyType.HASH),
                        new KeySchemaElement().withAttributeName("StartTime").withKeyType(KeyType.RANGE)
                    )
                    .withProjection(new Projection().withProjectionType(ProjectionType.ALL))
                    .withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(readCapacityUnits)
                            .withWriteCapacityUnits(writeCapacityUnits)));

            request.setGlobalSecondaryIndexes(globalSecondaryIndexes);

            log.info("Issuing CreateTable request for " + EVENTS_TABLE_NAME);

            Table table = dynamoDB.createTable(request);

            log.info("Waiting for " + EVENTS_TABLE_NAME + " to be created...this may take a while...");

            table.waitForActive();

        }
        catch (Exception e) {
            log.error("CreateTable request failed for " + EVENTS_TABLE_NAME);
            log.error(e.getMessage());
        }
    }
}

package ibox.iplanner.api.setup;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

import static ibox.iplanner.api.service.dbmodel.EventDefinition.*;

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

    public void createEventTable(long readCapacityUnits, long writeCapacityUnits) {

        try {
            ArrayList<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
            // Partition Key
            keySchema.add(new KeySchemaElement().withAttributeName(FIELD_NAME_ID).withKeyType(KeyType.HASH));

            ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
            attributeDefinitions.add(new AttributeDefinition().withAttributeName(FIELD_NAME_ID).withAttributeType("S"));
            attributeDefinitions.add(new AttributeDefinition().withAttributeName(FIELD_NAME_CREATED_BY).withAttributeType("S"));
            attributeDefinitions.add(new AttributeDefinition().withAttributeName(FIELD_NAME_START_TIME).withAttributeType("S"));

            CreateTableRequest request = new CreateTableRequest().withTableName(TABLE_NAME_EVENTS).withKeySchema(keySchema)
                    .withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(readCapacityUnits)
                            .withWriteCapacityUnits(writeCapacityUnits));

            request.setAttributeDefinitions(attributeDefinitions);

            // Global Secondary Index
            ArrayList<GlobalSecondaryIndex> globalSecondaryIndexes = new ArrayList<GlobalSecondaryIndex>();
            globalSecondaryIndexes.add(new GlobalSecondaryIndex().withIndexName(GSI_CREATOR_EVENTS_SORT_BY_START_TIME)
                    .withKeySchema(
                        new KeySchemaElement().withAttributeName(FIELD_NAME_CREATED_BY).withKeyType(KeyType.HASH),
                        new KeySchemaElement().withAttributeName(FIELD_NAME_START_TIME).withKeyType(KeyType.RANGE)
                    )
                    .withProjection(new Projection().withProjectionType(ProjectionType.ALL))
                    .withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(readCapacityUnits)
                            .withWriteCapacityUnits(writeCapacityUnits)));

            request.setGlobalSecondaryIndexes(globalSecondaryIndexes);

            log.info("Issuing CreateTable request for " + TABLE_NAME_EVENTS);

            Table table = dynamoDB.createTable(request);

            log.info("Waiting for " + TABLE_NAME_EVENTS + " to be created...this may take a while...");

            table.waitForActive();

        }
        catch (Exception e) {
            log.error("CreateTable request failed for " + TABLE_NAME_EVENTS);
            log.error(e.getMessage());
        }
    }
}

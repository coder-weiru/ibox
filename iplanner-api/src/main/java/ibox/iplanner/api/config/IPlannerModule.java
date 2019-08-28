package ibox.iplanner.api.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import ibox.iplanner.api.service.EventDataService;

import javax.inject.Singleton;

@Module
public class IPlannerModule {

    @Singleton
    @Provides
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Singleton
    @Provides
    public DynamoDB dynamoDB() {
        AWSCredentialsProvider awsCredentialsProvider = InstanceProfileCredentialsProvider.getInstance();
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder
                .standard().withCredentials(awsCredentialsProvider).build();
        return new DynamoDB(client);
    }

    @Singleton
    @Provides
    public EventDataService eventDataService() {
        return new EventDataService(dynamoDB());
    }

}

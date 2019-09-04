package ibox.iplanner.api.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class DynamoDBModule {

    @Singleton
    @Provides
    public static DynamoDB dynamoDB() {
        AWSCredentialsProvider awsCredentialsProvider = InstanceProfileCredentialsProvider.getInstance();
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder
                .standard().withCredentials(awsCredentialsProvider).build();
        return new DynamoDB(client);
    }
}

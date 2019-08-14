package ibox.iplanner.api;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import ibox.iplanner.api.service.EventDataService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    public AppConfig() {

    }

    @Bean
    public DynamoDB dynamoDB() {
        AWSCredentialsProvider awsCredentialsProvider = InstanceProfileCredentialsProvider.getInstance();
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder
                .standard().withCredentials(awsCredentialsProvider).build();
        return new DynamoDB(client);
    }

    @Bean
    public EventDataService eventDataService() {
        return new EventDataService(dynamoDB());
    }

}

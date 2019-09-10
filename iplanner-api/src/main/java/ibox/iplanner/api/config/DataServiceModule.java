package ibox.iplanner.api.config;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import dagger.Module;
import dagger.Provides;
import ibox.iplanner.api.service.ActivityDataService;
import ibox.iplanner.api.service.EventDataService;

import javax.inject.Singleton;

@Module(includes = DynamoDBModule.class)
public class DataServiceModule {

    @Singleton
    @Provides
    public static EventDataService eventDataService(DynamoDB dynamoDB) {
        return new EventDataService(dynamoDB);
    }

    @Singleton
    @Provides
    public static ActivityDataService activityDataService(DynamoDB dynamoDB) {
        return new ActivityDataService(dynamoDB);
    }
}

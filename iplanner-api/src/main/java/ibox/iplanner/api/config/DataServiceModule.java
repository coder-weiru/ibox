package ibox.iplanner.api.config;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import dagger.Module;
import dagger.Provides;
import ibox.iplanner.api.service.ActivityDataService;
import ibox.iplanner.api.service.TodoDataService;

import javax.inject.Singleton;

@Module(includes = DynamoDBModule.class)
public class DataServiceModule {

    @Singleton
    @Provides
    public static TodoDataService todoDataService(DynamoDB dynamoDB) {
        return new TodoDataService(dynamoDB);
    }

    @Singleton
    @Provides
    public static ActivityDataService activityDataService(DynamoDB dynamoDB) {
        return new ActivityDataService(dynamoDB);
    }
}

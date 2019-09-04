package ibox.iplanner.api.config;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import dagger.Module;
import dagger.Provides;
import ibox.iplanner.api.service.EventDataService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Module(includes = TestDynamoDBModule.class)
public class TestDataServiceModule {

    @Singleton
    @Provides
    @Inject
    public EventDataService eventDataService(DynamoDB dynamoDB) {
        return new EventDataService(dynamoDB);
    }
}

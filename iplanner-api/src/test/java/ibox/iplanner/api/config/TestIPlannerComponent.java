package ibox.iplanner.api.config;

import dagger.Component;
import ibox.iplanner.api.lambda.handler.*;

import javax.inject.Singleton;

@Singleton
@Component(modules = {TestDynamoDBModule.class, TestDataServiceModule.class, HandlerModule.class, ValidatorModule.class})
public interface TestIPlannerComponent {
    void inject(AddEventHandler requestHandler);
    void inject(GetEventHandler requestHandler);
    void inject(ListEventHandler requestHandler);
    void inject(UpdateEventHandler requestHandler);
    void inject(DeleteEventHandler requestHandler);

    void inject(AddActivityHandler requestHandler);
    void inject(GetActivityHandler requestHandler);
    void inject(ListActivityHandler requestHandler);
    void inject(UpdateActivityHandler requestHandler);
    void inject(DeleteActivityHandler requestHandler);

    void inject(GetActivitySchemaHandler requestHandler);

}

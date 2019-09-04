package ibox.iplanner.api.config;

import dagger.Component;
import ibox.iplanner.api.lambda.handler.AddEventHandler;
import ibox.iplanner.api.lambda.handler.GetEventHandler;
import ibox.iplanner.api.lambda.handler.ListEventHandler;

import javax.inject.Singleton;

@Singleton
@Component(modules = {TestDynamoDBModule.class, TestDataServiceModule.class, HandlerModule.class})
public interface TestIPlannerComponent {
    void inject(AddEventHandler requestHandler);
    void inject(GetEventHandler requestHandler);
    void inject(ListEventHandler requestHandler);
}

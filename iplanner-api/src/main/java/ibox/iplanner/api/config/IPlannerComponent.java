package ibox.iplanner.api.config;

import dagger.Component;
import ibox.iplanner.api.lambda.handler.*;

import javax.inject.Singleton;

@Singleton
@Component(modules = {DynamoDBModule.class, DataServiceModule.class, HandlerModule.class})
public interface IPlannerComponent {

    void inject(AddEventHandler requestHandler);
    void inject(GetEventHandler requestHandler);
    void inject(ListEventHandler requestHandler);

    void inject(AddActivityHandler requestHandler);
    void inject(GetActivityHandler requestHandler);
    void inject(ListActivityHandler requestHandler);

}

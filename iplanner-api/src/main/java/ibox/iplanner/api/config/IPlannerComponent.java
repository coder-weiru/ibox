package ibox.iplanner.api.config;

import dagger.Component;
import ibox.iplanner.api.lambda.AddEventHandler;
import ibox.iplanner.api.lambda.GetEventHandler;
import ibox.iplanner.api.lambda.ListEventHandler;

import javax.inject.Singleton;

@Singleton
@Component(modules = {IPlannerModule.class})
public interface IPlannerComponent {

    void inject(AddEventHandler requestHandler);
    void inject(GetEventHandler requestHandler);
    void inject(ListEventHandler requestHandler);

}

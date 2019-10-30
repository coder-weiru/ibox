package ibox.iplanner.api.config;

import dagger.Component;
import ibox.iplanner.api.lambda.handler.*;

import javax.inject.Singleton;

@Singleton
@Component(modules = {TestDynamoDBModule.class, TestDataServiceModule.class, HandlerModule.class, ValidatorModule.class})
public interface TestIPlannerComponent {
    void inject(AddTodoHandler requestHandler);
    void inject(GetTodoHandler requestHandler);
    void inject(ListTodoHandler requestHandler);
    void inject(UpdateTodoHandler requestHandler);
    void inject(DeleteTodoHandler requestHandler);

    void inject(AddActivityHandler requestHandler);
    void inject(GetActivityHandler requestHandler);
    void inject(ListActivityHandler requestHandler);
    void inject(UpdateActivityHandler requestHandler);
    void inject(DeleteActivityHandler requestHandler);

    void inject(GetEntityDefinitionHandler requestHandler);
    void inject(GetActivityTemplateHandler requestHandler);
    void inject(CreateTodoFromActivityHandler requestHandler);
}

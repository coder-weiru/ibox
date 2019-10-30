package ibox.iplanner.api.config;

import dagger.Module;
import dagger.Provides;
import ibox.iplanner.api.lambda.handler.*;

import javax.inject.Singleton;

@Module(includes = ValidatorModule.class)
public class HandlerModule {

    @Singleton
    @Provides
    public static AddTodoHandler addTodoHandler() {
        return new AddTodoHandler();
    }

    @Singleton
    @Provides
    public static GetTodoHandler getTodoHandler() {
        return new GetTodoHandler();
    }

    @Singleton
    @Provides
    public static ListTodoHandler listTodoHandler() {
        return new ListTodoHandler();
    }

    @Singleton
    @Provides
    public static UpdateTodoHandler updateTodoHandler() {
        return new UpdateTodoHandler();
    }

    @Singleton
    @Provides
    public static DeleteTodoHandler deleteTodoHandler() {
        return new DeleteTodoHandler();
    }

    @Singleton
    @Provides
    public static AddActivityHandler addActivityHandler() {
        return new AddActivityHandler();
    }

    @Singleton
    @Provides
    public static GetActivityHandler getActivityHandler() {
        return new GetActivityHandler();
    }

    @Singleton
    @Provides
    public static ListActivityHandler listActivityHandler() {
        return new ListActivityHandler();
    }

    @Singleton
    @Provides
    public static UpdateActivityHandler updateActivityHandler() {
        return new UpdateActivityHandler();
    }

    @Singleton
    @Provides
    public static DeleteActivityHandler deleteActivityHandler() {
        return new DeleteActivityHandler();
    }

    @Singleton
    @Provides
    public static GetEntityDefinitionHandler getEntityDefinitionHandler() {
        return new GetEntityDefinitionHandler();
    }

    @Singleton
    @Provides
    public static GetActivityTemplateHandler getActivityTemplateHandler() { return new GetActivityTemplateHandler();  }

    @Singleton
    @Provides
    public static CreateTodoFromActivityHandler createTodoFromActivityHandler() { return new CreateTodoFromActivityHandler();  }
}

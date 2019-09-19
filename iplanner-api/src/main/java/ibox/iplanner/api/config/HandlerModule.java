package ibox.iplanner.api.config;

import dagger.Module;
import dagger.Provides;
import ibox.iplanner.api.lambda.handler.*;

import javax.inject.Singleton;

@Module(includes = ValidatorModule.class)
public class HandlerModule {

    @Singleton
    @Provides
    public static AddEventHandler addEventHandler() {
        return new AddEventHandler();
    }

    @Singleton
    @Provides
    public static GetEventHandler getEventHandler() {
        return new GetEventHandler();
    }

    @Singleton
    @Provides
    public static ListEventHandler listEventHandler() {
        return new ListEventHandler();
    }

    @Singleton
    @Provides
    public static UpdateEventHandler updateEventHandler() {
        return new UpdateEventHandler();
    }

    @Singleton
    @Provides
    public static DeleteEventHandler deleteEventHandler() {
        return new DeleteEventHandler();
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
    public static CreateEventFromActivityHandler createEventFromActivityHandler() { return new CreateEventFromActivityHandler();  }
}

package ibox.iplanner.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import ibox.iplanner.api.lambda.exception.GlobalExceptionHandler;
import ibox.iplanner.api.lambda.handler.*;
import ibox.iplanner.api.lambda.validation.BeanValidator;
import ibox.iplanner.api.lambda.validation.RequestEventValidator;

import javax.inject.Singleton;

@Module
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
    public static ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Singleton
    @Provides
    public static RequestEventValidator requestEventValidator() {
        return new RequestEventValidator();
    }

    @Singleton
    @Provides
    public static GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

    @Singleton
    @Provides
    public static BeanValidator beanValidator() { return new BeanValidator(); }

}

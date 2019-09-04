package ibox.iplanner.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import ibox.iplanner.api.lambda.exception.GlobalExceptionHandler;
import ibox.iplanner.api.lambda.handler.AddEventHandler;
import ibox.iplanner.api.lambda.handler.GetEventHandler;
import ibox.iplanner.api.lambda.handler.ListEventHandler;
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

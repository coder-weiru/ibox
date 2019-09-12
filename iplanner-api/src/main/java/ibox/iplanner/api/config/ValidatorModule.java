package ibox.iplanner.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import ibox.iplanner.api.lambda.exception.GlobalExceptionHandler;
import ibox.iplanner.api.lambda.validation.EntitySchemaMap;
import ibox.iplanner.api.lambda.validation.JsonSchemaValidator;
import ibox.iplanner.api.lambda.validation.RequestEventValidator;

import javax.inject.Singleton;

@Module
public class ValidatorModule {

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
    public static JsonSchemaValidator jsonSchemaValidator() { return new JsonSchemaValidator(entitySchemaMap()); }

    @Singleton
    @Provides
    public static EntitySchemaMap entitySchemaMap() { return new EntitySchemaMap(); }
}

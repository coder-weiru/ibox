package ibox.iplanner.api.lambda.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.load.configuration.LoadingConfiguration;
import com.github.fge.jsonschema.core.load.uri.URITranslatorConfiguration;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import ibox.iplanner.api.model.Activity;
import ibox.iplanner.api.model.Todo;
import ibox.iplanner.api.model.Meeting;
import ibox.iplanner.api.model.Task;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class EntitySchemaMap {

    private static final String URI_BASE = "http://iplanner-api.ibox.com";
    private static final String RESOURCE_BASE = "resource:/schema/ibox/iplanner/api/model";

    @NonNull
    private final Map<Class<?>, String> entitySchemaLookup;
    private JsonSchemaFactory factory = JsonSchemaFactory.byDefault();

    public EntitySchemaMap() {
        entitySchemaLookup = new HashMap<>();

        entitySchemaLookup.put(Activity.class, getResourcePath(Activity.class) + "activity.schema.json");
        entitySchemaLookup.put(Todo.class, getResourcePath(Todo.class) + "todo.schema.json");
        entitySchemaLookup.put(Meeting.class, getResourcePath(Meeting.class) + "meeting.schema.json");
        entitySchemaLookup.put(Task.class, getResourcePath(Task.class) + "task.schema.json");

        initializeJsonSchemaFactory();
    }

    private void initializeJsonSchemaFactory() {
        final URITranslatorConfiguration translatorCfg
                = URITranslatorConfiguration.newBuilder()
                .addSchemaRedirect(String.format("%s/activity.schema.json#", URI_BASE), String.format("%s/activity.schema.json#", RESOURCE_BASE))
                .addSchemaRedirect(String.format("%s/attributes.schema.json#", URI_BASE), String.format("%s/attributes.schema.json#", RESOURCE_BASE))
                .freeze();
        final LoadingConfiguration cfg = LoadingConfiguration.newBuilder()
                .setURITranslatorConfiguration(translatorCfg).freeze();

        factory = JsonSchemaFactory.newBuilder()
                .setLoadingConfiguration(cfg).freeze();
    }

    private String getResourcePath(Class<?> entityClass) {
        return String.format("/schema/%s/", ClassUtil.getPackageName(entityClass).replace(".", "/"));
    }

    public String getSchemaResourcePath(Class<?> entityClass) {
        return entitySchemaLookup.get(entityClass);
    }

    public JsonSchema getSchema(Class<?> entityClass) throws IOException, ProcessingException {
        final JsonNode jsonSchema = JsonLoader.fromResource(getSchemaResourcePath(entityClass));
        final JsonSchema schema = factory.getJsonSchema(jsonSchema);

        return schema;
    }

    public JsonSchema getSchema(Class<?> entityClass, String pointer) throws IOException, ProcessingException {
        final JsonNode jsonSchema = JsonLoader.fromResource(getSchemaResourcePath(entityClass));
        final JsonSchema schema = factory.getJsonSchema(jsonSchema, pointer);

        return schema;
    }

    public Boolean contains(final Class<?> entityClass) {
        JsonSchema schema = null;
        try {
            schema = getSchema(entityClass);
        } catch (IOException | ProcessingException ex) {
            // do nothing
        }
        return Optional.ofNullable(schema).isPresent();
    }

    public Boolean containsSchemaResource(final String resourcePath) {
        return entitySchemaLookup.containsValue(resourcePath);
    }
}

package ibox.iplanner.api.lambda.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import ibox.iplanner.api.model.Activity;
import ibox.iplanner.api.model.Event;
import ibox.iplanner.api.model.updatable.Updatable;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class EntitySchemaMap {

    private static final String URI_BASE = "http://iplanner-api.ibox.com";

    @NonNull
    private final Map<Class<?>, String> entitySchemaLookup;
    private JsonSchemaFactory factory = JsonSchemaFactory.byDefault();

    public EntitySchemaMap() {
        entitySchemaLookup = new HashMap<>();

        entitySchemaLookup.put(Activity.class, getResourcePath(Activity.class) + "activity.schema.json");
        entitySchemaLookup.put(Event.class, getResourcePath(Event.class) + "event.schema.json");
        entitySchemaLookup.put(Updatable.class, getResourcePath(Updatable.class) + "updatable.schema.json");
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
}

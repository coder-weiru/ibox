package ibox.iplanner.api.lambda.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import ibox.iplanner.api.lambda.exception.InvalidInputException;
import ibox.iplanner.api.model.ApiError;

import java.util.ArrayList;
import java.util.List;

import static ibox.iplanner.api.util.ApiErrorConstants.ERROR_BAD_REQUEST;
import static ibox.iplanner.api.util.ApiErrorConstants.SC_BAD_REQUEST;

public class JsonSchemaValidator {

    private final EntitySchemaMap entitySchemaMap;

    private static final String INVALID_INPUT_ERROR_MESSAGE = "Input %s invalid";
    private static final String REQUEST_BODY_INVALID_ERROR_MESSAGE = "Request body json invalid %s: ";

    public JsonSchemaValidator(EntitySchemaMap entitySchemaMap) {
        this.entitySchemaMap = entitySchemaMap;
    }

    public void validate(String json, Class<?> entityClass, String subject) {
        JsonSchema jsonSchema;
        try {
            jsonSchema = entitySchemaMap.getSchema(entityClass, subject);
            validate(JsonLoader.fromString(json), entityClass, jsonSchema);
        } catch (InvalidInputException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void validate(String json, Class<?> entityClass) {
        JsonSchema jsonSchema;
        try {
            jsonSchema = entitySchemaMap.getSchema(entityClass);
            validate(JsonLoader.fromString(json), entityClass, jsonSchema);
        } catch (InvalidInputException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void validate(JsonNode json, Class<?> entityClass, JsonSchema jsonSchema) throws ProcessingException {
        ProcessingReport report;
        report = jsonSchema.validate(json);

        if (!report.isSuccess()) {
            List<String> errorDetails = new ArrayList<>();
            report.forEach(e -> errorDetails.add(e.getMessage()));
            ApiError error = ApiError.builder()
                    .error(ERROR_BAD_REQUEST)
                    .message(String.format(REQUEST_BODY_INVALID_ERROR_MESSAGE, report.toString()))
                    .status(SC_BAD_REQUEST)
                    .errorDetails(errorDetails)
                    .build();
            throw new InvalidInputException(String.format(INVALID_INPUT_ERROR_MESSAGE, entityClass.getSimpleName()), error);
        }

    }
}

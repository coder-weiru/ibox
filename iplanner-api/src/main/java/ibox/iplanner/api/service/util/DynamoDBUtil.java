package ibox.iplanner.api.service.util;

import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class DynamoDBUtil {

    private DynamoDBUtil() {}

    public static AttributeUpdate addAttributeUpdate(String attributeName, Object attributeValue) {

        AttributeUpdate attributeUpdate = new AttributeUpdate(attributeName);
        if (Number.class.isAssignableFrom(attributeValue.getClass())) {
            attributeUpdate.addNumeric((Number)attributeValue);
        } else {
            attributeUpdate.addElements(attributeValue);
        }
        return attributeUpdate;
    }

    public static AttributeUpdate updateAttributeUpdate(String attributeName, Object attributeValue) {

        AttributeUpdate attributeUpdate = new AttributeUpdate(attributeName);
        attributeUpdate.put(attributeValue);
        return attributeUpdate;
    }

    public static AttributeUpdate deleteAttributeUpdate(String attributeName, Object attributeValue) {

        AttributeUpdate attributeUpdate = new AttributeUpdate(attributeName);
        attributeUpdate.removeElements(attributeValue);
        return attributeUpdate;
    }

    public static PrimaryKeyBuilder primaryKeyBuilder() {

        return new PrimaryKeyBuilder();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class KeyAttribute {

        @NotNull
        private String attributeName;
        @NotNull
        private Object value;

    }

    public static class PrimaryKeyBuilder {

        private final List<KeyAttribute> components = new ArrayList<>();

        public PrimaryKeyBuilder addComponent(String keyAttributeName, Object keyAttributeValue) {
            this.components.add(new KeyAttribute(keyAttributeName, keyAttributeValue));
            return this;
        }

        public PrimaryKey build() {
            PrimaryKey primaryKey = new PrimaryKey();
            components.stream()
                    .forEach(e -> primaryKey.addComponent(e.getAttributeName(), e.getValue()));

            return primaryKey;
        }
    }

}

package ibox.iplanner.api.service.util;

import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import ibox.iplanner.api.model.updatable.UpdatableAttribute;
import ibox.iplanner.api.model.updatable.UpdatableKey;
import ibox.iplanner.api.model.updatable.UpdateAction;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DynamoDBUtil {

    private DynamoDBUtil() {}

    public static AttributeUpdate buildAttributeUpdate(UpdatableAttribute updatableAttribute) {

        AttributeUpdate attributeUpdate = new AttributeUpdate(updatableAttribute.getAttributeName());
        Object value = updatableAttribute.getValue();
        if (UpdateAction.ADD.equals(updatableAttribute.getAction())) {
            if (Number.class.isAssignableFrom(value.getClass())) {
                attributeUpdate.addNumeric((Number)value);
            } else {
                attributeUpdate.addElements(value);
            }
        } else if (UpdateAction.UPDATE.equals(updatableAttribute.getAction())) {
            attributeUpdate.put(value);
        } else if (UpdateAction.DELETE.equals(updatableAttribute.getAction())) {
            attributeUpdate.removeElements(value);
        } else {
            // Default to put
            attributeUpdate.put(value);
        }
        return attributeUpdate;
    }

    public static List<AttributeUpdate> buildAttributeUpdateList(Set<UpdatableAttribute> updatableAttributeSet) {
        return updatableAttributeSet.stream()
                .map(e -> buildAttributeUpdate(e))
                .collect(Collectors.toList());
    }

    public static PrimaryKey buildPrimaryKey(UpdatableKey updatableKey) {
        PrimaryKey primaryKey = new PrimaryKey();
        updatableKey.getComponents().stream()
                .forEach(e -> primaryKey.addComponent(e.getAttributeName(), e.getValue()));

        return primaryKey;
    }

}

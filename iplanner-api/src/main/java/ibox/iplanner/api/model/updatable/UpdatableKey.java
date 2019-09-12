package ibox.iplanner.api.model.updatable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class UpdatableKey {

    private final List<KeyAttribute> components = new ArrayList<>();

    public UpdatableKey(KeyAttribute... components) {
        this.addComponents(components);
    }

    public UpdatableKey(String hashKeyName, Object hashKeyValue) {
        this.addComponent(hashKeyName, hashKeyValue);
    }

    public List<KeyAttribute> getComponents() {
        return this.components;
    }

    public boolean hasComponent(String attrName) {

        return !this.components.stream().filter( k -> k.getAttributeName().equals(attrName) ).collect(Collectors.toList()).isEmpty();
    }

    public UpdatableKey addComponents(KeyAttribute... components) {
        if (components != null) {
            KeyAttribute[] componentArray = components;
            Arrays.stream(componentArray).forEach(ka -> this.components.add(ka));
        }

        return this;
    }

    public UpdatableKey addComponent(String keyAttributeName, Object keyAttributeValue) {
        this.components.add(new KeyAttribute(keyAttributeName, keyAttributeValue));
        return this;
    }

    public String toString() {
        return String.valueOf(this.components);
    }

    public int hashCode() {
        return this.components.hashCode();
    }

    public boolean equals(Object in) {
        if (in instanceof UpdatableKey) {
            UpdatableKey that = (UpdatableKey)in;
            return this.components.equals(that.components);
        } else {
            return false;
        }
    }
}

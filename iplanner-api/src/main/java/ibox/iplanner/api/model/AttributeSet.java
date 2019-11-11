package ibox.iplanner.api.model;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class AttributeSet {
    private Set<TodoAttribute> attributes;

    public AttributeSet() {
        attributes = new HashSet<>();
    }

    public TodoAttribute getAttribute(TodoFeature feature) {
        return attributes.stream().filter(attribute -> attribute.feature().equals(feature)).findFirst().get();
    }

    public Set<TodoAttribute> getAllAttributes() {
        return attributes;
    }

    public void addAttribute(TodoAttribute attribute) {
        attributes.add(attribute);
    }

    public void removeAttribute(TodoFeature feature) {
        TodoAttribute attribute = getAttribute(feature);
        if (attribute!=null) {
            attributes.remove(attribute);
        }
    }

    public void setAttribute(TodoAttribute attribute) {
        TodoFeature feature = attribute.feature();
        TodoAttribute existing = getAttribute(feature);
        if (existing!=null) {
            attributes.remove(existing);
        }
        addAttribute(attribute);
    }

    public Set<TodoFeature> getSupportedFeatures() {
        return attributes.stream().map(attribute -> attribute.feature()).collect(Collectors.toSet());
    }

    public boolean supports(TodoFeature feature) {
        return getSupportedFeatures().contains(feature);
    }

    public boolean supports(Set<TodoFeature> features) {
        return features.stream().allMatch(todoFeature -> supports(todoFeature));
    }

}

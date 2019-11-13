package ibox.iplanner.api.util;

import ibox.iplanner.api.model.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class TestHelper {

    private TestHelper() {}

    public static void verifyTaggingAttributeAreEqual(TagAttribute expected, TagAttribute actual) {
        if (expected!=null && actual!=null) {
            actual.getTags().stream().forEach( s -> assertTrue(expected.getTags().contains(s)));
        }
    }

    public static void verifyEventAttributeAreEqual(EventAttribute expected, EventAttribute actual) {
        if (expected!=null && actual!=null) {
            assertTrue(expected.getStart().equals(actual.getStart()));
            assertTrue(expected.getEnd().equals(actual.getEnd()));
            assertTrue(expected.getFrequency().equals(actual.getFrequency()));
            assertTrue(expected.getRecurrence().size()==actual.getRecurrence().size());
        }
    }

    public static void verifyLocationAttributeAreEqual(LocationAttribute expected, LocationAttribute actual) {
        if (expected!=null && actual!=null) {
            assertTrue(expected.getLocation().equals(actual.getLocation()));
        }
    }

    public static void verifyTimelineAttributeAreEqual(TimelineAttribute expected, TimelineAttribute actual) {
        if (expected!=null && actual!=null) {
            assertTrue(expected.getStartBy().equals(actual.getStartBy()));
            assertTrue(expected.getCompleteBy().equals(actual.getCompleteBy()));
        }
    }

    public static void verifyTodoAreEqual(Todo expected, Todo actual) {

        assertThat(expected.getId(), is(equalTo(actual.getId())));
        assertThat(expected.getSummary(), is(equalTo(actual.getSummary())));
        assertThat(expected.getDescription(), is(equalTo(actual.getDescription())));
        assertThat(expected.getCreator().getId(), is(equalTo(actual.getCreator().getId())));
        assertThat(expected.getCreator().getDisplayName(), is(equalTo(actual.getCreator().getDisplayName())));
        assertThat(expected.getCreator().getEmail(), is(equalTo(actual.getCreator().getEmail())));
        assertThat(expected.getCreator().getSelf(), is(equalTo(actual.getCreator().getSelf())));
        assertThat(expected.getCreated(), is(equalTo(actual.getCreated())));
        assertThat(expected.getUpdated(), is(equalTo(actual.getUpdated())));
        assertThat(expected.getStatus(), is(equalTo(actual.getStatus())));

        verifyTodoAttributeSetAreEqual(expected, actual);
    }

    public static void verifyTodoAttributeSetAreEqual(Todo expected, Todo actual) {
        assertThat(expected.getActivityType(), is(equalTo(actual.getActivityType())));
        expected.getSupportedFeatures().stream().forEach(feature -> {
            TodoAttribute expectedAttribute = expected.getAttribute(feature);
            TodoAttribute actualAttribute = actual.getAttribute(feature);
            verifyTodoAttributeAreEqual(expectedAttribute, actualAttribute);
        });
    }

    public static void verifyTodoAttributeAreEqual(TodoAttribute expected, TodoAttribute actual) {

        if (expected.getClass().equals(TagAttribute.class)) {
            verifyTaggingAttributeAreEqual((TagAttribute) expected, (TagAttribute) actual);
        }
        else if (expected.getClass().equals(EventAttribute.class)) {
            verifyEventAttributeAreEqual((EventAttribute) expected, (EventAttribute) actual);
        }
        else if (expected.getClass().equals(LocationAttribute.class)) {
            verifyLocationAttributeAreEqual((LocationAttribute) expected, (LocationAttribute) actual);
        }
        else if (expected.getClass().equals(TimelineAttribute.class)) {
            verifyTimelineAttributeAreEqual((TimelineAttribute) expected, (TimelineAttribute) actual);
        }
    }
}

package ibox.iplanner.api.service;

import ibox.iplanner.api.model.EventAttribute;
import ibox.iplanner.api.model.LocationAttribute;
import ibox.iplanner.api.model.TagAttribute;
import ibox.iplanner.api.model.TimelineAttribute;

import static org.junit.Assert.assertTrue;

public class TestHelper {

    private TestHelper() {}

    public static void verifyTaggingAttributeAreEqual(TagAttribute expected, TagAttribute actual) {
        actual.getTags().stream().forEach( s -> assertTrue(expected.getTags().contains(s)));
    }

    public static void verifyEventAttributeAreEqual(EventAttribute expected, EventAttribute actual) {
        assertTrue(expected.getStart().equals(actual.getStart()));
        assertTrue(expected.getEnd().equals(actual.getEnd()));
        assertTrue(expected.getFrequency().equals(actual.getFrequency()));
        assertTrue(expected.getRecurrence().size()==actual.getRecurrence().size());
    }

    public static void verifyLocationAttributeAreEqual(LocationAttribute expected, LocationAttribute actual) {
        assertTrue(expected.getLocation().equals(actual.getLocation()));
    }

    public static void verifyTimelineAttributeAreEqual(TimelineAttribute expected, TimelineAttribute actual) {
        assertTrue(expected.getStartBy().equals(actual.getStartBy()));
        assertTrue(expected.getCompleteBy().equals(actual.getCompleteBy()));
    }
}

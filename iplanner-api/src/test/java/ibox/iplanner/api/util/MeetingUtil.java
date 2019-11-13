package ibox.iplanner.api.util;

import ibox.iplanner.api.model.*;

import java.util.*;

public class MeetingUtil extends ActivityUtil {

    public static Meeting anyMeetingWithoutId() {
        Meeting meeting = new Meeting();
        meeting.setTitle(anyActivityTitle());
        meeting.setDescription(anyDescription());
        meeting.setStatus(anyActivityStatus());
        meeting.setActivityType(Activities.MEETING_TYPE);
        meeting.setCreated(anyCreatedTime());
        meeting.setUpdated(anyUpdatedTime());
        meeting.setCreator(anyActivityCreator());
        meeting.setAttribute(anyTagAttribute());
        meeting.setAttribute(anyEventAttribute());
        meeting.setAttribute(anyLocationAttribute());
        return meeting;
    }

    public static Meeting anyMeeting() {
        Meeting meeting = MeetingUtil.anyMeetingWithoutId();
        meeting.setId(anyUUID());
        return meeting;
    }

    public static List<Meeting> anyMeetingList() {
        int size = new Random().nextInt(10);
        List<Meeting> meetingList = new ArrayList<>();
        int i = 0;
        while (i < size) {
            meetingList.add(anyMeeting());
            i ++;
        }
        return meetingList;
    }

    public static EventAttribute anyEventAttribute() {
        return EventAttribute.builder()
                .start(anyStartTime())
                .end(anyEndTime())
                .frequency(anyFrequency())
                .recurrence(new HashSet<>(Arrays.asList(new String[] {
                        anyStartTime().toString(),
                        anyStartTime().toString(),
                        anyStartTime().toString()
                })))
                .build();
    }

    public static LocationAttribute anyLocationAttribute() {
        return LocationAttribute.builder()
                .location(anyLocation())
                .build();
    }
}

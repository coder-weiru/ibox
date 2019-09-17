package ibox.iplanner.api.util;

import ibox.iplanner.api.model.Frequency;
import ibox.iplanner.api.model.Meeting;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MeetingUtil extends ActivityUtil {


    public static Meeting anyMeetingWithoutId() {
        Meeting meeting = new Meeting();
        meeting.setTitle(anyActivityTitle());
        meeting.setDescription(anyDescription());
        meeting.setStatus(anyActivityStatus().name());
        meeting.setType("meeting");
        meeting.setCreated(anyCreatedTime());
        meeting.setUpdated(anyUpdatedTime());
        meeting.setCreator(anyActivityCreator());
        meeting.setFrequency(Frequency.DAILY);

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
}

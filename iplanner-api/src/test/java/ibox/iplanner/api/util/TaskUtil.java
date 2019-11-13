package ibox.iplanner.api.util;

import ibox.iplanner.api.model.Activities;
import ibox.iplanner.api.model.Task;
import ibox.iplanner.api.model.TimelineAttribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TaskUtil extends ActivityUtil {


    public static Task anyTaskWithoutId() {
        Task task = new Task();
        task.setTitle(anyActivityTitle());
        task.setDescription(anyDescription());
        task.setStatus(anyActivityStatus());
        task.setActivityType(Activities.TASK_TYPE);
        task.setCreated(anyCreatedTime());
        task.setUpdated(anyUpdatedTime());
        task.setCreator(anyActivityCreator());
        task.setAttribute(anyTagAttribute());
        task.setAttribute(anyTimelineAttribute());
        return task;
    }

    public static Task anyTask() {
        Task task = TaskUtil.anyTaskWithoutId();
        task.setId(anyUUID());
        return task;
    }

    public static List<Task> anyTaskList() {
        int size = new Random().nextInt(10);
        List<Task> taskList = new ArrayList<>();
        int i = 0;
        while (i < size) {
            taskList.add(anyTask());
            i ++;
        }
        return taskList;
    }

    public static TimelineAttribute anyTimelineAttribute() {
        return TimelineAttribute.builder()
                .startBy(anyStartTime())
                .completeBy(anyEndTime())
                .build();
    }
}

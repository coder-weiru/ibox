package ibox.iplanner.api.util;

import ibox.iplanner.api.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TaskUtil extends ActivityUtil {


    public static Task anyTaskWithoutId() {
        Task task = new Task();
        task.setTitle(anyActivityTitle());
        task.setDescription(anyDescription());
        task.setStatus(anyActivityStatus().name());
        task.setType(anyActivityType());
        task.setCreated(anyCreatedTime());
        task.setUpdated(anyUpdatedTime());
        task.setCreator(anyActivityCreator());
        task.setDeadline(anyEndTime());

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
}

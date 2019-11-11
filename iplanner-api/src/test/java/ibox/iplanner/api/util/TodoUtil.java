package ibox.iplanner.api.util;

import ibox.iplanner.api.model.Todo;
import ibox.iplanner.api.model.TodoStatus;
import ibox.iplanner.api.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TodoUtil extends BaseEntityUtil {

    public static TodoStatus anyTodoStatus() {
        return Arrays.asList(new TodoStatus[] {
                TodoStatus.OPEN,
                TodoStatus.CLOSED,
                TodoStatus.FINISHED
        }).get(new Random().nextInt(3));
    }

    public static User anyTodoCreator() {
        return anyUser();
    }

    public static Todo anyTodo() {
        return Todo.fromActivity(ActivityUtil.anyActivity());
    }

    public static Todo anyMeetingTodo() {
        return Todo.fromActivity(MeetingUtil.anyMeeting());
    }

    public static Todo anyTaskTodo() {
        return Todo.fromActivity(TaskUtil.anyTask());
    }

    public static List<Todo> anyTodoList() {
        int size = new Random().nextInt(10);
        List<Todo> todoList = new ArrayList<>();
        int i = 0;
        while (i < size) {
            todoList.add(anyTodo());
            i ++;
        }
        return todoList;
    }
}

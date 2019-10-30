package ibox.iplanner.api.util;

import ibox.iplanner.api.model.Todo;
import ibox.iplanner.api.model.TodoStatus;
import ibox.iplanner.api.model.User;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.*;

public class TodoUtil extends BaseEntityUtil {

    public static String anyTodoLocation() {
        return RandomStringUtils.random(20, true, false);
    }

    public static Boolean anyTodoEndTimeUnspecified() {
        return randomBoolean();
    }

    public static Set<String> anyTodoRecurrence() {
        int size = new Random().nextInt(10);
        Set<String> recurrence = new HashSet<>();
        int i = 0;
        while (i < size) {
            recurrence.add(RandomStringUtils.random(10, true, false));
            i ++;
        }
        return recurrence;
    }

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
        Todo todo = anyTodoWithoutId();
        todo.setId(anyShortId());
        return todo;
    }

    public static Todo anyTodoWithoutId() {
        Todo todo = new Todo();
        todo.setSummary(anySummary());
        todo.setDescription(anyDescription());
        todo.setStatus(anyTodoStatus().name());
        todo.setSummary(anySummary());
        todo.setActivity(anyActivityId());
        todo.setLocation(anyTodoLocation());
        todo.setRecurrence(anyTodoRecurrence());
        todo.setEndTimeUnspecified(anyTodoEndTimeUnspecified());
        todo.setCreated(anyCreatedTime());
        todo.setUpdated(anyUpdatedTime());
        todo.setStart(anyStartTime());
        todo.setEnd(anyEndTime());
        todo.setCreator(anyTodoCreator());

        return todo;
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

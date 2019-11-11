package ibox.iplanner.api.model;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Task extends Activity {

    public static final String TASK_TYPE = "task";

    public Task() {
        super();
        addAttribute(new TagAttribute());
        addAttribute(new TimelineAttribute());
    }
}

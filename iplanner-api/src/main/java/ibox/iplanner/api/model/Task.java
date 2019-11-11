package ibox.iplanner.api.model;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Task extends Activity {

    public Task() {
        super();
        addAttribute(new TimelineAttribute());
    }
}

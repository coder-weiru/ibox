package ibox.iplanner.api.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Meeting extends Activity {

    public static final String MEETING_TYPE = "meeting";

    public Meeting() {
        super();
        addAttribute(new TagAttribute());
        addAttribute(new EventAttribute());
        addAttribute(new LocationAttribute());
    }
}

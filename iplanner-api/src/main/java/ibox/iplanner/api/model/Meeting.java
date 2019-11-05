package ibox.iplanner.api.model;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Meeting extends Activity {

    private Frequency frequency;

    @Builder(builderMethodName = "meetingBuilder")
    public Meeting(String id, String title, String description, String type, User creator, Instant created, Instant updated, String status, Frequency frequency) {
        super(id, title, description, type, creator, created, updated, status);
        this.frequency = frequency;
    }
}

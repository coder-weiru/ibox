package ibox.iplanner.api.model;


import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Task extends Activity {

    @NotNull(message = "Task deadline {javax.validation.constraints.NotNull.message}")
    private Instant deadline;

    @Builder(builderMethodName = "taskBuilder")
    public Task(String id, String title, String description, String type, User creator, Instant created, Instant updated, String status, Instant deadline) {
        super(id, title, description, type, creator, created, updated, status);
        this.deadline = deadline;
    }
}

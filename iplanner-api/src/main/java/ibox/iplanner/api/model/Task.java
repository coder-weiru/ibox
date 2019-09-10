package ibox.iplanner.api.model;


import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task extends Activity {

    @NotNull(message = "Task deadline {javax.validation.constraints.NotNull.message}")
    private Instant deadline;

}

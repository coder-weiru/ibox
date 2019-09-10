package ibox.iplanner.api.model.template;


import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskTemplate extends BasicActivityTemplate {

    @NotNull(message = "Task deadline {javax.validation.constraints.NotNull.message}")
    private Instant deadline;

}

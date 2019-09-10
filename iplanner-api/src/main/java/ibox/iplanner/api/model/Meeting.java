package ibox.iplanner.api.model;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Meeting extends Activity {

    @NotNull(message = "Meeting frequency {javax.validation.constraints.NotNull.message}")
    private Frequency frequency;

}

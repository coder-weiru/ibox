package ibox.iplanner.api.model.template;

import ibox.iplanner.api.model.Frequency;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingTemplate extends BasicActivityTemplate {

    @NotNull(message = "Meeting frequency {javax.validation.constraints.NotNull.message}")
    private Frequency frequency;

}

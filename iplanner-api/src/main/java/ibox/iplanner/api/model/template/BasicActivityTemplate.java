package ibox.iplanner.api.model.template;

import com.fasterxml.jackson.annotation.JsonFormat;
import ibox.iplanner.api.model.User;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BasicActivityTemplate {

    private String id;
    @NotNull(message = "Title {javax.validation.constraints.NotNull.message}")
    private String title;
    private String type;
    private String description;

    @Valid
    @NotNull(message = "Creator {javax.validation.constraints.NotNull.message}")
    private User creator;
    @NotNull(message = "Created Time {javax.validation.constraints.NotNull.message}")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant created;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant updated;
    private String status;

}



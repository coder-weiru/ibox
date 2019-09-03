package ibox.iplanner.api.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@JsonAutoDetect
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiError implements Serializable {
    private static final long serialVersionUID = 679382664556289406L;

    @NotNull(message = "Status {javax.validation.constraints.NotNull.message}")
    private int status;

    @NotNull(message = "Error Code {javax.validation.constraints.NotNull.message}")
    private String error;

    private String message;

    private Instant timestamp;

    private List<String> errorDetails = new ArrayList<>();

}

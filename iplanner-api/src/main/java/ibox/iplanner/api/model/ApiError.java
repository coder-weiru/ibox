package ibox.iplanner.api.model;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ApiError {

    @NotNull(message = "Status {javax.validation.constraints.NotNull.message}")
    private String status;

    @NotNull(message = "Error Code {javax.validation.constraints.NotNull.message}")
    private String error;

    private String message;

    private Instant timestamp;

    private List<String> errorDetails = new ArrayList<>();

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getErrorDetails() {
        return errorDetails;
    }

    public void setErrorDetails(List<String> errorDetails) {
        this.errorDetails = errorDetails;
    }

    @Override
    public String toString() {
        return "Error{" +
                "status='" + status + '\'' +
                ", error='" + error + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", errorDetails=" + errorDetails +
                '}';
    }
}

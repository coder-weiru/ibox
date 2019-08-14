package ibox.iplanner.api.model;

import javax.validation.constraints.NotNull;

public class Error {

    @NotNull(message = "Error Code {javax.validation.constraints.NotNull.message}")
    private String code;
    @NotNull(message = "Error Message {javax.validation.constraints.NotNull.message}")
    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Error{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}

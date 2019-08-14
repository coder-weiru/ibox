package ibox.iplanner.api.model;

import javax.validation.constraints.NotNull;

public class User {

    @NotNull(message = "User ID {javax.validation.constraints.NotNull.message}")
    private String id;
    @NotNull(message = "User Email {javax.validation.constraints.NotNull.message}")
    private String email;
    @NotNull(message = "Display Name {javax.validation.constraints.NotNull.message}")
    private String displayName;
    @NotNull(message = "self flag {javax.validation.constraints.NotNull.message}")
    private Boolean self;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Boolean getSelf() {
        return self;
    }

    public void setSelf(Boolean self) {
        this.self = self;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", displayName='" + displayName + '\'' +
                ", self=" + self +
                '}';
    }
}

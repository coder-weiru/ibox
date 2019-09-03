package ibox.iplanner.api.model;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @NotNull(message = "User ID {javax.validation.constraints.NotNull.message}")
    private String id;
    @NotNull(message = "User Email {javax.validation.constraints.NotNull.message}")
    private String email;
    @NotNull(message = "Display Name {javax.validation.constraints.NotNull.message}")
    private String displayName;
    @NotNull(message = "self flag {javax.validation.constraints.NotNull.message}")
    private Boolean self;
}

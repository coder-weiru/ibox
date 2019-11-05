package ibox.iplanner.api.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    private String id;
    private String email;
    private String displayName;
    private Boolean self;
}

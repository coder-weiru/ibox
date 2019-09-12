package ibox.iplanner.api.model.updatable;


import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Updatable {

    @NotNull
    private String objectType;

    @NotNull
    @Valid
    private UpdatableKey primaryKey;

    @NotNull
    @Valid
    private Set<UpdatableAttribute> updatableAttributes;

}

package ibox.iplanner.api.model.updatable;


import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Updatable {

    @NotBlank
    private String objectType;

    @NotNull
    @Valid
    private UpdatableKey primaryKey;

    @NotNull
    @NotEmpty
    @Valid
    private Set<UpdatableAttribute> updatableAttributes;

}

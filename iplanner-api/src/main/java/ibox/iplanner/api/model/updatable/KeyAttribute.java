package ibox.iplanner.api.model.updatable;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class KeyAttribute {

    @NotNull
    private String attributeName;
    @NotNull
    private Object value;

}

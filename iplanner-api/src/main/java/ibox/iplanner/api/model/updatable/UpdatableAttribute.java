package ibox.iplanner.api.model.updatable;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class UpdatableAttribute {

    @NotBlank
    private String attributeName;
    @NotNull
    private UpdateAction action;
    private Set<Object> attributeValues;
    private Object value;

}

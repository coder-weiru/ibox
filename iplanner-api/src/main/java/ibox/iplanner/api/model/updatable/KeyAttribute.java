package ibox.iplanner.api.model.updatable;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class KeyAttribute {

    @NotBlank
    private String attributeName;
    private Object value;

}

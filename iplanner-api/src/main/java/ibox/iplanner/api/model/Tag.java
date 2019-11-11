package ibox.iplanner.api.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class Tag {

    private String value;
    private String rgbHexCode;
}

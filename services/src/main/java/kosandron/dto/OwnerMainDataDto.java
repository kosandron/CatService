package kosandron.dto;

import kosandron.entities.Owner;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class OwnerMainDataDto {
    private Long id;
    private String name;
    @EqualsAndHashCode.Exclude
    private LocalDate birthday;

    public static OwnerMainDataDto fromEntity(Owner owner) {
        if (owner == null) {
            return null;
        }

        return OwnerMainDataDto.builder()
                .id(owner.getId())
                .name(owner.getName())
                .birthday(owner.getBirthday())
                .build();
    }
}

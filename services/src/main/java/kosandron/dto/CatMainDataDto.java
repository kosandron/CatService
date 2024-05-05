package kosandron.dto;

import kosandron.entities.Cat;
import kosandron.enums.Color;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class CatMainDataDto {
    private Long id;
    private String name;
    @EqualsAndHashCode.Exclude
    private LocalDate birthday;
    private String breed;
    private Color color;
    private Long ownerId;

    public static CatMainDataDto fromEntity(Cat cat) {
        if (cat == null) {
            return null;
        }

        return CatMainDataDto.builder()
                .id(cat.getId())
                .name(cat.getName())
                .birthday(cat.getBirthday())
                .breed(cat.getBreed())
                .color(cat.getColor())
                .ownerId(cat.getOwner().getId())
                .build();
    }
}

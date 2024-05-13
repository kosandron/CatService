package org.kosandron.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import org.kosandron.enums.Color;
import org.kosandron.validation.ValueOfEnum;

import java.time.LocalDate;

public record InputCatData(@NotBlank(message = "Empty name!") String name,
                           @PastOrPresent(message = "Date cannot be in future!") LocalDate birthDate,
                           @NotBlank(message = "Empty breed!") String breed,
                           @NotBlank(message = "Empty color!") @ValueOfEnum(enumClass = Color.class) String color,
                           Long ownerId) {
}

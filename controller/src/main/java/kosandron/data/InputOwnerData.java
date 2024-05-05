package kosandron.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record InputOwnerData(@NotBlank(message = "Name cannot be empty!") String name,
                             @PastOrPresent(message = "Date cannot be in future!") LocalDate birthDate,
                                @NotBlank(message = "Name cannot be empty!") String login,
                             @NotBlank(message = "Name cannot be empty!") String password,
                             @NotBlank(message = "Name cannot be empty!") String roles
                             ) {}

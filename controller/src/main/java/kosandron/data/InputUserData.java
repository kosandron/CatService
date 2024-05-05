package kosandron.data;

import jakarta.validation.constraints.NotBlank;

public record InputUserData(
        @NotBlank(message = "login cannot be empty!") String login,
        @NotBlank(message = "password cannot be empty!") String password,
        Long catOwnerId,
        @NotBlank(message = "Roles cannot be empty!") String roles
) {
}

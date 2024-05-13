package org.kosandron.dto;

import lombok.Builder;
import lombok.Data;
import org.kosandron.entities.User;
import org.kosandron.enums.UserRole;

import java.util.Optional;
import java.util.Set;

@Data
@Builder
public class UserDto {
    private Long id;
    private String login;
    private String password;
    private Set<UserRole> roles;
    private Long catOwnerId;

    public static UserDto fromUser(Optional<User> optionalUser) {
        if (optionalUser.isEmpty()) {
            return null;
        }
        User user = optionalUser.get();
        return UserDto.builder()
                .id(user.getId())
                .login(user.getLogin())
                .password(user.getPassword())
                .catOwnerId(user.getCatOwnerId())
                .roles(user.getRoles())
                .build();
    }
}

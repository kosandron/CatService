package org.kosandron.security;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.kosandron.dao.UserDao;
import org.kosandron.dto.UserDto;
import org.kosandron.entities.User;
import org.kosandron.enums.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    public UserDto getUserByLogin(String login) {
        return UserDto.fromUser(userDao.findByLogin(login));
    }

    public UserDto add(String login, String password, Long catOwnerId, String roles) {
        Set<UserRole> roleSet = Arrays.stream(roles.split(", "))
                .map(word -> UserRole.valueOf(word))
                .collect(Collectors.toSet());
        User user = new User(login, passwordEncoder.encode(password), catOwnerId, roleSet);
        userDao.save(user);
        return UserDto.fromUser(Optional.of(user));
    }

    @Transactional
    public void removeByOwnerId(Long id) {
        userDao.deleteByCatOwnerId(id);
    }
  //  getOw
}

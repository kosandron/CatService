package kosandron.services.security;

import jakarta.transaction.Transactional;
import kosandron.dao.UserDao;
import kosandron.dto.UserDto;
import kosandron.entities.User;
import kosandron.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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

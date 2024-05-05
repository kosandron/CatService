package kosandron.services.security;

import kosandron.entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class MyUserDetails implements UserDetails {
    private String login;
    private String password;
    private Collection<CustomGrantedAuthority> roles;
    private Long catOwnerId;

    public MyUserDetails(User user) {
        this.password = user.getPassword();
        this.login = user.getLogin();
        this.roles = user.getRoles().stream()
                .map(CustomGrantedAuthority::new)
                .collect(Collectors.toList());
        this.catOwnerId = user.getCatOwnerId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Long getCatOwnerId() {
        return catOwnerId;
    }
}

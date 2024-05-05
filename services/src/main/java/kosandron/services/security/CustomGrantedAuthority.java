package kosandron.services.security;

import kosandron.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
public class CustomGrantedAuthority implements GrantedAuthority {

    private final static String PREFIX = "ROLE_";
    private final UserRole userRole;

    @Override
    public String getAuthority() {
        return PREFIX + userRole.name();
    }
}

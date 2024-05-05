package kosandron;

import kosandron.controllers.CatControllerImpl;
import kosandron.controllers.OwnerControllerImpl;
import kosandron.data.InputCatData;
import kosandron.dto.CatMainDataDto;
import kosandron.dto.UserDto;
import kosandron.entities.User;
import kosandron.enums.Color;
import kosandron.enums.UserRole;
import kosandron.exceptions.OtherOwnerDataException;
import kosandron.services.jpaservices.JpaCatService;
import kosandron.services.jpaservices.JpaOwnerService;
import kosandron.services.security.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AdminLogicTests /*extends IntegrationTest*/ {
    @Mock
    private Principal principal;

    private UserService userService;

    private JpaOwnerService ownerService;

    private JpaCatService catService;

    private OwnerControllerImpl ownerController;

    private CatControllerImpl catController;

    @BeforeEach
    public void setup() {
        catService = mock(JpaCatService.class);
        userService = mock(UserService.class);
        ownerService = mock(JpaOwnerService.class);
        catController = new CatControllerImpl(catService, userService);
        ownerController = new OwnerControllerImpl(ownerService, userService);
        MockitoAnnotations.openMocks(this);
    }

    @BeforeEach
    public void init() {
        //ownerService.create("Roman", LocalDate.now());
        User user = new User("user", "user", 1L, Set.of(UserRole.ADMIN));
        //userService.add("user", "user", 1L, "ROLE_ADMIN");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken(user, null));
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void gutCreateCatAnotherUserTest() {
        // Arrange
        User user = new User("user", "user", 1L, Set.of(UserRole.ADMIN));
        when(userService.getUserByLogin(anyString())).thenReturn(UserDto.fromUser(Optional.of(user)));
        when(catService.create("Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, 3L)).thenReturn(1L);
        when(principal.getName()).thenReturn("user");

        // Act, Assert
        assertDoesNotThrow(() -> catController.create(
                new InputCatData("Motik", LocalDate.of(1, 1, 1), "sfinks", "GRAY", 3L), principal));
    }

    @Test
    public void gutRemoveCatAnotherUserTest() {
        // Arrange
        User user = new User("user", "user", 1L, Set.of(UserRole.ADMIN));
        when(userService.getUserByLogin(anyString())).thenReturn(UserDto.fromUser(Optional.of(user)));
        when(catService.getById(1L)).thenReturn(new CatMainDataDto(1L, "Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, 3L));
        when(principal.getName()).thenReturn("user");

        // Act, Assert
        assertDoesNotThrow( () -> catController.removeById(1L, principal));
    }

    @Test
    public void badMakeFriendsAnotherUserTest() {
        // Arrange
        User user = new User("user", "user", 1L, Set.of(UserRole.ADMIN));
        when(userService.getUserByLogin(anyString())).thenReturn(UserDto.fromUser(Optional.of(user)));
        when(catService.getById(1L)).thenReturn(new CatMainDataDto(1L, "Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, 3L));
        when(catService.getById(2L)).thenReturn(new CatMainDataDto(2L, "Motik", LocalDate.of(1, 1, 1), "sfinks", Color.BROWN, 3L));
        when(principal.getName()).thenReturn("user");

        // Act, Assert
        assertDoesNotThrow(() -> catController.makeFriend(1L, 2L, principal));
    }

    @Test
    public void gutRemoveFriendsAnotherUserTest() {
        // Arrange
        User user = new User("user", "user", 1L, Set.of(UserRole.ADMIN));
        when(userService.getUserByLogin(anyString())).thenReturn(UserDto.fromUser(Optional.of(user)));
        when(catService.getById(1L)).thenReturn(new CatMainDataDto(1L, "Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, 3L));
        when(catService.getById(2L)).thenReturn(new CatMainDataDto(2L, "Motik", LocalDate.of(1, 1, 1), "sfinks", Color.BROWN, 3L));
        when(principal.getName()).thenReturn("user");

        // Act, Assert
        assertDoesNotThrow(() -> catController.removeFriend(1L, 2L, principal));
    }

    @Test
    public void filterFriendsTest() {
        // Arrange
        User user = new User("user", "user", 1L, Set.of(UserRole.ADMIN));
        when(userService.getUserByLogin(anyString())).thenReturn(UserDto.fromUser(Optional.of(user)));
        when(catService.getById(1L)).thenReturn(new CatMainDataDto(1L, "Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, 1L));
        CatMainDataDto friend1 = new CatMainDataDto(2L, "Otos", LocalDate.of(1, 1, 1), "sfinks", Color.BROWN, 3L);
        CatMainDataDto friend2 = new CatMainDataDto(3L, "Kitik", LocalDate.of(1, 1, 1), "sfinks", Color.BROWN, 1L);
        CatMainDataDto friend3 = new CatMainDataDto(4L, "Barsik", LocalDate.of(1, 1, 1), "sfinks", Color.BROWN, 1L);
        CatMainDataDto friend4 = new CatMainDataDto(5L, "Ghora", LocalDate.of(1, 1, 1), "sfinks", Color.BROWN, 2L);
        when(catService.getFriends(1L))
                .thenReturn(List.of(
                        friend1, friend2, friend3, friend4
                ));

        when(principal.getName()).thenReturn("user");

        // Act
        List<CatMainDataDto> friends = catController.getFriends(1L, principal);

        // Assert
        assertThat(friends.size()).isEqualTo(4);
        assertThat(friends).contains(friend1);
        assertThat(friends).contains(friend2);
        assertThat(friends).contains(friend3);
        assertThat(friends).contains(friend4);
    }

    @Test
    public void gutGetCatsRequest() {
        // Arrange
        User user = new User("user", "user", 1L, Set.of(UserRole.ADMIN));
        when(userService.getUserByLogin(anyString())).thenReturn(UserDto.fromUser(Optional.of(user)));
        when(principal.getName()).thenReturn("user");
        when(ownerService.getCatsListByOwnerId(anyLong())).thenReturn(new ArrayList<>());

        // Act, Assert
        assertDoesNotThrow(() -> ownerController.getCats(2L , principal));
    }
}

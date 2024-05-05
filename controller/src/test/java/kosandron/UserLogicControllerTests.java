package kosandron;

import kosandron.controllers.CatControllerImpl;
import kosandron.controllers.OwnerControllerImpl;
import kosandron.data.InputCatData;
import kosandron.dto.CatMainDataDto;
import kosandron.dto.OwnerMainDataDto;
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
import org.springframework.boot.test.context.SpringBootTest;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserLogicControllerTests  {

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
        User user = new User("user", "user", 1L, Set.of(UserRole.USER));
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken(user, null));
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void badCreateAnotherUserTest() {
        // Arrange
        User user = new User("user", "user", 1L, Set.of(UserRole.USER));
        when(userService.getUserByLogin(anyString())).thenReturn(UserDto.fromUser(Optional.of(user)));
        when(principal.getName()).thenReturn("user");

        // Act, Assert
        assertThrows(OtherOwnerDataException.class, () -> catController.create(
                new InputCatData("Motik", LocalDate.of(1, 1, 1), "sfinks", "GRAY", 3L), principal));
    }

    @Test
    public void badRemoveAnotherUserTest() {
        // Arrange
        User user = new User("user", "user", 1L, Set.of(UserRole.USER));
        when(userService.getUserByLogin(anyString())).thenReturn(UserDto.fromUser(Optional.of(user)));
        when(catService.getById(1L)).thenReturn(new CatMainDataDto(1L, "Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, 3L));
        when(principal.getName()).thenReturn("user");

        // Act, Assert
        assertThrows(OtherOwnerDataException.class, () -> catController.removeById(1L, principal));
    }

    @Test
    public void badMakeFriendsAnotherUserTest() {
        // Arrange
        User user = new User("user", "user", 1L, Set.of(UserRole.USER));
        when(userService.getUserByLogin(anyString())).thenReturn(UserDto.fromUser(Optional.of(user)));
        when(catService.getById(1L)).thenReturn(new CatMainDataDto(1L, "Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, 3L));
        when(catService.getById(2L)).thenReturn(new CatMainDataDto(2L, "Motik", LocalDate.of(1, 1, 1), "sfinks", Color.BROWN, 3L));

        when(principal.getName()).thenReturn("user");

        // Act, Assert
        assertThrows(OtherOwnerDataException.class, () -> catController.makeFriend(1L, 2L, principal));
    }

    @Test
    public void badRemoveFriendsAnotherUserTest() {
        // Arrange
        User user = new User("user", "user", 1L, Set.of(UserRole.USER));
        when(userService.getUserByLogin(anyString())).thenReturn(UserDto.fromUser(Optional.of(user)));
        when(catService.getById(1L)).thenReturn(new CatMainDataDto(1L, "Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, 3L));
        when(catService.getById(2L)).thenReturn(new CatMainDataDto(2L, "Motik", LocalDate.of(1, 1, 1), "sfinks", Color.BROWN, 3L));

        when(principal.getName()).thenReturn("user");

        // Act, Assert
        assertThrows(OtherOwnerDataException.class, () -> catController.removeFriend(1L, 2L, principal));
    }

    @Test
    public void filterFriendsTest() {
        // Arrange
        User user = new User("user", "user", 1L, Set.of(UserRole.USER));
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


        List<CatMainDataDto> friends = catController.getFriends(1L, principal);

        // Act, Assert
        assertThat(friends.size()).isEqualTo(2);
        assertThat(friends).contains(friend2);
        assertThat(friends).contains(friend3);
    }

    @Test
    public void badCatsRequest() {
        // Arrange
        User user = new User("user", "user", 1L, Set.of(UserRole.USER));
        when(userService.getUserByLogin(anyString())).thenReturn(UserDto.fromUser(Optional.of(user)));
        //when(userDao.findByLogin(anyString())).thenReturn(Optional.of(user));
        when(principal.getName()).thenReturn("user");

        // Act, Assert
        assertThrows(OtherOwnerDataException.class, () -> ownerController.getCats(2L , principal));
    }




    @Test
    public void gutCreateAnotherUserTest() {
        // Arrange
        User user = new User("user", "user", 1L, Set.of(UserRole.USER));
        when(userService.getUserByLogin(anyString())).thenReturn(UserDto.fromUser(Optional.of(user)));
        when(principal.getName()).thenReturn("user");
        when(catService.create("Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, 1L)).thenReturn(1L);

        // Act, Assert
        assertDoesNotThrow(() -> catController.create(
                new InputCatData("Motik", LocalDate.of(1, 1, 1), "sfinks", "GRAY", 1L), principal));
    }

    @Test
    public void gutRemoveAnotherUserTest() {
        // Arrange
        User user = new User("user", "user", 1L, Set.of(UserRole.USER));
        when(userService.getUserByLogin(anyString())).thenReturn(UserDto.fromUser(Optional.of(user)));
        when(catService.getById(1L)).thenReturn(new CatMainDataDto(1L, "Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, 1L));
        when(principal.getName()).thenReturn("user");

        // Act, Assert
        assertDoesNotThrow(() -> catController.removeById(1L, principal));
    }

    @Test
    public void gutMakeFriendsAnotherUserTest() {
        // Arrange
        User user = new User("user", "user", 1L, Set.of(UserRole.USER));
        when(userService.getUserByLogin(anyString())).thenReturn(UserDto.fromUser(Optional.of(user)));
        when(catService.getById(1L)).thenReturn(new CatMainDataDto(1L, "Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, 1L));
        when(catService.getById(2L)).thenReturn(new CatMainDataDto(2L, "Motik", LocalDate.of(1, 1, 1), "sfinks", Color.BROWN, 3L));

        when(principal.getName()).thenReturn("user");

        // Act, Assert
        assertDoesNotThrow(() -> catController.makeFriend(1L, 2L, principal));
    }

    @Test
    public void gutRemoveFriendsAnotherUserTest() {
        // Arrange
        User user = new User("user", "user", 1L, Set.of(UserRole.USER));
        when(userService.getUserByLogin(anyString())).thenReturn(UserDto.fromUser(Optional.of(user)));
        when(catService.getById(1L)).thenReturn(new CatMainDataDto(1L, "Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, 1L));
        when(catService.getById(2L)).thenReturn(new CatMainDataDto(2L, "Motik", LocalDate.of(1, 1, 1), "sfinks", Color.BROWN, 3L));

        when(principal.getName()).thenReturn("user");

        // Act, Assert
        assertDoesNotThrow(() -> catController.removeFriend(1L, 2L, principal));
    }

    @Test
    public void gutCatsRequest() {
        // Arrange
        User user = new User("user", "user", 1L, Set.of(UserRole.USER));
        when(userService.getUserByLogin(anyString())).thenReturn(UserDto.fromUser(Optional.of(user)));
        //when(userDao.findByLogin(anyString())).thenReturn(Optional.of(user));
        when(principal.getName()).thenReturn("user");

        // Act, Assert
        assertDoesNotThrow(() -> ownerController.getCats(1L , principal));
    }
}

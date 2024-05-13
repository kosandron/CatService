package kosandron;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kosandron.controllers.CatControllerImpl;
import org.kosandron.controllers.OwnerControllerImpl;
import org.kosandron.data.InputCatData;
import org.kosandron.dto.CatMainDataDto;
import org.kosandron.dto.UserDto;
import org.kosandron.entities.User;
import org.kosandron.enums.Color;
import org.kosandron.enums.UserRole;
import org.kosandron.exceptions.OtherOwnerDataException;
import org.kosandron.kafka.CatKafkaProducer;
import org.kosandron.kafka.OwnerKafkaProducer;
import org.kosandron.security.UserService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserLogicControllerTests  {

    @Mock
    private Principal principal;

    private UserService userService;

    private OwnerKafkaProducer ownerKafkaProducer;
    private CatKafkaProducer catKafkaProducer;

    private OwnerControllerImpl ownerController;

    private CatControllerImpl catController;

    private ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @BeforeEach
    public void setup() {
        userService = mock(UserService.class);
        ownerKafkaProducer = mock(OwnerKafkaProducer.class);
        catKafkaProducer = mock(CatKafkaProducer.class);
        catController = new CatControllerImpl(userService, catKafkaProducer, ownerKafkaProducer);
        ownerController = new OwnerControllerImpl(userService, ownerKafkaProducer);
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
    public void badCreateAnotherUserTest() throws Exception {
        // Arrange
        User user = new User("user", "user", 1L, Set.of(UserRole.USER));
        when(userService.getUserByLogin(anyString())).thenReturn(UserDto.fromUser(Optional.of(user)));
        when(principal.getName()).thenReturn("user");
        when(catKafkaProducer.kafkaRequestReply(anyString(), any())).thenReturn(
                mapper.writeValueAsString(new CatMainDataDto(1L, "Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, 3L)));


        // Act, Assert
        assertThrows(OtherOwnerDataException.class, () -> catController.create(
                new InputCatData("Motik", LocalDate.of(1, 1, 1), "sfinks", "GRAY", 3L), principal));
    }

    @Test
    public void badRemoveAnotherUserTest() throws Exception {
        // Arrange
        User user = new User("user", "user", 1L, Set.of(UserRole.USER));
        when(userService.getUserByLogin(anyString())).thenReturn(UserDto.fromUser(Optional.of(user)));
      //  when(catService.getById(1L)).thenReturn(new CatMainDataDto(1L, "Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, 3L));
        when(principal.getName()).thenReturn("user");
        when(catKafkaProducer.kafkaRequestReply(anyString(), any())).thenReturn(
                mapper.writeValueAsString(new CatMainDataDto(2L, "Otos", LocalDate.of(1, 1, 1), "sfinks", Color.BROWN, 3L)));
        when(ownerKafkaProducer.kafkaRequestReply(anyString(), any())).thenReturn(
                mapper.writeValueAsString(""));

        // Act, Assert
        assertThrows(OtherOwnerDataException.class, () -> catController.removeById(1L, principal));
    }

    @Test
    public void badMakeFriendsAnotherUserTest() throws Exception {
        // Arrange
        User user = new User("user", "user", 1L, Set.of(UserRole.USER));
        when(userService.getUserByLogin(anyString())).thenReturn(UserDto.fromUser(Optional.of(user)));
        when(catKafkaProducer.kafkaRequestReply(anyString(), any())).thenReturn(
                mapper.writeValueAsString(new CatMainDataDto(2L, "Otos", LocalDate.of(1, 1, 1), "sfinks", Color.BROWN, 3L)));

        when(principal.getName()).thenReturn("user");

        // Act, Assert
        assertThrows(OtherOwnerDataException.class, () -> catController.makeFriend(1L, 2L, principal));
    }

    @Test
    public void badRemoveFriendsAnotherUserTest() throws Exception {
        // Arrange
        User user = new User("user", "user", 1L, Set.of(UserRole.USER));
        when(userService.getUserByLogin(anyString())).thenReturn(UserDto.fromUser(Optional.of(user)));
        when(catKafkaProducer.kafkaRequestReply(anyString(), any())).thenReturn(
                mapper.writeValueAsString(new CatMainDataDto(2L, "Otos", LocalDate.of(1, 1, 1), "sfinks", Color.BROWN, 3L)));

        when(principal.getName()).thenReturn("user");

        // Act, Assert
        assertThrows(OtherOwnerDataException.class, () -> catController.removeFriend(1L, 2L, principal));
    }

    @Test
    public void filterFriendsTest() throws Exception {
        // Arrange
        User user = new User("user", "user", 1L, Set.of(UserRole.USER));
        when(userService.getUserByLogin(anyString())).thenReturn(UserDto.fromUser(Optional.of(user)));
        CatMainDataDto friend1 = new CatMainDataDto(2L, "Otos", LocalDate.of(1, 1, 1), "sfinks", Color.BROWN, 3L);
        CatMainDataDto friend2 = new CatMainDataDto(3L, "Kitik", LocalDate.of(1, 1, 1), "sfinks", Color.BROWN, 1L);
        CatMainDataDto friend3 = new CatMainDataDto(4L, "Barsik", LocalDate.of(1, 1, 1), "sfinks", Color.BROWN, 1L);
        CatMainDataDto friend4 = new CatMainDataDto(5L, "Ghora", LocalDate.of(1, 1, 1), "sfinks", Color.BROWN, 2L);
        when(catKafkaProducer.kafkaRequestReply(anyString(), any())).thenReturn(
                mapper.writeValueAsString(List.of(friend1, friend2, friend3, friend4)));


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
    public void gutCreateAnotherUserTest() throws Exception {
        // Arrange
        User user = new User("user", "user", 1L, Set.of(UserRole.USER));
        when(userService.getUserByLogin(anyString())).thenReturn(UserDto.fromUser(Optional.of(user)));
        when(principal.getName()).thenReturn("user");
        when(catKafkaProducer.kafkaRequestReply(anyString(), any())).thenReturn(
                mapper.writeValueAsString(new CatMainDataDto(1L, "Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, 3L)));

        // Act, Assert
        assertDoesNotThrow(() -> catController.create(
                new InputCatData("Motik", LocalDate.of(1, 1, 1), "sfinks", "GRAY", 1L), principal));
    }

    @Test
    public void gutRemoveAnotherUserTest() throws Exception {
        // Arrange
        User user = new User("user", "user", 1L, Set.of(UserRole.USER));
        when(userService.getUserByLogin(anyString())).thenReturn(UserDto.fromUser(Optional.of(user)));
        when(catKafkaProducer.kafkaRequestReply(anyString(), any())).thenReturn(
                mapper.writeValueAsString(new CatMainDataDto(2L, "Otos", LocalDate.of(1, 1, 1), "sfinks", Color.BROWN, 1L)));
        when(principal.getName()).thenReturn("user");

        // Act, Assert
        assertDoesNotThrow(() -> catController.removeById(1L, principal));
    }

    @Test
    public void gutMakeFriendsAnotherUserTest() throws Exception {
        // Arrange
        User user = new User("user", "user", 1L, Set.of(UserRole.USER));
        when(userService.getUserByLogin(anyString())).thenReturn(UserDto.fromUser(Optional.of(user)));
        when(catKafkaProducer.kafkaRequestReply(anyString(), any())).thenReturn(
                mapper.writeValueAsString(new CatMainDataDto(2L, "Otos", LocalDate.of(1, 1, 1), "sfinks", Color.BROWN, 1L)));

        when(principal.getName()).thenReturn("user");

        // Act, Assert
        assertDoesNotThrow(() -> catController.makeFriend(1L, 2L, principal));
    }

    @Test
    public void gutRemoveFriendsAnotherUserTest() throws Exception {
        // Arrange
        User user = new User("user", "user", 1L, Set.of(UserRole.USER));
        when(userService.getUserByLogin(anyString())).thenReturn(UserDto.fromUser(Optional.of(user)));
        when(catKafkaProducer.kafkaRequestReply(anyString(), any())).thenReturn(
                mapper.writeValueAsString(new CatMainDataDto(2L, "Otos", LocalDate.of(1, 1, 1), "sfinks", Color.BROWN, 1L)));

        when(principal.getName()).thenReturn("user");

        // Act, Assert
        assertDoesNotThrow(() -> catController.removeFriend(1L, 2L, principal));
    }

    @Test
    public void gutCatsRequest() throws Exception {
        // Arrange
        User user = new User("user", "user", 1L, Set.of(UserRole.USER));
        when(userService.getUserByLogin(anyString())).thenReturn(UserDto.fromUser(Optional.of(user)));
        //when(userDao.findByLogin(anyString())).thenReturn(Optional.of(user));
        when(principal.getName()).thenReturn("user");
        when(ownerKafkaProducer.kafkaRequestReply(anyString(), any())).thenReturn(
                mapper.writeValueAsString(new ArrayList<>()));

        // Act, Assert
        assertDoesNotThrow(() -> ownerController.getCats(1L , principal));
    }
}

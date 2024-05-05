package kosandron;

import kosandron.dao.CatDao;
import kosandron.dao.OwnerDao;
import kosandron.dto.CatMainDataDto;
import kosandron.enums.Color;
import kosandron.exceptions.NotFoundException;
import kosandron.services.CatService;
import kosandron.services.OwnerService;
import kosandron.services.jpaservices.JpaCatService;
import kosandron.services.jpaservices.JpaOwnerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {JpaCatService.class, JpaOwnerService.class, CatDao.class, OwnerDao.class})
@EnableAutoConfiguration
public class CatServiceTests extends IntegrationTest {
    @Autowired
    private CatService catService;
    @Autowired
    private OwnerService ownerService;

    @Test
    @Transactional
    @Rollback
    public void getAllEmptyTest() {
        // Act
        List<CatMainDataDto> data = catService.getAll();

        // Assert
        assertThat(data).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    public void createCatWithoutRegisteredOwnerTest() {
        // Act, Assert
        assertThrows(NotFoundException.class, () -> catService.create("Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, 5L));
    }

    @Test
    @Transactional
    @Rollback
    public void createAndGetAllTest() {
        // Arrange
        Long ownerId = ownerService.create("Vasya", LocalDate.of(1, 2, 3));
        Long catId = catService.create("Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, ownerId);
        CatMainDataDto expectedResult = new CatMainDataDto(catId, "Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, ownerId);

        // Act
        List<CatMainDataDto> data = catService.getAll();

        // Assert
        assertThat(data).contains(expectedResult);
    }

    @Test
    @Transactional
    @Rollback
    public void deleteTest() {
        // Arrange
        Long ownerId = ownerService.create("Vasya", LocalDate.of(1, 2, 3));
        Long catId = catService.create("Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, ownerId);
        CatMainDataDto expectedResult = new CatMainDataDto(catId, "Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, ownerId);
        catService.removeById(catId);

        // Act
        List<CatMainDataDto> data = catService.getAll();

        // Assert
        assertThat(data).doesNotContain(expectedResult);
    }

    @Test
    @Transactional
    @Rollback
    public void deleteByNotExistedIdTest() {
        // Act, Assert
        assertThrows(NotFoundException.class, () -> catService.removeById(1L));
    }

    @Test
    @Transactional
    @Rollback
    public void getByExitedIdTest() {
        // Arrange
        Long ownerId = ownerService.create("Vasya", LocalDate.of(1, 2, 3));
        Long catId = catService.create("Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, ownerId);
        CatMainDataDto expectedResult = new CatMainDataDto(catId, "Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, ownerId);

        // Act
        CatMainDataDto data = catService.getById(catId);

        // Assert
        assertThat(data).isEqualTo(expectedResult);
    }

    @Test
    @Transactional
    @Rollback
    public void getByNotExitedIdTest() {
        // Act, Assert
        assertThrows(NotFoundException.class, () -> catService.getById(1L));
    }

    @Test
    @Transactional
    @Rollback
    public void getFriendsEmptyListTest() {
        // Arrange
        Long owid = ownerService.create("Vasya", LocalDate.of(1, 2, 3));
        ownerService.create("Petya", LocalDate.of(11, 7, 4));
        Long id1 = catService.create("Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, owid);
        Long id2 = catService.create("Marsik", LocalDate.of(1, 1, 1), "kashirskiy", Color.ORANGE, owid);

        // Act
        List<CatMainDataDto> firstCatFriends = catService.getFriends(id1);
        List<CatMainDataDto> secondCatFriends = catService.getFriends(id2);

        // Assert
        assertThat(firstCatFriends).isEmpty();
        assertThat(secondCatFriends).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    public void addFriendsTest() {
        // Arrange
        Long ownerId = ownerService.create("Vasya", LocalDate.of(1, 2, 3));
        Long id1 = catService.create("Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, ownerId);
        Long id2 = catService.create("Marsik", LocalDate.of(1, 1, 1), "kashirskiy", Color.ORANGE, ownerId);
        CatMainDataDto firstCatData = new CatMainDataDto(id1, "Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, ownerId);
        CatMainDataDto secondCatData = new CatMainDataDto(id2, "Marsik", LocalDate.of(1, 1, 1), "kashirskiy", Color.ORANGE, ownerId);

        // Act
        catService.makeFriend(id1, id2);

        // Assert
        List<CatMainDataDto> firstCatFriends = catService.getFriends(id1);
        List<CatMainDataDto> secondCatFriends = catService.getFriends(id2);

        assertThat(firstCatFriends).contains(secondCatData);
        assertThat(secondCatFriends).contains(firstCatData);
    }

    @Test
    @Transactional
    @Rollback
    public void addNotExistedFriendTest() {
        // Arrange
        Long ownerId = ownerService.create("Vasya", LocalDate.of(1, 2, 3));
        // Act
        Long id1 = catService.create("Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, ownerId);

        // Assert
        assertThrows(RuntimeException.class,() -> catService.makeFriend(id1, 2L));
    }

    @Test
    @Transactional
    @Rollback
    public void addNotExistedCatsTest() {
        // Act, Assert
        assertThrows(RuntimeException.class,() -> catService.makeFriend(1L, 2L));
    }

    @Test
    @Transactional
    @Rollback
    public void removeFriendsTest() {
        // Arrange
        Long ownerId = ownerService.create("Vasya", LocalDate.of(1, 2, 3));
        Long id1 = catService.create("Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, ownerId);
        Long id2 = catService.create("Marsik", LocalDate.of(1, 1, 1), "kashirskiy", Color.ORANGE, ownerId);
        CatMainDataDto firstCatData = new CatMainDataDto(1L, "Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, ownerId);
        CatMainDataDto secondCatData = new CatMainDataDto(2L, "Marsik", LocalDate.of(1, 1, 1), "kashirskiy", Color.ORANGE, ownerId);

        // Act
        catService.makeFriend(id1, id2);
        catService.removeFriend(id1, id2);

        // Assert
        List<CatMainDataDto> firstCatFriends = catService.getFriends(id1);
        List<CatMainDataDto> secondCatFriends = catService.getFriends(id2);

        assertThat(firstCatFriends).doesNotContain(secondCatData);
        assertThat(secondCatFriends).doesNotContain(firstCatData);
    }

    @Test
    @Transactional
    @Rollback
    public void removeNotExistedFriendTest() {
        // Arrange
        Long ownerId = ownerService.create("Vasya", LocalDate.of(1, 2, 3));
        // Act
        Long id1 = catService.create("Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, ownerId);

        assertThrows(NotFoundException.class,() -> catService.removeFriend(id1, 2L));
    }

    @Test
    @Transactional
    @Rollback
    public void removeNotExistedCatsTest() {
        // Act, Assert
        assertThrows(NotFoundException.class,() -> catService.removeFriend(1L, 2L));
    }

    @Test
    @Transactional
    @Rollback
    public void getFilterAllTest() {
        // Arrange
        Long ownerId = ownerService.create("Vasya", LocalDate.of(1, 2, 3));
        Long id1 = catService.create("Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, ownerId);
        Long id2 = catService.create("Marsik", LocalDate.of(1, 1, 1), "kashirskiy", Color.ORANGE, ownerId);
        CatMainDataDto firstCatData = new CatMainDataDto(1L, "Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, ownerId);
        CatMainDataDto secondCatData = new CatMainDataDto(2L, "Marsik", LocalDate.of(1, 1, 1), "kashirskiy", Color.ORANGE, ownerId);

        // Act
        List<CatMainDataDto> data = catService.getAllWithFilter(Optional.of("GRAY"), Optional.empty(), Optional.empty());

        // Assert
        assertThat(data).contains(firstCatData);
    }
}

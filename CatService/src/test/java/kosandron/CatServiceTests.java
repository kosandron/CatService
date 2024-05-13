package kosandron;

import org.junit.jupiter.api.Test;
import org.kosandron.dao.CatDao;
import org.kosandron.dto.CatMainDataDto;
import org.kosandron.enums.Color;
import org.kosandron.exceptions.NotFoundException;
import org.kosandron.services.CatService;
import org.kosandron.services.JpaCatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {JpaCatService.class, CatDao.class})
@EnableJpaRepositories(basePackages = "org.kosandron")
@EntityScan(basePackages = "org.kosandron")
@EnableAutoConfiguration
public class CatServiceTests extends IntegrationTest {
    @Autowired
    private CatService catService;

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
        assertThrows(Exception.class, () -> catService.create("Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, 5L));
    }

    @Test
    @Transactional
    @Rollback
    public void createAndGetAllTest() {
        // Arrange
        Long ownerId = 1L;
        CatMainDataDto expectedResult = catService.create("Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, ownerId);
        //CatMainDataDto expectedResult = new CatMainDataDto(catId, "Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, ownerId);

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
        Long ownerId = 1L;
        CatMainDataDto expectedResult =  catService.create("Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, ownerId);
        //CatMainDataDto expectedResult = new CatMainDataDto(catId, "Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, ownerId);
        catService.removeById(expectedResult.getId());

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
        Long ownerId =1L;
        CatMainDataDto expectedResult = catService.create("Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, ownerId);
        //CatMainDataDto expectedResult = new CatMainDataDto(catId, "Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, ownerId);

        // Act
        CatMainDataDto data = catService.getById(expectedResult.getId());

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
        Long owid = 1L;
        //ownerService.create("Petya", LocalDate.of(11, 7, 4));
        CatMainDataDto cat1 = catService.create("Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, owid);
        CatMainDataDto cat2 = catService.create("Marsik", LocalDate.of(1, 1, 1), "kashirskiy", Color.ORANGE, owid);

        // Act
        List<CatMainDataDto> firstCatFriends = catService.getFriends(cat1.getId());
        List<CatMainDataDto> secondCatFriends = catService.getFriends(cat2.getId());

        // Assert
        assertThat(firstCatFriends).isEmpty();
        assertThat(secondCatFriends).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    public void addFriendsTest() {
        // Arrange
        Long ownerId = 1L;
        CatMainDataDto cat1 = catService.create("Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, ownerId);
        CatMainDataDto cat2= catService.create("Marsik", LocalDate.of(1, 1, 1), "kashirskiy", Color.ORANGE, ownerId);
       // CatMainDataDto firstCatData = new CatMainDataDto(id1, "Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, ownerId);
       // CatMainDataDto secondCatData = new CatMainDataDto(id2, "Marsik", LocalDate.of(1, 1, 1), "kashirskiy", Color.ORANGE, ownerId);

        // Act
        catService.makeFriend(cat1.getId(), cat2.getId());

        // Assert
        List<CatMainDataDto> firstCatFriends = catService.getFriends(cat1.getId());
        List<CatMainDataDto> secondCatFriends = catService.getFriends(cat2.getId());

        assertThat(firstCatFriends).contains(cat2);
        assertThat(secondCatFriends).contains(cat1);
    }

    @Test
    @Transactional
    @Rollback
    public void addNotExistedFriendTest() {
        // Arrange
        Long ownerId = 1L;
        // Act
        CatMainDataDto cat = catService.create("Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, ownerId);

        // Assert
        assertThrows(RuntimeException.class,() -> catService.makeFriend(cat.getId(), 2L));
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
        Long ownerId = 1L;
        CatMainDataDto firstCatData = catService.create("Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, ownerId);
        CatMainDataDto secondCatData = catService.create("Marsik", LocalDate.of(1, 1, 1), "kashirskiy", Color.ORANGE, ownerId);

        // Act
        catService.makeFriend(firstCatData.getId(), secondCatData.getId());
        catService.removeFriend(firstCatData.getId(), secondCatData.getId());

        // Assert
        List<CatMainDataDto> firstCatFriends = catService.getFriends(firstCatData.getId());
        List<CatMainDataDto> secondCatFriends = catService.getFriends(secondCatData.getId());

        assertThat(firstCatFriends).doesNotContain(secondCatData);
        assertThat(secondCatFriends).doesNotContain(firstCatData);
    }

    @Test
    @Transactional
    @Rollback
    public void removeNotExistedFriendTest() {
        // Arrange
        Long ownerId = 1L;
        // Act
        CatMainDataDto cat = catService.create("Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, ownerId);

        assertThrows(NotFoundException.class,() -> catService.removeFriend(cat.getId(), 2L));
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
        Long ownerId = 1L;
        CatMainDataDto firstCatData = catService.create("Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, ownerId);
        CatMainDataDto secondCatData = catService.create("Marsik", LocalDate.of(1, 1, 1), "kashirskiy", Color.ORANGE, ownerId);

        // Act
        List<CatMainDataDto> data = catService.getAllWithFilter("GRAY", null, null);

        // Assert
        assertThat(data).contains(firstCatData);
        assertThat(data).doesNotContain(secondCatData);
    }
}

package kosandron;

import kosandron.dao.CatDao;
import kosandron.dao.OwnerDao;
import kosandron.dto.CatMainDataDto;
import kosandron.enums.Color;
import kosandron.services.CatService;
import kosandron.services.OwnerService;
import kosandron.services.jpaservices.JpaCatService;
import kosandron.services.jpaservices.JpaOwnerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {JpaCatService.class, JpaOwnerService.class, CatDao.class, OwnerDao.class})
@EnableAutoConfiguration
public class CatAndOwnerServiceTests extends IntegrationTest {
    @Autowired
    private CatService catService;
    @Autowired
    private OwnerService ownerService;

    @Test
    @Transactional
    @Rollback
    public void getCatsByOwnerIdEmptyListTest() {
        // Arrange
        Long id = ownerService.create("Vasya", LocalDate.of(1, 2, 3));

        // Act
        List<CatMainDataDto> cats = ownerService.getCatsListByOwnerId(id);

        // Assert
        assertThat(cats).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    public void getCatsByOwnerIdOneCatTest() {
        // Arrange
        Long ownerId = ownerService.create("Vasya", LocalDate.of(1, 2, 3));
        Long catId = catService.create("Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, ownerId);
        CatMainDataDto expectedResult = new CatMainDataDto(catId, "Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, ownerId);

        // Act
        List<CatMainDataDto> cats = ownerService.getCatsListByOwnerId(ownerId);

        // Assert
        assertThat(cats.size()).isEqualTo(0);
        assertThat(cats).doesNotContain(expectedResult);
    }

    @Test
    @Transactional
    @Rollback
    public void getCatsByOwnerIdRemovedCatTest() {
        // Arrange
        Long ownerId = ownerService.create("Vasya", LocalDate.of(1, 2, 3));
        Long catId = catService.create("Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, ownerId);
        catService.removeById(catId);

        // Act
        List<CatMainDataDto> cats = ownerService.getCatsListByOwnerId(ownerId);

        // Assert
        assertThat(cats).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    public void deleteOwnerTest() {
        // Arrange
        Long ownerId1 = ownerService.create("Petya", LocalDate.of(1, 1, 1));
        Long ownerId2 = ownerService.create("Vasya", LocalDate.of(3, 5, 12));
        Long catId1 = catService.create("Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, ownerId1);
        Long catId2 = catService.create("Yuppi", LocalDate.of(1, 3, 4), "kashirskiy", Color.BLUE, ownerId2);
        CatMainDataDto cat1Data = new CatMainDataDto(catId1, "Motik", LocalDate.of(1, 1, 1), "sfinks", Color.GRAY, ownerId1);
        CatMainDataDto cat2Data = new CatMainDataDto(catId2, "Yuppi", LocalDate.of(1, 3, 4), "kashirskiy", Color.BLUE, ownerId2);

        // Act
        ownerService.removeById(ownerId1);

        // Assert
        List<CatMainDataDto> cats = catService.getAll();
        assertThat(cats).contains(cat2Data);
        assertThat(cats).doesNotContain(cat1Data);
    }
}

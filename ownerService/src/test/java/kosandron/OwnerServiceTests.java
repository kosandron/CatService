package kosandron;


import org.junit.jupiter.api.Test;
import org.kosandron.dao.OwnerDao;
import org.kosandron.dto.OwnerMainDataDto;
import org.kosandron.exceptions.NotFoundException;
import org.kosandron.services.JpaOwnerService;
import org.kosandron.services.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {OwnerDao.class, JpaOwnerService.class})
@EnableJpaRepositories(basePackages = "org.kosandron")
@EntityScan(basePackages = "org.kosandron")
@EnableAutoConfiguration
public class OwnerServiceTests extends IntegrationTest {
    @Autowired
    private OwnerService ownerService;

    @Test
    @Transactional
    @Rollback
    void getAllAndCreateTest() {
        // Arrange
        OwnerMainDataDto expectedResult = ownerService.create("Vasya", LocalDate.of(1, 2, 3));
        //OwnerMainDataDto expectedResult = new OwnerMainDataDto(id, "Vasya", LocalDate.of(1, 2, 3));

        // Act
        List<OwnerMainDataDto> data = ownerService.getAll();

        // Assert
        assertThat(data).contains(expectedResult);
    }

    @Test
    @Transactional
    @Rollback
    public void deleteTest() {
        // Arrange
        OwnerMainDataDto expectedResult = ownerService.create("Vasya", LocalDate.of(1, 2, 3));
        //OwnerMainDataDto expectedResult = new OwnerMainDataDto(1L, "Vasya", LocalDate.of(1, 2, 3));
        ownerService.removeById(expectedResult.getId());

        // Act
        List<OwnerMainDataDto> data = ownerService.getAll();

        // Assert
        assertThat(data).doesNotContain(expectedResult);
    }

    @Test
    @Transactional
    @Rollback
    public void getByExitedIdTest() {
        // Arrange
        OwnerMainDataDto expectedResult = ownerService.create("Vasya", LocalDate.of(1, 2, 3));
       // OwnerMainDataDto expectedResult = new OwnerMainDataDto(1L, "Vasya", LocalDate.of(1, 2, 3));

        // Act
        OwnerMainDataDto data = ownerService.getById(expectedResult.getId());

        // Assert
        assertThat(data).isEqualTo(expectedResult);
    }

    @Test
    @Transactional
    @Rollback
    public void getByNotExitedIdTest() {
        // Act, Assert
        assertThrows(NotFoundException.class,() -> ownerService.getById(1L));
    }
}

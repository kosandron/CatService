package kosandron;

import kosandron.dao.CatDao;
import kosandron.dao.OwnerDao;
import kosandron.exceptions.SameCatException;
import kosandron.services.CatService;
import kosandron.services.OwnerService;
import kosandron.services.jpaservices.JpaCatService;
import kosandron.services.jpaservices.JpaOwnerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class LogicServiceTests {
    private CatDao catDao;
    private OwnerDao ownerDao;
    private CatService catService;
    private OwnerService ownerService;

    public LogicServiceTests() {
        catDao = Mockito.mock(CatDao.class);
        ownerDao = Mockito.mock(OwnerDao.class);
        catService = new JpaCatService(catDao, ownerDao);
        ownerService = new JpaOwnerService(ownerDao, catDao);
    }


    @Test
    public void makeFriendsTest() {
        // Act, Assert
        assertThrows(SameCatException.class ,() -> catService.makeFriend(1L, 1L));
    }

    @Test
    public void deleteFriendsTest() {
        // Act, Assert
        assertThrows(SameCatException.class ,() -> catService.removeFriend(1L, 1L));
    }
}

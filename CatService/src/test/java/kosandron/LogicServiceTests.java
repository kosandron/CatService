package kosandron;

import org.junit.jupiter.api.Test;
import org.kosandron.dao.CatDao;
import org.kosandron.exceptions.SameCatException;
import org.kosandron.services.CatService;
import org.kosandron.services.JpaCatService;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class LogicServiceTests {
    private CatDao catDao;
    private CatService catService;

    public LogicServiceTests() {
        catDao = Mockito.mock(CatDao.class);
        catService = new JpaCatService(catDao);
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

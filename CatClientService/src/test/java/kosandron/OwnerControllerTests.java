package kosandron;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kosandron.CatApplication;
import org.kosandron.config.SecurityConfig;
import org.kosandron.controllers.CatControllerImpl;
import org.kosandron.controllers.OwnerController;
import org.kosandron.controllers.OwnerControllerImpl;
import org.kosandron.data.InputCatData;
import org.kosandron.data.InputOwnerData;
import org.kosandron.dto.CatMainDataDto;
import org.kosandron.dto.OwnerMainDataDto;
import org.kosandron.kafka.CatKafkaProducer;
import org.kosandron.kafka.OwnerKafkaProducer;
import org.kosandron.security.UserService;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@SpringBootTest
@ContextConfiguration(classes= CatApplication.class)
@Import({OwnerControllerImpl.class, SecurityConfig.class})
@EnableAutoConfiguration
public class OwnerControllerTests extends IntegrationTest {
    @Autowired
    private UserService userService;
    @Autowired
    private OwnerControllerImpl ownerController;
    @Autowired
    private CatControllerImpl catController;

   /* @Autowired
    private OwnerKafkaProducer ownerKafkaProducer;
    @Autowired
    private CatKafkaProducer catKafkaProducer;*/
    @Mock
    private Principal principal;

    @BeforeEach
    public void setup() {
       /* userService = mock(UserService.class);
        ownerKafkaProducer = mock(OwnerKafkaProducer.class);
        catKafkaProducer = mock(CatKafkaProducer.class);
        catController = new CatControllerImpl(userService, catKafkaProducer, ownerKafkaProducer);
        ownerController = new OwnerControllerImpl(userService, ownerKafkaProducer);*/
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(username="admin", roles={"ADMIN"})
    public void getAllOwners() throws Exception {
        List<OwnerMainDataDto> owners = ownerController.getAllOwners();
        System.out.println(owners);
    }

    @Test
    @WithMockUser(username="admin", roles={"ADMIN"})
    public void getOwnerById() throws Exception {
        OwnerMainDataDto owner = ownerController.getOwnerById(1L);
        System.out.println(owner);
    }

    @Test
    @WithMockUser(username="admin", roles={"ADMIN"})
    public void createOwner() throws Exception {
        OwnerMainDataDto newOwner = ownerController.createOwner(new InputOwnerData("petya", LocalDate.now(), "petya", "1234", "USER"));
        List<OwnerMainDataDto> owners = ownerController.getAllOwners();
        System.out.println(owners);
        assertThat(owners).contains(newOwner);
    }

    @Test
    @WithMockUser(username="admin", roles={"ADMIN"})
    public void deleteOwner() throws Exception {
        OwnerMainDataDto newOwner = ownerController.createOwner(new InputOwnerData("petya", LocalDate.now(), "petya", "1234", "USER"));
        ownerController.deleteOwner(newOwner.getId());
        List<OwnerMainDataDto> owners = ownerController.getAllOwners();
        assertThat(owners).doesNotContain(newOwner);
    }

    @Test
    @WithMockUser(username="admin", roles={"ADMIN"})
    public void getAllCats() throws Exception {
        List<CatMainDataDto> cats = catController.getAll(principal);
        System.out.println(cats);
    }

    @Test
    @WithMockUser(username="admin", roles={"ADMIN"})
    public void createCat() throws Exception {
        CatMainDataDto cat = catController.create(new InputCatData("Vasya", LocalDate.now(), "amerikan", "ORANGE", 1L), principal);
        List<CatMainDataDto> cats = catController.getAll(principal);
        System.out.println(cats);
        assertThat(cats).contains(cat);
    }

    @Test
    @WithMockUser(username="admin", roles={"ADMIN"})
    public void deleteCat() throws Exception {
        CatMainDataDto cat = catController.create(new InputCatData("Vasya", LocalDate.now(), "amerikan", "ORANGE", 1L), principal);
        catController.removeById(cat.getId(), principal);
        List<CatMainDataDto> cats = catController.getAll(principal);
        System.out.println(cats);
        assertThat(cats).doesNotContain(cat);
    }

    @Test
    @WithMockUser(username="admin", roles={"ADMIN"})
    public void makeFriends() throws Exception {
        CatMainDataDto cat1 = catController.create(new InputCatData("Vasya", LocalDate.now(), "amerikan", "ORANGE", 1L), principal);
        CatMainDataDto cat2 = catController.create(new InputCatData("Ghora", LocalDate.now(), "amerikan", "BLUE", 1L), principal);
        catController.makeFriend(cat1.getId(), cat2.getId(), principal);
        List<CatMainDataDto> friends1 = catController.getFriends(cat1.getId(), principal);
        List<CatMainDataDto> friends2 = catController.getFriends(cat2.getId(), principal);

        assertThat(friends1).contains(cat2);
        assertThat(friends2).contains(cat1);
    }

    @Test
    @WithMockUser(username="admin", roles={"ADMIN"})
    public void deleteFriends() throws Exception {
        CatMainDataDto cat1 = catController.create(new InputCatData("Vasya", LocalDate.now(), "amerikan", "ORANGE", 1L), principal);
        CatMainDataDto cat2 = catController.create(new InputCatData("Ghora", LocalDate.now(), "amerikan", "BLUE", 1L), principal);
        catController.makeFriend(cat1.getId(), cat2.getId(), principal);
        catController.removeFriend(cat1.getId(), cat2.getId(), principal);
        List<CatMainDataDto> friends1 = catController.getFriends(cat1.getId(), principal);
        List<CatMainDataDto> friends2 = catController.getFriends(cat2.getId(), principal);

        assertThat(friends1).doesNotContain(cat2);
        assertThat(friends2).doesNotContain(cat1);
    }
}

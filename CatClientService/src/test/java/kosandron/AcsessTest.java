package kosandron;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kosandron.CatApplication;
import org.kosandron.config.SecurityConfig;
import org.kosandron.controllers.CatControllerImpl;
import org.kosandron.controllers.OwnerControllerImpl;
import org.kosandron.security.UserService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;

import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ContextConfiguration(classes= CatApplication.class)
@Import({OwnerControllerImpl.class, SecurityConfig.class})
@EnableAutoConfiguration
@WebAppConfiguration
public class AcsessTest extends IntegrationTest {
    @Mock
    private Principal principal;

    //@Mock
    private UserService userService;

    @InjectMocks
    private OwnerControllerImpl ownerController;

    @InjectMocks
    private CatControllerImpl catController;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup8() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

    }

   /* @BeforeEach
    public void setup() {
        catService = mock(JpaCatService.class);
        userService = mock(UserService.class);
        ownerService = mock(JpaOwnerService.class);
        catController = new CatControllerImpl(catService, userService);
        ownerController = new OwnerControllerImpl(ownerService, userService);
        MockitoAnnotations.openMocks(this);
    //    mockMvc = MockMvcBuilders.standaloneSetup(ownerController, catController).build();
    }*/

    @Test
    @WithMockUser(username="admin", roles={"USER"})
    public void getOwnerByIdUserTest() throws Exception {
        mockMvc.perform(get("/owner/43").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(403));
    }


    @Test
    @WithMockUser(username="admin", roles={"ADMIN"})
    public void getAllOwnerAdminTest() throws Exception {
        mockMvc.perform(get("/owner/all").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
    }

    @Test
    @WithMockUser(username="admin", roles={"USER"})
    public void getAllOwnerUserTest() throws Exception {
        mockMvc.perform(get("/owner/all").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(403));
    }

    @Test
    @WithMockUser(username="admin", roles={"USER"})
    public void createOwnerUserTest() throws Exception {
        String requestJson= """
                {
                    "name" : "Georgy",
                    "birthDate" : "2021-03-19",
                    "login" : "gosha",
                    "password" : "1234",
                    "roles" : "USER, ADMIN"
                }
                """;
        mockMvc.perform(post("/owner/create")
                        .contentType(new MediaType("application", "json", java.nio.charset.Charset.forName("UTF-8")))
                        .content(requestJson).accept(new MediaType("application", "json", java.nio.charset.Charset.forName("UTF-8"))))
                .andExpect(status().is(403));
    }

    @Test
    @WithMockUser(username="admin", roles={"ANONYMOUS"})
    public void getOwnerByIdAnonymousTest() throws Exception {
        mockMvc.perform(get("/owner/43").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(403));
    }

    @Test
    @WithMockUser(username="admin", roles={"ANONYMOUS"})
    public void getCatByIdAnonymousTest() throws Exception {
        mockMvc.perform(get("/cat/43").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(403));
    }

    @Test
    @WithMockUser(username="admin", roles={"ANONYMOUS"})
    public void getAllCatsAnonymousTest() throws Exception {
        mockMvc.perform(get("/cat/all").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(403));
    }

    @Test
    @WithMockUser(username="admin", roles={"ANONYMOUS"})
    public void makeFriendsCatsAnonymousTest() throws Exception {
        mockMvc.perform(put("/cat/makeFriends?cat1=96&cat2=99").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(403));
    }

    @Test
    @WithMockUser(username="admin", roles={"ANONYMOUS"})
    public void removeFriendsCatsAnonymousTest() throws Exception {
        mockMvc.perform(put("/cat/removeFriends?cat1=96&cat2=99").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(403));
    }

    @Test
    @WithMockUser(username="admin", roles={"ANONYMOUS"})
    public void getFilterCatsAnonymousTest() throws Exception {
        mockMvc.perform(get("/cat/filter?color=ORANGE").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(403));
    }
}

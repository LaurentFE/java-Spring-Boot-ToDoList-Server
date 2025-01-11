package fr.laurentFE.todolistspringbootserver.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs(outputDir = "target/snippets")
public class ServerControllerE2ETests {

    @Autowired
    private ServerController serverController;

    @Autowired
    private RestDocumentationContextProvider restDocumentation;
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(serverController)
                .setControllerAdvice(new CustomExceptionController())
                .apply(documentationConfiguration(restDocumentation))
                .build();
        primeDataBase();
    }

    @AfterEach
    public void tearDown() {
        emptyDataBase();

    }
    private void primeDataBase() {
        jdbcTemplate.execute("INSERT INTO `users` (`user_name`) VALUES (\"Archibald\");");
        jdbcTemplate.execute("INSERT INTO `users` (`user_name`) VALUES (\"Balthazar\");");
        jdbcTemplate.execute("INSERT INTO `lists` (`user_id`) VALUES (1);");
        jdbcTemplate.execute("INSERT INTO `lists` (`user_id`) VALUES (1);");
        jdbcTemplate.execute("INSERT INTO `lists` (`user_id`) VALUES (2)");
        jdbcTemplate.execute("INSERT INTO `items` (`label`, `is_checked`) VALUES (\"Chocolate\", true);");
        jdbcTemplate.execute("INSERT INTO `items` (`label`, `is_checked`) VALUES (\"Milk\", true);");
        jdbcTemplate.execute("INSERT INTO `items` (`label`, `is_checked`) VALUES (\"Cookies\", false);");
        jdbcTemplate.execute("INSERT INTO `items` (`label`, `is_checked`) VALUES (\"10 Sit ups\", true);");
        jdbcTemplate.execute("INSERT INTO `items` (`label`, `is_checked`) VALUES (\"10 Push ups\", false);");
        jdbcTemplate.execute("INSERT INTO `items` (`label`, `is_checked`) VALUES (\"10 Pull ups\", false);");
        jdbcTemplate.execute("INSERT INTO `items` (`label`, `is_checked`) VALUES (\"Bacon\", false);");
        jdbcTemplate.execute("INSERT INTO `items` (`label`, `is_checked`) VALUES (\"Lettuce\", true);");
        jdbcTemplate.execute("INSERT INTO `items` (`label`, `is_checked`) VALUES (\"Tomatoes\", false);");
        jdbcTemplate.execute("INSERT INTO `list_items` (`list_id`, `item_id`) VALUES (1, 1);");
        jdbcTemplate.execute("INSERT INTO `list_items` (`list_id`, `item_id`) VALUES (1, 2);");
        jdbcTemplate.execute("INSERT INTO `list_items` (`list_id`, `item_id`) VALUES (1, 3);");
        jdbcTemplate.execute("INSERT INTO `list_items` (`list_id`, `item_id`) VALUES (2, 4);");
        jdbcTemplate.execute("INSERT INTO `list_items` (`list_id`, `item_id`) VALUES (2, 5);");
        jdbcTemplate.execute("INSERT INTO `list_items` (`list_id`, `item_id`) VALUES (2, 6);");
        jdbcTemplate.execute("INSERT INTO `list_items` (`list_id`, `item_id`) VALUES (3, 7);");
        jdbcTemplate.execute("INSERT INTO `list_items` (`list_id`, `item_id`) VALUES (3, 8);");
        jdbcTemplate.execute("INSERT INTO `list_items` (`list_id`, `item_id`) VALUES (3, 9);");
        jdbcTemplate.execute("INSERT INTO `list_names` (`list_id`, `label`) VALUES (1, \"Groceries\");");
        jdbcTemplate.execute("INSERT INTO `list_names` (`list_id`, `label`) VALUES (2, \"Exercises\");");
        jdbcTemplate.execute("INSERT INTO `list_names` (`list_id`, `label`) VALUES (3, \"Groceries\");");
    }

    private void emptyDataBase() {
        jdbcTemplate.execute("DELETE FROM `list_names`;");
        jdbcTemplate.execute("DELETE FROM `list_items`;");
        jdbcTemplate.execute("DELETE FROM `items`;");
        jdbcTemplate.execute("DELETE FROM `lists`;");
        jdbcTemplate.execute("DELETE FROM `users`;");
        jdbcTemplate.execute("ALTER TABLE `users` AUTO_INCREMENT=1;");
        jdbcTemplate.execute("ALTER TABLE `lists` AUTO_INCREMENT=1;");
        jdbcTemplate.execute("ALTER TABLE `items` AUTO_INCREMENT=1;");
    }

    @Test
    public void shouldReturnDefaultMessage() throws Exception {
        this.mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("list available endpoints and usage")))
                .andDo(document("home"));
    }

    @Test
    public void ServerController_getUsers_returnsListOfUser() throws Exception {
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get("/rest/users");

        mockMvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andDo(document("getUsers", responseFields(
                        fieldWithPath("[].userId").description("The user's id"),
                        fieldWithPath("[].userName").description("The user's name")
                )));
    }

    @Test
    public void ServerController_getUsersById_returnsUser() throws Exception {
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get("/rest/users/{id}", 1);

        mockMvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andDo(document("getUsersId", responseFields(
                        fieldWithPath("userId").description("The user's id"),
                        fieldWithPath("userName").description("The user's name")
                )));
    }

    @Test
    public void ServerController_postUsers_returnsCreatedUser() throws Exception {
        String jsonResponseBody = "{ \"userName\": \"Cornelius\" }";
        ObjectMapper objectMapper = new ObjectMapper();
        Object jsonObject = objectMapper.readValue(jsonResponseBody, Object.class);
        String prettyJsonResponseBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post("/rest/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(prettyJsonResponseBody);

        mockMvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andDo(document("postUsers", responseFields(
                        fieldWithPath("userId").description("The user's id"),
                        fieldWithPath("userName").description("The user's name")
                )));
    }

}

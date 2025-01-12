package fr.laurentFE.todolistspringbootserver.web;

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

import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
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
    public void ServerControllerE2E_getUsers_returnsListOfUser() throws Exception {
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get("/rest/users");

        mockMvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[?(@.userId == 1 && @.userName == \"Archibald\")]").exists())
                .andExpect(jsonPath("$.[?(@.userId == 2 && @.userName == \"Balthazar\")]").exists())
                .andDo(document("getUsers",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].userId").description("The user's id"),
                                fieldWithPath("[].userName").description("The user's name")
                )));
    }

    @Test
    public void ServerControllerE2E_getUsersById_returnsUser() throws Exception {
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get("/rest/users/{id}", 1);

        mockMvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[?(@.userId == 1 && @.userName == \"Archibald\")]").exists())
                .andDo(document("getUsersId",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("userId").description("The user's id"),
                                fieldWithPath("userName").description("The user's name")
                )));
    }

    @Test
    public void ServerControllerE2E_postUsers_returnsCreatedUser() throws Exception {
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post("/rest/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"userName\": \"Cornelius\" }");

        mockMvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.[?(@.userName == \"Cornelius\")]").exists())
                .andDo(document("postUsers",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("userId").description("The user's id"),
                                fieldWithPath("userName").description("The user's name")
                )));
    }

    @Test
    public void ServerControllerE2E_patchUsersById_returnsPatchedUser() throws Exception {
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .patch("/rest/users/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"userName\": \"Demetrius\" }");

        mockMvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName")
                        .value("Demetrius"))
                .andExpect(jsonPath("$.userId")
                        .value(1))
                .andDo(document("patchUsers",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("userId").description("The user's id"),
                                fieldWithPath("userName").description("The user's name"))
                ));
    }

    @Test
    public void ServerControllerE2E_getToDoLists_returnsListOfToDoList() throws Exception {
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get("/rest/toDoLists")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"userName\": \"Archibald\" }");

        mockMvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[?(@.listId == 1 " +
                        "&& @.userId == 1 " +
                        "&& @.label == \"Groceries\" " +
                        "&& @.items.[?(@.itemId == 1 " +
                                    "&& @.label == \"Chocolate\" " +
                                    "&& @.checked == true )] " +
                        "&& @.items.[?(@.itemId == 2 " +
                                    "&& @.label == \"Milk\" " +
                                    "&& @.checked == true )] " +
                        "&& @.items.[?(@.itemId == 3 " +
                                    "&& @.label == \"Cookies\" " +
                                    "&& @.checked == false )] " +
                        ")]").exists())
                .andExpect(jsonPath("$.[?(@.listId == 2 " +
                        "&& @.userId == 1 " +
                        "&& @.label == \"Exercises\" " +
                        "&& @.items.[?(@.itemId == 4 " +
                                    "&& @.label == \"10 Sit ups\" " +
                                    "&& @.checked == true )] " +
                        "&& @.items.[?(@.itemId == 5 " +
                                    "&& @.label == \"10 Push ups\" " +
                                    "&& @.checked == false )] " +
                        "&& @.items.[?(@.itemId == 6 " +
                                    "&& @.label == \"10 Pull ups\" " +
                                    "&& @.checked == false )] " +
                        ")]").exists())
                .andDo(document("getToDoLists",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].listId").description("The todo list's id"),
                                fieldWithPath("[].userId").description("The todo list's owner's userId"),
                                fieldWithPath("[].label").description("The todo list's label"),
                                fieldWithPath("[].items").description("The list of the todo list's items"),
                                fieldWithPath("[].items[].itemId").description("The item's id"),
                                fieldWithPath("[].items[].label").description("The item's items"),
                                fieldWithPath("[].items[].checked").description("The item's state (checked/unchecked)")
                )));
    }

    @Test
    public void ServerControllerE2E_getToDoListsById_returnsListOfToDoList() throws Exception {
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get("/rest/toDoLists/{id}", 2)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"userName\": \"Archibald\" }");

        mockMvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[?(@.listId == 2 " +
                        "&& @.userId == 1 " +
                        "&& @.label == \"Exercises\" " +
                        "&& @.items.[?(@.itemId == 4 " +
                        "&& @.label == \"10 Sit ups\" " +
                        "&& @.checked == true )] " +
                        "&& @.items.[?(@.itemId == 5 " +
                        "&& @.label == \"10 Push ups\" " +
                        "&& @.checked == false )] " +
                        "&& @.items.[?(@.itemId == 6 " +
                        "&& @.label == \"10 Pull ups\" " +
                        "&& @.checked == false )] " +
                        ")]").exists())
                .andDo(document("getToDoListsId",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("listId").description("The todo list's id"),
                                fieldWithPath("userId").description("The todo list's owner's userId"),
                                fieldWithPath("label").description("The todo list's label"),
                                fieldWithPath("items").description("The list of the todo list's items"),
                                fieldWithPath("items[].itemId").description("The item's id"),
                                fieldWithPath("items[].label").description("The item's items"),
                                fieldWithPath("items[].checked").description("The item's state (checked/unchecked)")
                        )));
    }

    @Test
    public void ServerControllerE2E_postToDoLists_returnsSavedToDoList() throws Exception {
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post("/rest/toDoLists")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"userId\": 2, \"label\": \"Groceries\", \"items\":" +
                        "[{ \"label\": \"Chocolate\", \"checked\": true}," +
                        "{ \"label\": \"Milk\", \"checked\": true}," +
                        "{ \"label\": \"Cookies\", \"checked\": false}]}");

        mockMvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.[?(@.listId == 4 " +
                        "&& @.userId == 2 " +
                        "&& @.label == \"Groceries\" " +
                        "&& @.items.[?(@.itemId == 10 " +
                        "&& @.label == \"Chocolate\" " +
                        "&& @.checked == true )] " +
                        "&& @.items.[?(@.itemId == 11 " +
                        "&& @.label == \"Milk\" " +
                        "&& @.checked == true )] " +
                        "&& @.items.[?(@.itemId == 12 " +
                        "&& @.label == \"Cookies\" " +
                        "&& @.checked == false )] " +
                        ")]").exists())
                .andDo(document("postToDoLists",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("listId").description("The todo list's id"),
                                fieldWithPath("userId").description("The todo list's owner's userId"),
                                fieldWithPath("label").description("The todo list's label"),
                                fieldWithPath("items").description("The list of the todo list's items"),
                                fieldWithPath("items[].itemId").description("The item's id"),
                                fieldWithPath("items[].label").description("The item's items"),
                                fieldWithPath("items[].checked").description("The item's state (checked/unchecked)")
                        )));
    }

    @Test
    public void ServerControllerE2E_patchToDoLists_returnsPatchedToDoList() throws Exception {
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .patch("/rest/toDoLists/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"userId\": 1, \"label\": \"List of Groceries\"}");

        mockMvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[?(@.listId == 1 " +
                        "&& @.userId == 1 " +
                        "&& @.label == \"List of Groceries\" " +
                        "&& @.items.[?(@.itemId == 1 " +
                        "&& @.label == \"Chocolate\" " +
                        "&& @.checked == true )] " +
                        "&& @.items.[?(@.itemId == 2 " +
                        "&& @.label == \"Milk\" " +
                        "&& @.checked == true )] " +
                        "&& @.items.[?(@.itemId == 3 " +
                        "&& @.label == \"Cookies\" " +
                        "&& @.checked == false )] " +
                        ")]").exists())
                .andDo(document("patchToDoLists",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("listId").description("The todo list's id"),
                                fieldWithPath("userId").description("The todo list's owner's userId"),
                                fieldWithPath("label").description("The todo list's label"),
                                fieldWithPath("items").description("The list of the todo list's items"),
                                fieldWithPath("items[].itemId").description("The item's id"),
                                fieldWithPath("items[].label").description("The item's items"),
                                fieldWithPath("items[].checked").description("The item's state (checked/unchecked)")
                        )));
    }

    @Test
    public void ServerControllerE2E_postItems_returnsCreatedItem() throws Exception {
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post("/rest/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"listId\": 1, \"label\": \"Chocolate\", \"checked\": false }");

        mockMvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.itemId").value(10))
                .andExpect(jsonPath("$.label").value("Chocolate"))
                .andExpect(jsonPath("$.checked").value(false))
                .andDo(document("postItems",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("itemId").description("The item's id"),
                                fieldWithPath("label").description("The item's items"),
                                fieldWithPath("checked").description("The item's state (checked/unchecked)")
                        )));
    }
}

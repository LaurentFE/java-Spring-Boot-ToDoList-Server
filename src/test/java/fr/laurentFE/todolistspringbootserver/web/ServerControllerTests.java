package fr.laurentFE.todolistspringbootserver.web;


import fr.laurentFE.todolistspringbootserver.model.Item;
import fr.laurentFE.todolistspringbootserver.model.RItem;
import fr.laurentFE.todolistspringbootserver.model.ToDoList;
import fr.laurentFE.todolistspringbootserver.model.User;
import fr.laurentFE.todolistspringbootserver.model.exceptions.DataDuplicateException;
import fr.laurentFE.todolistspringbootserver.model.exceptions.DataNotFoundException;
import fr.laurentFE.todolistspringbootserver.model.exceptions.UnexpectedParameterException;
import fr.laurentFE.todolistspringbootserver.service.ServerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ServerControllerTests {

    @Autowired
    private MockMvc mvc;
    @Mock
    private ServerService serverService;

    @InjectMocks
    private ServerController serverController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mvc = MockMvcBuilders
                .standaloneSetup(serverController)
                .setControllerAdvice(new CustomExceptionController())
                .build();
    }

    @Test
    public void ServerController_getUsers_returnsListOfUser() throws Exception {
        User user1 = new User(1, "Archibald");
        User user2 = new User(2, "Balthazar");
        User user3 = new User(3, "Cornelius");
        ArrayList<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        users.add(user3);
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(
                "/rest/users"
        );

        when(serverService.findAllUsers())
                .thenReturn(users);

        mvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$.[0].userId").value(1))
                .andExpect(jsonPath("$.[0].userName").value("Archibald"))
                .andExpect(jsonPath("$.[1].userId").value(2))
                .andExpect(jsonPath("$.[1].userName").value("Balthazar"))
                .andExpect(jsonPath("$.[2].userId").value(3))
                .andExpect(jsonPath("$.[2].userName").value("Cornelius"));
    }

    @Test
    public void ServerController_getUsersById_returnsUser() throws Exception {
        User user = new User(1, "Archibald");
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(
                "/rest/users/1"
        );

        when(serverService.findUser(1))
                .thenReturn(user);

        mvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.userName").value("Archibald"));
    }

    @Test
    public void ServerController_getUsersById_returnsNotFound() throws Exception {
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(
                "/rest/users/1"
        );

        when(serverService.findUser(1))
                .thenThrow(new DataNotFoundException("userId"));

        mvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("No data was found for requested : userId"));
    }

    @Test
    public void ServerController_postUsers_returnsCreatedUser() throws Exception {
        User savedUser = new User(1, "Archibald");
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post("/rest/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"userName\": \"Archibald\" }");

        when(serverService.createUser(any(User.class)))
                .thenReturn(savedUser);

        mvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId")
                        .value(1))
                .andExpect(jsonPath("$.userName")
                        .value("Archibald"));
    }

    @Test
    public void ServerController_postUsers_throwsUnexpectedParameterException() throws Exception {
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post("/rest/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"userId\": 1, \"userName\": \"Archibald\" }");

        mvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Unexpected request JSON key : userId"));
    }

    @Test
    public void ServerController_postUsers_throwsOverSizedStringProvidedException() throws Exception {
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post("/rest/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"userName\": \"1234567890123456789012345678901234567890123456\" }");

        mvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("String parameter is too long : userName [max:45]"));
    }

    @Test
    public void ServerController_postUsers_throwsDataDuplicateException() throws Exception {
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post("/rest/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"userName\": \"Archibald\" }");

        when(serverService.createUser(any(User.class)))
                .thenThrow(new DataDuplicateException("userName"));

        mvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("An entry already exist for requested : userName"));
    }

    @Test
    public void ServerController_patchUsersById_returnsPatchedUser() throws Exception {
        User patchedUser = new User(1, "Balthazar");
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .patch("/rest/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"userName\": \"Balthazar\" }");

        when(serverService.updateUser(any(User.class), eq(1)))
                .thenReturn(patchedUser);

        mvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName")
                        .value("Balthazar"))
                .andExpect(jsonPath("$.userId")
                        .value(1));
    }

    @Test
    public void ServerController_patchUsersById_throwsUnexpectedParameterException() throws Exception {
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .patch("/rest/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"userId\": 1, \"userName\": \"Balthazar\" }");

        mvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Unexpected request JSON key : userId"));
    }

    @Test
    public void ServerController_patchUsersById_throwsOverSizedStringProvidedException() throws Exception {
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .patch("/rest/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userName\": \"1234567890123456789012345678901234567890123456\" }");

        mvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("String parameter is too long : userName [max:45]"));
    }

    @Test
    public void ServerController_patchUsersById_throwsDataDuplicateException() throws Exception {
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .patch("/rest/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"userName\": \"Archibald\" }");

        when(serverService.updateUser(any(User.class), eq(1)))
                .thenThrow(new DataNotFoundException("userId"));

        mvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("No data was found for requested : userId"));
    }

    @Test
    public void ServerController_getToDoLists_returnsListOfToDoList() throws Exception {
        Item item1 = new Item("Chocolate", true);
        item1.setItemId(1);
        Item item2 = new Item("Milk", true);
        item2.setItemId(2);
        Item item3 = new Item("Cookies", false);
        item3.setItemId(3);
        ArrayList<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        items.add(item3);
        ToDoList list1 = new ToDoList(1, 1, "Groceries", items);
        ToDoList list2 = new ToDoList(2, 1, "Exercises", new ArrayList<>());
        ArrayList<ToDoList> toDoLists = new ArrayList<>();
        toDoLists.add(list1);
        toDoLists.add(list2);
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get("/rest/toDoLists")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"userName\": \"Archibald\" }");

        when(serverService.findAllToDoLists(any(User.class)))
                .thenReturn(toDoLists);

        mvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].listId").value(1))
                .andExpect(jsonPath("$.[0].userId").value(1))
                .andExpect(jsonPath("$.[0].label").value("Groceries"))
                .andExpect(jsonPath("$.[0].items").isArray())
                .andExpect(jsonPath("$.[0].items", hasSize(3)))
                .andExpect(jsonPath("$.[0].items.[0].itemId").value(1))
                .andExpect(jsonPath("$.[0].items.[0].label").value("Chocolate"))
                .andExpect(jsonPath("$.[0].items.[0].checked").value(true))
                .andExpect(jsonPath("$.[0].items.[1].itemId").value(2))
                .andExpect(jsonPath("$.[0].items.[1].label").value("Milk"))
                .andExpect(jsonPath("$.[0].items.[1].checked").value(true))
                .andExpect(jsonPath("$.[0].items.[2].itemId").value(3))
                .andExpect(jsonPath("$.[0].items.[2].label").value("Cookies"))
                .andExpect(jsonPath("$.[0].items.[2].checked").value(false))
                .andExpect(jsonPath("$.[1].listId").value(2))
                .andExpect(jsonPath("$.[1].userId").value(1))
                .andExpect(jsonPath("$.[1].label").value("Exercises"))
                .andExpect(jsonPath("$.[1].items").isArray())
                .andExpect(jsonPath("$.[1].items", hasSize(0)));
    }

    @Test
    public void ServerController_getToDoLists_throwsMethodArgumentNotValidException() throws Exception {

        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get("/rest/toDoLists")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}");

        mvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Invalid value 'null' for field 'userName' : This field cannot be empty;"));
    }

    @Test
    public void ServerController_getToDoListsById_returnsToDoList() throws Exception {
        Item item1 = new Item("Chocolate", true);
        item1.setItemId(1);
        Item item2 = new Item("Milk", true);
        item2.setItemId(2);
        Item item3 = new Item("Cookies", false);
        item3.setItemId(3);
        ArrayList<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        items.add(item3);
        ToDoList list = new ToDoList(1, 1, "Groceries", items);

        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get("/rest/toDoLists/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"userName\": \"Archibald\" }");

        when(serverService.findSpecificToDoList(any(User.class), eq(1)))
                .thenReturn(list);

        mvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.listId").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.label").value("Groceries"))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items", hasSize(3)))
                .andExpect(jsonPath("$.items.[0].itemId").value(1))
                .andExpect(jsonPath("$.items.[0].label").value("Chocolate"))
                .andExpect(jsonPath("$.items.[0].checked").value(true))
                .andExpect(jsonPath("$.items.[1].itemId").value(2))
                .andExpect(jsonPath("$.items.[1].label").value("Milk"))
                .andExpect(jsonPath("$.items.[1].checked").value(true))
                .andExpect(jsonPath("$.items.[2].itemId").value(3))
                .andExpect(jsonPath("$.items.[2].label").value("Cookies"))
                .andExpect(jsonPath("$.items.[2].checked").value(false));
    }

    @Test
    public void ServerController_getToDoListsById_throwsUnexpectedParameterException() throws Exception {
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get("/rest/toDoLists/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"userId\": 1, \"userName\": \"Archibald\" }");

        mvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Unexpected request JSON key : userId"));
    }

    @Test
    public void ServerController_getToDoListsById_throwsDataNotFoundException() throws Exception {
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get("/rest/toDoLists/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"userName\": \"Archibald\" }");

        when(serverService.findSpecificToDoList(any(User.class), eq(1)))
                .thenThrow(new DataNotFoundException("(listId, userName)"));

        mvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("No data was found for requested : (listId, userName)"));
    }

    @Test
    public void ServerController_postToDoLists_returnsSavedToDoList() throws Exception {
        Item item1 = new Item("Chocolate", true);
        item1.setItemId(1);
        Item item2 = new Item("Milk", true);
        item2.setItemId(2);
        Item item3 = new Item("Cookies", false);
        item3.setItemId(3);
        ArrayList<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        items.add(item3);
        ToDoList savedToDoList = new ToDoList(1, 1, "Groceries", items);
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post("/rest/toDoLists")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"userId\": 1, \"label\": \"Groceries\", \"items\":" +
                        "[{ \"label\": \"Chocolate\", \"checked\": true}," +
                        "{ \"label\": \"Milk\", \"checked\": true}," +
                        "{ \"label\": \"Cookies\", \"checked\": false}]}");

        when(serverService.createToDoList(any(ToDoList.class)))
                .thenReturn(savedToDoList);

        mvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.listId").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.label").value("Groceries"))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items", hasSize(3)))
                .andExpect(jsonPath("$.items.[0].itemId").value(1))
                .andExpect(jsonPath("$.items.[0].label").value("Chocolate"))
                .andExpect(jsonPath("$.items.[0].checked").value(true))
                .andExpect(jsonPath("$.items.[1].itemId").value(2))
                .andExpect(jsonPath("$.items.[1].label").value("Milk"))
                .andExpect(jsonPath("$.items.[1].checked").value(true))
                .andExpect(jsonPath("$.items.[2].itemId").value(3))
                .andExpect(jsonPath("$.items.[2].label").value("Cookies"))
                .andExpect(jsonPath("$.items.[2].checked").value(false));
    }

    @Test
    public void ServerController_postToDoLists_throwsOverSizedStringProvidedException() throws Exception {
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post("/rest/toDoLists")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"userId\": 1, \"label\": \"Groceries\", \"items\":" +
                        "[{ \"label\": \"Chocolate\", \"checked\": true}," +
                        "{ \"label\": \"1234567890123456789012345678901234567890123456\", \"checked\": true}," +
                        "{ \"label\": \"Cookies\", \"checked\": false}]}");

        mvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("String parameter is too long : items[label] [max:45]"));
    }

    @Test
    public void ServerController_postToDoLists_throwsUnexpectedParameterException() throws Exception {
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post("/rest/toDoLists")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"userId\": 1, \"label\": \"Groceries\", \"items\":" +
                        "[{ \"label\": \"Chocolate\", \"checked\": true}," +
                        "{ \"itemId\": 1, \"label\": \"Milk\", \"checked\": true}," +
                        "{ \"label\": \"Cookies\", \"checked\": false}]}");

        mvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Unexpected request JSON key : items[itemId]"));
    }

    @Test
    public void ServerController_patchToDoListById_returnsPatchedToDoList() throws Exception {
        Item item1 = new Item("Chocolate", true);
        item1.setItemId(1);
        Item item2 = new Item("Milk", true);
        item2.setItemId(2);
        Item item3 = new Item("Cookies", false);
        item3.setItemId(3);
        ArrayList<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        items.add(item3);
        ToDoList patchedToDoList = new ToDoList(1, 1, "List of Groceries", items);
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .patch("/rest/toDoLists/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"userId\": 1, \"label\": \"List of Groceries\"}");

        when(serverService.updateToDoList(any(ToDoList.class), eq(1)))
                .thenReturn(patchedToDoList);

        mvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.listId").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.label").value("List of Groceries"))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items", hasSize(3)))
                .andExpect(jsonPath("$.items.[0].itemId").value(1))
                .andExpect(jsonPath("$.items.[0].label").value("Chocolate"))
                .andExpect(jsonPath("$.items.[0].checked").value(true))
                .andExpect(jsonPath("$.items.[1].itemId").value(2))
                .andExpect(jsonPath("$.items.[1].label").value("Milk"))
                .andExpect(jsonPath("$.items.[1].checked").value(true))
                .andExpect(jsonPath("$.items.[2].itemId").value(3))
                .andExpect(jsonPath("$.items.[2].label").value("Cookies"))
                .andExpect(jsonPath("$.items.[2].checked").value(false));
    }

    @Test
    public void ServerController_patchToDoListById_throwsDataNotFoundException() throws Exception {
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .patch("/rest/toDoLists/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"userId\": 1, \"label\": \"List of Groceries\"}");

        when(serverService.updateToDoList(any(ToDoList.class), eq(1)))
                .thenThrow(new DataNotFoundException("listId"));

        mvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("No data was found for requested : listId"));
    }

    @Test
    public void ServerController_patchToDoListById_throwsUnexpectedParameterException() throws Exception {
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .patch("/rest/toDoLists/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"userId\": 1, \"label\": \"List of Groceries\", \"items\": []}");

        when(serverService.updateToDoList(any(ToDoList.class), eq(1)))
                .thenThrow(new UnexpectedParameterException("items"));

        mvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Unexpected request JSON key : items"));
    }

    @Test
    public void ServerController_patchToDoListById_throwsOverSizedStringProvidedException() throws Exception {
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .patch("/rest/toDoLists/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"userId\": 1, \"label\": \"1234567890123456789012345678901234567890123456\"}");

        mvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("String parameter is too long : label [max:45]"));
    }

    @Test
    public void ServerController_postItems_returnsCreatedItem() throws Exception {
        Item createdItem = new Item("Chocolate", false);
        createdItem.setItemId(1);
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post("/rest/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"listId\": 1, \"label\": \"Chocolate\", \"checked\": false }");

        when(serverService.createItem(any(RItem.class)))
                .thenReturn(createdItem);

        mvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.itemId").value(1))
                .andExpect(jsonPath("$.label").value("Chocolate"))
                .andExpect(jsonPath("$.checked").value(false));
    }

    @Test
    public void ServerController_postItems_throwsDataNotFoundException() throws Exception {
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post("/rest/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"listId\": 1, \"label\": \"Chocolate\", \"checked\": false }");

        when(serverService.createItem(any(RItem.class)))
                .thenThrow(new DataNotFoundException("listId"));

        mvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("No data was found for requested : listId"));
    }

    @Test
    public void ServerController_postItems_throwsOverSizedStringProvidedException() throws Exception {
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post("/rest/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"listId\": 1, \"label\": \"1234567890123456789012345678901234567890123456\", \"checked\": false }");

        mvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("String parameter is too long : label [max:45]"));
    }

    @Test
    public void ServerController_patchItemsById_returnsPatchedItem() throws Exception {
        Item patchedItem = new Item("Chocolate", true);
        patchedItem.setItemId(1);
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .patch("/rest/items/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"label\": \"Chocolate\", \"checked\": true }");

        when(serverService.updateItem(any(Item.class), eq(1)))
                .thenReturn(patchedItem);

        mvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId").value(1))
                .andExpect(jsonPath("$.label").value("Chocolate"))
                .andExpect(jsonPath("$.checked").value(true));
    }

    @Test
    public void ServerController_patchItemById_throwsUnexpectedParameterException() throws Exception {
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .patch("/rest/items/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"itemId\": 1, \"label\": \"Chocolate\", \"checked\": true }");

        mvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Unexpected request JSON key : itemId"));
    }

    @Test
    public void ServerController_patchItemById_throwsDataNotFoundException() throws Exception {
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .patch("/rest/items/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"label\": \"Chocolate\", \"checked\": true }");

        when(serverService.updateItem(any(Item.class), eq(1)))
                .thenThrow(new DataNotFoundException("listId"));

        mvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("No data was found for requested : listId"));
    }

    @Test
    public void ServerController_patchItemById_throwsOverSizedStringProvidedException() throws Exception {
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .patch("/rest/items/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"label\": \"1234567890123456789012345678901234567890123456\", \"checked\": true }");

        mvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("String parameter is too long : label [max:45]"));
    }
}

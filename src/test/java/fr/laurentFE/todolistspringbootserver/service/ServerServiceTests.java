package fr.laurentFE.todolistspringbootserver.service;

import fr.laurentFE.todolistspringbootserver.model.*;
import fr.laurentFE.todolistspringbootserver.model.exceptions.DataDuplicateException;
import fr.laurentFE.todolistspringbootserver.model.exceptions.DataNotFoundException;
import fr.laurentFE.todolistspringbootserver.repository.ItemRepository;
import fr.laurentFE.todolistspringbootserver.repository.ListNameRepository;
import fr.laurentFE.todolistspringbootserver.repository.UserListRepository;
import fr.laurentFE.todolistspringbootserver.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServerServiceTests {

    @InjectMocks
    private ServerService serverService;

    @Mock
    private UsersRepository usersRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ListNameRepository listNameRepository;
    @Mock
    private UserListRepository userListRepository;
    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void ServerService_findUser_ById_returnsUser() {
        Integer searchedUserId = 1;
        User user = new User(searchedUserId, "Archibald");

        when(usersRepository.findById(searchedUserId))
                .thenReturn(Optional.of(user));

        User foundUser = serverService.findUser(searchedUserId);

        assertNotNull(foundUser);
        assertEquals(searchedUserId, foundUser.getUserId());
        assertEquals("Archibald", foundUser.getUserName());
    }

    @Test
    public void ServerService_findUser_ById_throwsDataNotFoundException() {
        Integer searchedUserId = 1;
        Optional<User> op = Optional.empty();

        when(usersRepository.findById(searchedUserId))
                .thenReturn(op);

        DataNotFoundException e = assertThrows(
                DataNotFoundException.class,
                () -> serverService.findUser(searchedUserId));
        assertEquals("userId", e.getMessage());
    }

    @Test
    public void ServerService_findUser_ByUserName_returnsUser() {
        String searchedUserName = "Archibald";
        User user = new User(1, searchedUserName);

        when(usersRepository.findByUserName(searchedUserName))
                .thenReturn(Optional.of(user));

        User foundUser = serverService.findUser(searchedUserName);

        assertNotNull(foundUser);
        assertEquals(searchedUserName, foundUser.getUserName());
        assertEquals(1, foundUser.getUserId());
    }

    @Test
    public void ServerService_findUser_ByUserName_throwsDataNotFoundException() {
        String searchedUserName = "Archibald";
        Optional<User> op = Optional.empty();

        when(usersRepository.findByUserName(searchedUserName))
                .thenReturn(op);

        DataNotFoundException e = assertThrows(
                DataNotFoundException.class,
                () -> serverService.findUser(searchedUserName));
        assertEquals("userName", e.getMessage());
    }

    @Test
    public void ServerService_createUser_returnsUser() {
        String userName = "Archibald";
        User user = new User(null, userName);
        User savedUser = new User(1, userName);

        when(jdbcTemplate.query(
                any(String.class),
                any(MapSqlParameterSource.class),
                any(UserMapper.class)))
                .thenReturn(new ArrayList<>());
        when(usersRepository.save(user))
                .thenReturn(savedUser);

        User returnedUser = serverService.createUser(user);

        assertNotNull(returnedUser);
        assertEquals(userName, returnedUser.getUserName());
        assertEquals(savedUser.getUserId(), returnedUser.getUserId());
    }

    @Test
    public void ServerService_createUser_throwsDataDuplicateException() {
        String userName = "Archibald";
        User user = new User(null, userName);
        ArrayList<User> userArrayList = new ArrayList<>();
        userArrayList.add(new User(1, userName));

        when(jdbcTemplate.query(
                any(String.class),
                any(MapSqlParameterSource.class),
                any(UserMapper.class)))
                .thenReturn(userArrayList);

        DataDuplicateException e = assertThrows(
                DataDuplicateException.class,
                () -> serverService.createUser(user));
        assertEquals("userName", e.getMessage());
    }

    @Test
    public void ServerService_updateUser_returnsUser() {
        Integer updatedUserId = 1;
        User user = new User(null, "Archibald");
        User storedUser = new User(updatedUserId, "Balthazar");

        when(usersRepository.findById(updatedUserId))
                .thenReturn(Optional.of(storedUser));
        when(usersRepository.save(any(User.class)))
                .thenReturn(new User(updatedUserId, "Archibald"));

        User updatedUser = serverService.updateUser(user, updatedUserId);

        assertNotNull(updatedUser);
        assertEquals(updatedUserId, updatedUser.getUserId());
        assertEquals("Archibald", updatedUser.getUserName());
    }

    @Test
    public void ServerService_findSpecificToDoList_returnsToDoList() {
        String userName = "Archibald";
        User user = new User(null, userName);
        Integer listId = 1;
        Integer userId = 1;
        User storedUser = new User(userId, userName);
        UserList storedUserList = new UserList(listId, userId);
        String label = "Groceries";
        ListName storedListName = new ListName(listId, label);
        List<Map<String, Object>> storedListOfItems = new ArrayList<>();
        ToDoList expectedToDoList = new ToDoList(listId, userId, label, new ArrayList<>());

        when(userListRepository.findById(listId))
                .thenReturn(Optional.of(storedUserList));
        when(listNameRepository.findById(listId))
                .thenReturn(Optional.of(storedListName));
        when(jdbcTemplate.queryForList(
                any(String.class),
                any(MapSqlParameterSource.class)))
                .thenReturn(storedListOfItems);

        ToDoList returnedToDoList = serverService.findSpecificToDoList(listId);

        assertNotNull(returnedToDoList);
        assertEquals(expectedToDoList.getListId(), returnedToDoList.getListId());
        assertEquals(expectedToDoList.getUserId(), returnedToDoList.getUserId());
        assertEquals(expectedToDoList.getLabel(), returnedToDoList.getLabel());
        assertEquals(expectedToDoList.getItems().size(), returnedToDoList.getItems().size());
    }

    @Test
    public void ServerService_findAllToDoLists_returnsToDoListList() {
        String userName = "Archibald";
        Integer userId = 1;
        User user = new User(userId, userName);
        User storedUser = new User(userId, userName);
        UserList storedUserList1 = new UserList(1, userId);
        UserList storedUserList2 = new UserList(2, userId);
        ArrayList<UserList> userListList = new ArrayList<>();
        userListList.add(storedUserList1);
        userListList.add(storedUserList2);
        ListName storedListName1 = new ListName(1, "Groceries");
        ListName storedListName2 = new ListName(2, "Exercises");
        List<Map<String, Object>> storedListOfItems = new ArrayList<>();
        Item storedItem = new Item("Chocolate", true);
        storedItem.setItemId(1);
        HashMap<String, Object> storedItems = new HashMap<>();
        storedItems.put("item_id", 1);
        storedListOfItems.add(storedItems);

        when(usersRepository.findById(userId))
                .thenReturn(Optional.of(storedUser));
        when(userListRepository.findAllByUserId(storedUser.getUserId()))
                .thenReturn(Optional.of(userListList));
        when(userListRepository.findById(1))
                .thenReturn(Optional.of(storedUserList1));
        when(userListRepository.findById(2))
                .thenReturn(Optional.of(storedUserList2));
        when(listNameRepository.findById(1))
                .thenReturn(Optional.of(storedListName1));
        when(listNameRepository.findById(2))
                .thenReturn(Optional.of(storedListName2));
        when(jdbcTemplate.queryForList(
                any(String.class),
                any(MapSqlParameterSource.class)))
                .thenReturn(storedListOfItems);
        when(itemRepository.findById(1))
                .thenReturn(Optional.of(storedItem));

        ArrayList<ToDoList> returnedToDoLists = (ArrayList<ToDoList>) serverService.findAllToDoLists(user.getUserId());

        assertNotNull(returnedToDoLists);
        assertFalse(returnedToDoLists.isEmpty());
        assertEquals(2, returnedToDoLists.size());

        ToDoList firstToDoList = returnedToDoLists.getFirst();
        assertNotNull(firstToDoList);
        assertEquals("Groceries", firstToDoList.getLabel());
        assertEquals(1, firstToDoList.getListId());
        assertEquals(userId, firstToDoList.getUserId());
        assertFalse(firstToDoList.getItems().isEmpty());
        assertEquals(1, firstToDoList.getItems().size());
        assertEquals("Chocolate", firstToDoList.getItems().getFirst().getLabel());
        assertEquals(1, firstToDoList.getItems().getFirst().getItemId());
        assertTrue(firstToDoList.getItems().getFirst().isChecked());

        ToDoList secondToDoList = returnedToDoLists.get(1);
        assertNotNull(secondToDoList);
        assertEquals("Exercises", secondToDoList.getLabel());
        assertEquals(2, secondToDoList.getListId());
        assertEquals(userId, secondToDoList.getUserId());
        // secondToDoList.getItems() should return an empty list given the test data, but because of the way I mocked
        // the queryForList() call, firstToDoList's items will also be provided to secondToDoList, when in reality it
        // will not. So no assertions on the second ToDoList's items.
    }

    @Test
    public void ServerService_createToDoList_returnsToDoListWithThreeItems() {
        String userName = "Archibald";
        Integer userId = 1;
        User storedUser = new User(userId, userName);
        Integer listId = 1;
        String label = "Groceries";
        ArrayList<Item> itemsList = new ArrayList<>();
        ArrayList<Item> savedItemsList = new ArrayList<>();
        Item item1 = new Item("Chocolate", false);
        Item item2 = new Item("Milk", true);
        Item item3 = new Item("Cookies", false);
        Item savedItem1 = new Item("Chocolate", false);
        savedItem1.setItemId(1);
        Item savedItem2 = new Item("Milk", true);
        savedItem2.setItemId(2);
        Item savedItem3 = new Item("Cookies", false);
        savedItem3.setItemId(3);
        itemsList.add(item1);
        itemsList.add(item2);
        itemsList.add(item3);
        savedItemsList.add(savedItem1);
        savedItemsList.add(savedItem2);
        savedItemsList.add(savedItem3);
        List<Map<String, Object>> storedListOfItems = new ArrayList<>();
        HashMap<String, Object> storedItem1 = new HashMap<>();
        storedItem1.put("item_id", 1);
        storedListOfItems.add(storedItem1);
        HashMap<String, Object> storedItem2 = new HashMap<>();
        storedItem2.put("item_id", 2);
        storedListOfItems.add(storedItem2);
        HashMap<String, Object> storedItem3 = new HashMap<>();
        storedItem3.put("item_id", 3);
        storedListOfItems.add(storedItem3);
        UserList savedUserList = new UserList(listId, userId);
        ListName savedListName = new ListName(listId, label);
        UserList storedUserList = new UserList(listId, userId);
        ListName storedListName = new ListName(listId, label);
        ToDoList toDoList = new ToDoList(null, userId, label, itemsList);
        ToDoList savedToDoList = new ToDoList(listId, userId, label, savedItemsList);

        when(usersRepository.findById(userId))
                .thenReturn(Optional.of(storedUser));
        when(userListRepository.save(any(UserList.class)))
                .thenReturn(savedUserList);
        when(listNameRepository.save(any(ListName.class)))
                .thenReturn(savedListName);
        when(itemRepository.save(item1))
                .thenReturn(savedItem1);
        when(itemRepository.save(item2))
                .thenReturn(savedItem2);
        when(itemRepository.save(item3))
                .thenReturn(savedItem3);
        when(usersRepository.findByUserName(userName))
                .thenReturn(Optional.of(storedUser));
        when(jdbcTemplate.queryForObject(
                any(String.class),
                any(MapSqlParameterSource.class),
                any(Class.class)))
                .thenReturn(listId);
        when(userListRepository.findById(listId))
                .thenReturn(Optional.of(storedUserList));
        when(listNameRepository.findById(listId))
                .thenReturn(Optional.of(storedListName));
        when(jdbcTemplate.queryForList(
                any(String.class),
                any(MapSqlParameterSource.class)))
                .thenReturn(storedListOfItems);
        when(itemRepository.findById(1))
                .thenReturn(Optional.of(savedItem1));
        when(itemRepository.findById(2))
                .thenReturn(Optional.of(savedItem2));
        when(itemRepository.findById(3))
                .thenReturn(Optional.of(savedItem3));

        ToDoList returnedToDoList = serverService.createToDoList(toDoList);

        assertNotNull(returnedToDoList);
        assertEquals(savedToDoList.getListId(), returnedToDoList.getListId());
        assertEquals(savedToDoList.getUserId(), returnedToDoList.getUserId());
        assertEquals(savedToDoList.getLabel(), returnedToDoList.getLabel());
        assertNotNull(returnedToDoList.getItems());
        assertEquals(savedToDoList.getItems().size(), returnedToDoList.getItems().size());
        assertEquals(savedItem1.getItemId(), returnedToDoList.getItems().getFirst().getItemId());
        assertEquals(savedItem1.getLabel(), returnedToDoList.getItems().getFirst().getLabel());
        assertEquals(savedItem2.getItemId(), returnedToDoList.getItems().get(1).getItemId());
        assertEquals(savedItem2.getLabel(), returnedToDoList.getItems().get(1).getLabel());
        assertEquals(savedItem3.getItemId(), returnedToDoList.getItems().get(2).getItemId());
        assertEquals(savedItem3.getLabel(), returnedToDoList.getItems().get(2).getLabel());
    }

    @Test
    public void ServerService_updateToDoList_returnsUpdatedToDoList() {
        String userName = "Archibald";
        Integer userId = 1;
        User storedUser = new User(userId, userName);
        Integer listId = 1;
        String label = "Groceries";
        String storedLabel = "Grosseryz";
        UserList storedUserList = new UserList(listId, userId);
        ListName storedListName = new ListName(listId, label);
        List<Map<String, Object>> storedListOfItems = new ArrayList<>();
        ToDoList toDoList = new ToDoList(null, userId, label, null);
        ToDoList storedToDoList = new ToDoList(listId, userId, storedLabel, null);

        when(userListRepository.existsById(listId))
                .thenReturn(true);
        when(usersRepository.findById(userId))
                .thenReturn(Optional.of(storedUser));
        when(userListRepository.findById(listId))
                .thenReturn(Optional.of(storedUserList));
        when(listNameRepository.findById(listId))
                .thenReturn(Optional.of(storedListName));
        when(jdbcTemplate.queryForList(
                any(String.class),
                any(MapSqlParameterSource.class)))
                .thenReturn(storedListOfItems);

        ToDoList updatedToDoList = serverService.updateToDoList(toDoList, listId);

        assertNotNull(updatedToDoList);
        assertEquals(toDoList.getListId(), updatedToDoList.getListId());
        assertEquals(storedToDoList.getListId(), updatedToDoList.getListId());
        assertEquals(toDoList.getUserId(), updatedToDoList.getUserId());
        assertEquals(storedToDoList.getUserId(), updatedToDoList.getUserId());
        assertEquals(toDoList.getLabel(), updatedToDoList.getLabel());
        assertNotEquals(storedToDoList.getLabel(), updatedToDoList.getLabel());
        assertTrue(updatedToDoList.getItems().isEmpty());
    }

    @Test
    public void ServerService_updateToDoList_throwsDataNotFoundException() {
        Integer userId = 1;
        Integer listId = 1;
        String label = "Groceries";
        ToDoList toDoList = new ToDoList(null, userId, label, null);

        when(userListRepository.existsById(listId))
                .thenReturn(false);

        DataNotFoundException e = assertThrows(
                DataNotFoundException.class,
                () -> serverService.updateToDoList(toDoList, listId));

        assertEquals("listId", e.getMessage());
    }

    @Test
    public void ServerService_createItem_returnsCreatedItem() {
        Integer listId = 1;
        String itemName = "Chocolate";
        boolean itemChecked = true;
        RItem requestItem = new RItem();
        requestItem.setListId(listId);
        requestItem.setLabel(itemName);
        requestItem.setChecked(itemChecked);
        Item savedItem = new Item(itemName,itemChecked);
        savedItem.setItemId(1);

        when(userListRepository.existsById(requestItem.getListId()))
                .thenReturn(true);
        when(itemRepository.save(any(Item.class)))
                .thenReturn(savedItem);

        Item returnedItem = serverService.createItem(requestItem);

        assertNotNull(returnedItem);
        assertEquals(savedItem.getItemId(), returnedItem.getItemId());
        assertEquals(savedItem.getLabel(), returnedItem.getLabel());
        assertEquals(savedItem.isChecked(), returnedItem.isChecked());
    }

    @Test
    public void ServerService_createItem_throwsDataNotFoundException() {
        Integer listId = 1;
        String itemName = "Chocolate";
        boolean itemChecked = true;
        RItem requestItem = new RItem();
        requestItem.setListId(listId);
        requestItem.setLabel(itemName);
        requestItem.setChecked(itemChecked);

        when(userListRepository.existsById(requestItem.getListId()))
                .thenReturn(false);

        DataNotFoundException e = assertThrows(
                DataNotFoundException.class,
                () -> serverService.createItem(requestItem));

        assertEquals("listId", e.getMessage());
    }

    @Test
    public void ServerService_updateItem_returnsUpdatedItem() {
        Integer itemId = 1;
        String itemLabel = "Chocolate";
        boolean itemChecked = false;
        Item item = new Item(itemLabel, itemChecked);
        Item savedItem = new Item(itemLabel, itemChecked);
        savedItem.setItemId(itemId);

        when(itemRepository.existsById(itemId))
                .thenReturn(true);
        when(itemRepository.save(item))
                .thenReturn(savedItem);

        Item returnedItem = serverService.updateItem(item, itemId);

        assertNotNull(returnedItem);
        assertEquals(itemId, returnedItem.getItemId());
        assertEquals(itemLabel, returnedItem.getLabel());
        assertEquals(itemChecked, returnedItem.isChecked());
    }

    @Test
    public void ServerService_updateItem_throwsDataNotFoundException() {
        Integer itemId = 1;
        String itemLabel = "Chocolate";
        boolean itemChecked = false;
        Item item = new Item(itemLabel, itemChecked);

        when(itemRepository.existsById(itemId))
                .thenReturn(false);

        DataNotFoundException e = assertThrows(
                DataNotFoundException.class,
                () -> serverService.updateItem(item, itemId));
        assertEquals("itemId", e.getMessage());
    }
}

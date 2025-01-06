package fr.laurentFE.todolistspringbootserver.service;

import fr.laurentFE.todolistspringbootserver.model.*;
import fr.laurentFE.todolistspringbootserver.model.exceptions.*;
import fr.laurentFE.todolistspringbootserver.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* This class handles the business logic behind the API endpoints */
@Service
public class ServerService {

    Logger logger = LoggerFactory.getLogger(ServerService.class);

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ListNameRepository listNameRepository;
    @Autowired
    private UserListRepository userListRepository;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private boolean userNameExists(String user_name) {
        List<User> users = jdbcTemplate.query(
                "SELECT * FROM users WHERE user_name=:name",
                    new MapSqlParameterSource().addValue("name", user_name),
                    new UserMapper());
        return !users.isEmpty();
    }

    public Iterable<User> findAllUsers() {
        return usersRepository.findAll();
    }

    public User findUser(Integer id) {
        User user = usersRepository.findById(id).orElse(null);
        if (user == null) {
            throw new DataNotFoundException("userId");
        }
        return user;
    }

    public User findUser(String user_name) {
        User user = usersRepository.findByUserName(user_name).orElse(null);
        if (user == null) {
            throw new DataNotFoundException("userId");
        }
        return user;
    }

    public User createUser(User user) {
        if (userNameExists(user.getUserName())) {
            throw new DataDuplicateException("userName");
        }
        else if (user.getUserId() != null) {
            throw new UnexpectedParameterException("userId");
        }
        else {
            return usersRepository.save(user);
        }
    }

    public User updateUser(User user, Integer id) {
        if (user.getUserId() != null) {
            throw new UnexpectedParameterException("userId");
        }
        // Throws an exception if userId does not link to an existing user
        findUser(id);
        user.setUserId(id);
        return usersRepository.save(user);

    }

    private ListName getToDoListName(Integer listId) {
        ListName ln = listNameRepository.findById(listId).orElse(null);
        if (ln == null) {
            logger.error("No record in table 'list_names' for list_id='{}'", listId);
            throw new IncompleteDataSetException("user_id");
        }
        return ln;
    }

    private Item getItem(Integer itemId) {
        return itemRepository.findById(itemId).orElse(null);
    }

    private ArrayList<Item> getAllItems(Integer listId) {
        List<Map<String, Object>> itemsList = jdbcTemplate.queryForList(
                "SELECT item_id FROM list_items WHERE list_id=:listId",
                new MapSqlParameterSource().addValue("listId", listId));
        ArrayList<Item> items = new ArrayList<>();
        for (Map<String, Object> itemIdMap : itemsList) {
            Integer itemId = (Integer)itemIdMap.get("item_id");
            Item item = getItem(itemId);
            if (item == null) {
                logger.error("No record in table 'items' for item_id='{}'", itemId);
                throw new IncompleteDataSetException("userId");
            }
            items.add(item);
        }
        return items;
    }

    private Integer getListUserId(Integer listId) {
        UserList ul = userListRepository.findById(listId).orElse(null);
        if (ul == null) {
            logger.error("No record in table 'lists' for list_id='{}'", listId);
            throw new IncompleteDataSetException("list_id");
        }
        return ul.getUserId();
    }

    private ToDoList getFilledToDoList(Integer listId) {
        ToDoList res = new ToDoList();
        res.setListId(listId);
        // userId can't be null as if no userId is found, an exception is thrown
        Integer userId = getListUserId(listId);
        res.setUserId(userId);
        // label can't be null as if no label is found, an exception is thrown
        String label = getToDoListName(listId).getLabel();
        res.setLabel(label);
        ArrayList<Item> items = getAllItems(listId);
        res.setItems(items);
        return res;
    }

    public Iterable<ToDoList> findAllToDoLists(User user) {
        // dbUser cannot be null, as if no user is found from the given userName, an exception is thrown
        User dbUser = findUser(user.getUserName());
        ArrayList<ToDoList> tdl = new ArrayList<>();
        Iterable<UserList> userList = userListRepository.findAllByUserId(dbUser.getUserId()).orElse(null);
        if (userList != null) {
            for (UserList ul : userList) {
                ToDoList tmp = getFilledToDoList(ul.getListId());
                tdl.add(tmp);
            }
        }
        return tdl;
    }

    public ToDoList createToDoList(ToDoList toDoList) {
        if (toDoList.getListId() != null) {
            throw new UnexpectedParameterException("listId");
        }
        else {
            if(!toDoList.getItems().isEmpty()) {
                for (Item item : toDoList.getItems()) {
                    if (item.getItemId() != null) {
                        throw new UnexpectedParameterException("itemId");
                    }
                }
            }
            findUser(toDoList.getUserId());
            UserList ul = userListRepository.save(new UserList(toDoList.getListId(), toDoList.getUserId()));
            toDoList.setListId(ul.getListId());
            jdbcTemplate.update(
                    "INSERT INTO list_names (list_id, label) VALUES (:list_id, :label)",
                    new MapSqlParameterSource()
                            .addValue("list_id", toDoList.getListId())
                            .addValue("label", toDoList.getLabel()));
            if (!toDoList.getItems().isEmpty()) {
                for (Item item : toDoList.getItems()) {
                    if (item.getLabel() == null) {
                        throw new MissingParameterException("items[label]");
                    }
                    Item insertedItem = itemRepository.save(item);
                    jdbcTemplate.update(
                            "INSERT INTO list_items (list_id, item_id) VALUES (:list_id, :item_id)",
                            new MapSqlParameterSource()
                                    .addValue("list_id", toDoList.getListId())
                                    .addValue("item_id", insertedItem.getItemId()));
                }
            }
            return getFilledToDoList(toDoList.getListId());
        }
    }
}

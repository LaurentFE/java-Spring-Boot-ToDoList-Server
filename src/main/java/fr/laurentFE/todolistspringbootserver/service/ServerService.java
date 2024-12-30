package fr.laurentFE.todolistspringbootserver.service;

import fr.laurentFE.todolistspringbootserver.model.*;
import fr.laurentFE.todolistspringbootserver.model.exceptions.IncompleteDataSetException;
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
    private ListRepository listRepository;
    @Autowired
    private ToDoListRepository toDoListRepository;

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
        return usersRepository.findById(id).orElse(null);
    }

    public User findUser(String user_name) {
        return usersRepository.findByUserName(user_name).orElse(null);
    }

    public User createUser(User user) {
        if (userNameExists(user.getUserName())) {
            user.setUserId(-409);
            user.setUserName("user_name");
            return user;
        }
        else if (user.getUserId() != null) {
            user.setUserId(-400);
            user.setUserName("user_id");
            return user;
        }
        else {
            return usersRepository.save(user);
        }
    }

    public User updateUser(User user, Integer id) {
        if (user.getUserId() != null) {
            user.setUserId(-400);
            user.setUserName("user_id");
            return user;
        }
        User previous_user = findUser(id);
        if (previous_user == null) {
            user.setUserId(-404);
            user.setUserName("user_id");
            return user;
        }
        else {
            user.setUserId(id);
            return usersRepository.save(user);
        }
    }

    private ListName getToDoListName(Integer listId) {
        return listNameRepository.findById(listId).orElse(null);
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
                item = new Item();
                item.setItemId(-500);
                items.add(item);
                break;
            }
            items.add(item);
        }
        return items;
    }

    private ToDoList getFilledToDoList(Integer listId) {
        ToDoList res = new ToDoList();
        res.setListId(listId);
        String label = getToDoListName(listId).getLabel();
        if (label == null) {
            logger.error("No record in table 'list_names' for list_id='{}'", listId);
            res.setListId(-500);
        }
        res.setLabel(label);
        ArrayList<Item> items = getAllItems(listId);
        if (!items.isEmpty() && items.getFirst().getItemId() == -500) {
            // Error is already logged in getAllItems()
            res.setListId(-500);
        }
        res.setItems(items);
        return res;
    }

    public Iterable<ToDoList> findAllToDoLists(User user) {
        User dbUser = findUser(user.getUserName());
        ArrayList<ToDoList> tdl = new ArrayList<>();
        if (dbUser != null) {
            Iterable<UserList> userList = listRepository.findAllByUserId(dbUser.getUserId()).orElse(null);
            if (userList != null) {
                for (UserList ul : userList) {
                    ToDoList tmp = getFilledToDoList(ul.getListId());
                    if (tmp.getListId() == -500) {
                        // Errors are already logged in getFilledToDoList() and its calls
                        throw new IncompleteDataSetException("user_id");
                    }
                    tdl.add(tmp);
                }
            }
            return tdl;
        } else {
            ToDoList tmp = new ToDoList(-404, "user_id");
            ArrayList<ToDoList> err = new ArrayList<>();
            err.add(tmp);
            return err;
        }
    }
}

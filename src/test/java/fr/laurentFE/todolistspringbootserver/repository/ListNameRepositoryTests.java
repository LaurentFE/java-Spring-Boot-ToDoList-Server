package fr.laurentFE.todolistspringbootserver.repository;

import fr.laurentFE.todolistspringbootserver.model.ListName;
import fr.laurentFE.todolistspringbootserver.model.User;
import fr.laurentFE.todolistspringbootserver.model.UserList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

@SpringBootTest
public class ListNameRepositoryTests {
    @Autowired
    private UserListRepository userListRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ListNameRepository listNameRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    public void tearDown() {
        jdbcTemplate.execute("DELETE FROM `list_names`;");
        jdbcTemplate.execute("DELETE FROM `list_items`;");
        jdbcTemplate.execute("DELETE FROM `items`;");
        jdbcTemplate.execute("DELETE FROM `lists`;");
        jdbcTemplate.execute("DELETE FROM `users`;");
    }

    @Test
    public void ListNameRepository_Save_ReturnSavedListName() {
        // Arrange
        User user = new User("Archibald");
        User savedUser = usersRepository.save(user);
        UserList userList = new UserList(null, savedUser.getUserId());
        UserList savedUserList = userListRepository.save(userList);
        String label = "Groceries";
        ListName listName = new ListName(null, label);

        // Act
        ListName savedListName = listNameRepository.save(listName);

        // Assert
        Assert.notNull(savedListName, "savedListName must not be null");
        Assert.isTrue(savedListName.getListId().equals(savedUserList.getListId()), "savedListName.listId must be the same as savedUserList.listId");
        Assert.isTrue(savedListName.getLabel().equals(label), "savedUserList.label must be identical to '"+label+"'");
    }

    @Test
    public void ListNameRepository_FindById_ReturnFoundListName() {
        // Arrange
        User savedUser = usersRepository.save(new User("Archibald"));
        userListRepository.save(new UserList(null, savedUser.getUserId()));
        String label = "Groceries";
        ListName listName = new ListName(null, label);
        ListName savedListName = listNameRepository.save(listName);
        Integer searchedId = savedListName.getListId();

        // Act
        ListName foundListName = listNameRepository.findById(searchedId).orElse(null);

        // Assert
        Assert.notNull(foundListName, "foundListName must not be null");
        Assert.isTrue(foundListName.getListId().equals(searchedId), "foundListName.listId must be the same as searchedId");
        Assert.isTrue(foundListName.getLabel().equals(label), "savedUserList.label must be identical to '"+label+"'");
    }

    @Test
    public void ListNameRepository_Delete_ReturnNull() {
        // Arrange
        User savedUser = usersRepository.save(new User("Archibald"));
        userListRepository.save(new UserList(null, savedUser.getUserId()));
        ListName savedListName = listNameRepository.save(new ListName(null, "Groceries"));

        // Act
        listNameRepository.delete(savedListName);

        // Assert
        ListName foundListName = listNameRepository.findById(savedListName.getListId()).orElse(null);
        Assert.isNull(foundListName, "foundListName should be null");
    }
}

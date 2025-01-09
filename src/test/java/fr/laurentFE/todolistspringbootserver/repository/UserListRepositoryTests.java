package fr.laurentFE.todolistspringbootserver.repository;

import fr.laurentFE.todolistspringbootserver.model.User;
import fr.laurentFE.todolistspringbootserver.model.UserList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import java.util.List;

@SpringBootTest
public class UserListRepositoryTests {

    @Autowired
    private UserListRepository userListRepository;

    @Autowired
    private UsersRepository usersRepository;


    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    public void setup() {
        jdbcTemplate.execute("DELETE FROM `list_names`;");
        jdbcTemplate.execute("DELETE FROM `list_items`;");
        jdbcTemplate.execute("DELETE FROM `items`;");
        jdbcTemplate.execute("DELETE FROM `lists`;");
        jdbcTemplate.execute("DELETE FROM `users`;");
    }

    @Test
    public void UserListRepository_Save_ReturnSavedUserList() {
        // Arrange
        User user = new User("Archibald");
        User savedUser = usersRepository.save(user);
        UserList userList = new UserList(null, savedUser.getUserId());

        // Act
        UserList savedUserList = userListRepository.save(userList);

        // Assert
        Assert.notNull(savedUserList, "savedUserList must not be null");
        Assert.isTrue(savedUserList.getUserId().equals(user.getUserId()), "savedUserList.userId must be the same as user.userId");
    }

    @Test
    public void UserListRepository_ExistsById_ReturnTrue() {
        // Arrange
        User user = new User("Archibald");
        User savedUser = usersRepository.save(user);
        UserList userList = new UserList(null, savedUser.getUserId());
        UserList savedUserList = userListRepository.save(userList);

        // Act
        boolean exists = userListRepository.existsById(savedUserList.getListId());

        // Assert
        Assert.isTrue(exists, "exists must be true");
    }

    @Test
    public void UserListRepository_FindById_ReturnFoundUserList() {
        // Arrange
        User user = new User("Archibald");
        User savedUser = usersRepository.save(user);
        UserList userList = new UserList(null, savedUser.getUserId());
        UserList savedUserList = userListRepository.save(userList);
        Integer searchedId = savedUserList.getListId();

        // Act
        UserList foundUserList = userListRepository.findById(searchedId).orElse(null);

        // Assert
        Assert.notNull(foundUserList, "foundUserList must not be null");
        Assert.isTrue(foundUserList.getListId().equals(searchedId), "foundUserList.listId must be the same as searchedId");
        Assert.isTrue(foundUserList.getUserId().equals(savedUser.getUserId()), "foundUserList.userId must be the same as savedUser.userId");
    }

    @Test
    public void UserListRepository_FindAllByUserId_ReturnThree() {
        // Arrange
        User user = usersRepository.save(new User("Archibald"));
        userListRepository.save(new UserList(null, user.getUserId()));
        userListRepository.save(new UserList(null, user.getUserId()));
        userListRepository.save(new UserList(null, user.getUserId()));

        // Act
        List<UserList> UserListList = (List<UserList>) userListRepository.findAllByUserId(user.getUserId()).orElse(null);

        // Assert
        Assert.notNull(UserListList, "UserListList must not be null");
        Assert.isTrue(UserListList.size() == 3, "UserListList should contain three pairs (listId, userId)");
    }

    @Test
    public void UserListRepository_Delete_ReturnNull() {
        // Arrange
        User user = new User("Archibald");
        User savedUser = usersRepository.save(user);
        UserList userList = new UserList(null, savedUser.getUserId());
        UserList savedUserList = userListRepository.save(userList);

        // Act
        userListRepository.delete(savedUserList);

        // Assert
        UserList foundUserList = userListRepository.findById(savedUserList.getListId()).orElse(null);
        Assert.isNull(foundUserList, "foundUserList should be null");
    }


}

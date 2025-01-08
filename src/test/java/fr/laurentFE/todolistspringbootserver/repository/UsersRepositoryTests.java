package fr.laurentFE.todolistspringbootserver.repository;

import fr.laurentFE.todolistspringbootserver.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import java.util.List;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsersRepositoryTests {

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @AfterEach
    public void setup() {
        jdbcTemplate.execute("DELETE FROM `list_names`;");
        jdbcTemplate.execute("DELETE FROM `list_items`;");
        jdbcTemplate.execute("DELETE FROM `items`;");
        jdbcTemplate.execute("DELETE FROM `lists`;");
        jdbcTemplate.execute("DELETE FROM `users`;");
    }

    @Test
    public void UserRepository_Save_ReturnSavedUser() {
        // Arrange
        String userName = "Archibald";
        User user = new User(userName);

        // Act
        User savedUser = usersRepository.save(user);

        // Assert
        Assert.notNull(savedUser, "savedUser must not be null");
        Assert.isTrue(savedUser.getUserId() > 0, "savedUser.userId must be positive");
        Assert.isTrue(savedUser.getUserName().equals(userName), "savedItem.userName must be identical to '"+userName+"'");
    }

    @Test
    public void UserRepository_FindById_ReturnFoundUser() {
        // Arrange
        String userName = "Archibald";
        User user = new User(userName);
        User savedUser = usersRepository.save(user);
        Integer searchedId = savedUser.getUserId();

        // Act
        User foundUser = usersRepository.findById(searchedId).orElse(null);

        // Assert
        Assert.notNull(foundUser, "foundUser must not be null");
        Assert.isTrue(foundUser.getUserId().equals(searchedId), "foundUser.userId must be the same as searchedId");
        Assert.isTrue(foundUser.getUserName().equals(userName), "foundUser.userName must be identical to '"+userName+"'");
    }

    @Test
    public void UserRepository_FindByUserName_ReturnFoundUser() {
        // Arrange
        String userName = "Archibald";
        User user = new User(userName);
        User savedUser = usersRepository.save(user);

        // Act
        User foundUser = usersRepository.findByUserName(userName).orElse(null);

        // Assert
        Assert.notNull(foundUser, "foundUser must not be null");
        Assert.isTrue(foundUser.getUserId().equals(savedUser.getUserId()), "foundUser.userId must be the same as savedUser.userId");
        Assert.isTrue(foundUser.getUserName().equals(userName), "foundUser.userName must be identical to '"+userName+"'");
    }

    @Test
    public void UserRepository_FindAll_ReturnThree() {
        // Arrange
        usersRepository.save(new User("Archibald"));
        usersRepository.save(new User("Balthazar"));
        usersRepository.save(new User("Cornelius"));

        // Act
        List<User> usersList = (List<User>) usersRepository.findAll();

        // Assert
        Assert.notNull(usersList, "usersList should not be null");
        Assert.isTrue(usersList.size() == 3, "usersList should contain 3 Users");
    }
}

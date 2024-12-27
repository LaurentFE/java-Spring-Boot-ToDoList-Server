package fr.laurentFE.todolistspringbootserver.service;

import fr.laurentFE.todolistspringbootserver.model.User;
import fr.laurentFE.todolistspringbootserver.model.UserMapper;
import fr.laurentFE.todolistspringbootserver.repository.UsersRepository;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/* This class handles the business logic behind the API endpoints */
@Service
public class ServerService {

    @Autowired
    private UsersRepository usersRepository;

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

    public User createUser(User user) {
        if (userNameExists(user.getUser_name())) {
            user.setUser_id(-409);
            user.setUser_name("user_name");
            return user;
        }
        else if (user.getUser_id() != null) {
            user.setUser_id(-400);
            user.setUser_name("user_id");
            return user;
        }
        else {
            return usersRepository.save(user);
        }
    }

    public User updateUser(User user, Integer id) {
        if (user.getUser_id() != null) {
            user.setUser_id(-400);
            user.setUser_name("user_id");
            return user;
        }
        User previous_user = findUser(id);
        if (previous_user == null) {
            user.setUser_id(-404);
            user.setUser_name("user_id");
            return user;
        }
        else {
            user.setUser_id(id);
            return usersRepository.save(user);
        }
    }
}

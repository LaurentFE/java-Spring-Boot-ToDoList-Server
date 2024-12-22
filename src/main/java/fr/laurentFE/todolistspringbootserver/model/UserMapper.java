package fr.laurentFE.todolistspringbootserver.model;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements RowMapper<User> {

    public User mapRow(ResultSet resultSet, int i) throws SQLException {
        if (resultSet.isBeforeFirst()) {
            return null;
        }
        return new User(resultSet.getInt("user_id"), resultSet.getString("user_name"));
    }
}

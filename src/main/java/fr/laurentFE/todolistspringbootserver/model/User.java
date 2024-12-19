package fr.laurentFE.todolistspringbootserver.model;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("users")
public class User {
    @Id
    private final Integer user_id;
    @NotEmpty
    private String user_name;

    public User(Integer user_id, String user_name) {
        this.user_id = user_id;
        this.user_name = user_name;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}

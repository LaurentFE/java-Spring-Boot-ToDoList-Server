package fr.laurentFE.todolistspringbootserver.model;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("users")
public class User {

    @Id
    @Column("user_id")
    private Integer userId;
    @NotEmpty
    @Column("user_name")
    private String userName;

    public User() {
        this.userId = null;
        this.userName = null;
    }
    public User(String name) {
        this.userId = null;
        this.userName = name;
    }

    public User(Integer id, String name) {
        this.userId = id;
        this.userName = name;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}

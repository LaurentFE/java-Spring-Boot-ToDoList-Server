package fr.laurentFE.todolistspringbootserver.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("lists")
public class UserList {
    @Id
    @Column("list_id")
    private Integer listId;
    @Column("user_id")
    private Integer userId;

    public UserList() {
        listId = null;
        userId = null;
    }

    public UserList( Integer listId, Integer userId) {
        this.listId = listId;
        this.userId = userId;
    }

    public Integer getListId() {
        return listId;
    }

    public void setListId(Integer listId) {
        this.listId = listId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}

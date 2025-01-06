package fr.laurentFE.todolistspringbootserver.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;

public class ToDoList {
    @Id
    private Integer listId;
    @NotNull(message="This field cannot be empty")
    private Integer userId;
    @NotEmpty(message="This field cannot be empty")
    private String label;
    private ArrayList<Item> items;

    public ToDoList() {
        this.listId = null;
        this.userId = null;
        this.label = null;
        this.items = new ArrayList<>();
    }

    public ToDoList(Integer listId, Integer userId, String label, ArrayList<Item> items) {
        this.listId = listId;
        this.userId = userId;
        this.label = label;
        this.items = items;
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }
}

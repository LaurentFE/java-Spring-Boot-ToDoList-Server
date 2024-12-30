package fr.laurentFE.todolistspringbootserver.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.ArrayList;

@Table("lists")
public class ToDoList {
    @Id
    @Column("list_id")
    private Integer listId;
    private String label;
    private ArrayList<Item> items;

    public ToDoList() {
        this.listId = null;
        this.label = null;
        this.items = null;
    }

    public ToDoList(Integer listId, String label) {
        this.listId = listId;
        this.label = label;
        this.items = null;
    }

    public ToDoList(Integer listId, String label, ArrayList<Item> items) {
        this.listId = listId;
        this.label = label;
        this.items = items;
    }

    public Integer getListId() {
        return listId;
    }

    public void setListId(Integer listId) {
        this.listId = listId;
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

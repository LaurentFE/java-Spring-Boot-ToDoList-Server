package fr.laurentFE.todolistspringbootserver.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("list_names")
public class ListName {
    @Id
    @Column("list_id")
    private Integer listId;
    @Column("label")
    private String label;

    public ListName() {
        listId = null;
        label = null;
    }

    public ListName(Integer listId, String label) {
        this.listId = listId;
        this.label = label;
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
}

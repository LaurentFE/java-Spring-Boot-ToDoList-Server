package fr.laurentFE.todolistspringbootserver.model;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("list_items")
public class ListItem {
    @Id
    private final Integer item_id;
    @NotEmpty
    private String label;
    @NotEmpty
    private Boolean checked;

    public ListItem(Integer item_id, String label, Boolean checked) {
        this.item_id = item_id;
        this.label = label;
        this.checked = checked;
    }

    public Integer getItem_id() {
        return item_id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }
}

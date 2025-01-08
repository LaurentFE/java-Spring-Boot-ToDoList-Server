package fr.laurentFE.todolistspringbootserver.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("items")
public class Item {
    @Id
    @Column("item_id")
    private Integer itemId;
    @NotEmpty(message="This field cannot be empty")
    @Column("label")
    private String label;
    @NotNull(message="This field cannot be empty")
    @Column("is_checked")
    private Boolean checked;

    public Item() {
        itemId = null;
        label = null;
        checked = null;
    }

    public Item(String label, boolean checked) {
        itemId = null;
        this.label = label;
        this.checked = checked;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Boolean isChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }
}

package fr.laurentFE.todolistspringbootserver.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class RItem {
    @NotNull(message="This field cannot be empty")
    private Integer listId;
    @NotEmpty(message="This field cannot be empty")
    private String label;
    @NotNull(message="This field cannot be empty")
    private Boolean checked;

    public RItem() {
        listId = null;
        label = null;
        checked = null;
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

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}

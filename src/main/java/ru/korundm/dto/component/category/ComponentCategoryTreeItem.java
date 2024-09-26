package ru.korundm.dto.component.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public final class ComponentCategoryTreeItem implements Serializable {

    private Long id; // идентификатор
    private String name; // название
    private String description; // описание
    @JsonProperty("_children")
    private List<ComponentCategoryTreeItem> childrenList; // список категорий
}
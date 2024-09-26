package ru.korundm.dto.corporateDocumentCategory;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CorporateDocumentCategoryTreeItem implements Serializable {

    private Long id; // идентификатор
    private String name; // название
    private String description; // описание
    private int sort; // сортировка
    @JsonProperty("_children")
    private List<CorporateDocumentCategoryTreeItem> childrenList; // список категорий
}
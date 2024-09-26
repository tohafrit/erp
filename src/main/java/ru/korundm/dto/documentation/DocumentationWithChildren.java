package ru.korundm.dto.documentation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler"})
public final class DocumentationWithChildren implements Serializable {

    private Long id; // идентификатор документации
    private String name; // название документации
    private String content; // текст документации
    @JsonProperty("_children")
    private List<DocumentationWithChildren> childrenList; // список документаций
    private String menuName; // наименование пункта меню
}
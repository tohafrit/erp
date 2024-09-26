package ru.korundm.dto.documentation;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class DocumentationSearchResult implements Serializable {

    Long id;
    String name;
    String highlightedContent;
}
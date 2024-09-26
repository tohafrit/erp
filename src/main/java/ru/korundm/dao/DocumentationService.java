package ru.korundm.dao;

import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import ru.korundm.dto.documentation.DocumentationSearchResult;
import ru.korundm.entity.Documentation;

import java.io.IOException;
import java.util.List;

public interface DocumentationService extends CommonService<Documentation> {

    List<Documentation> getParentDocumentationList();

    List<Long> getAllSiblingsIdByParentId(Long parentId);

    Documentation getByName(String name);

    List<DocumentationSearchResult> getByContent(String text) throws IOException, InvalidTokenOffsetsException;

    List<Documentation> recursiveGetAll(List<Documentation> documentations);

    Documentation getPrevious(List<Documentation> documentationList, Long id);

    Documentation getNext(List<Documentation> documentationList, Long id);

    List<Documentation> getListForBreadcrumbs(Long id);

    boolean existsById(Long id);
}
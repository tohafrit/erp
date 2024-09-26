package ru.korundm.dao.impl;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.*;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.DocumentationService;
import ru.korundm.dto.documentation.DocumentationSearchResult;
import ru.korundm.entity.Documentation;
import ru.korundm.entity.Documentation_;
import ru.korundm.repository.DocumentationRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class DocumentationServiceImpl implements DocumentationService {

    @PersistenceContext
    private EntityManager entityManager;

    private final DocumentationRepository documentationRepository;

    public DocumentationServiceImpl(DocumentationRepository documentationRepository) {
        this.documentationRepository = documentationRepository;
    }

    @Override
    public List<Documentation> getAll() {
        return documentationRepository.findAll();
    }

    @Override
    public List<Documentation> getAllById(List<Long> idList) {
        return documentationRepository.findAllById(idList);
    }

    @Override
    public Documentation save(Documentation object) {
        return documentationRepository.save(object);
    }

    @Override
    public List<Documentation> saveAll(List<Documentation> objectList) {
        return documentationRepository.saveAll(objectList);
    }

    @Override
    public Documentation read(long id) {
        return documentationRepository.findById(id).orElse(null);
    }

    @Override
    public boolean existsById(Long id) {
        return id != null && documentationRepository.existsById(id);
    }

    @Override
    public void delete(Documentation object) {
        documentationRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        documentationRepository.deleteById(id);
    }

    @Override
    public List<Documentation> getParentDocumentationList() {
        return documentationRepository.findAllByParentIsNull();
    }

    @Override
    public List<Long> getAllSiblingsIdByParentId(Long parentId) {
        return documentationRepository.findAllSiblingsIdByParentId(parentId);
    }

    @Override
    public Documentation getByName(String name) {
        return documentationRepository.findFirstByName(name);
    }

    @Override
    public List<DocumentationSearchResult> getByContent(String text) throws IOException, InvalidTokenOffsetsException {
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Documentation.class).get();
        Analyzer analyzer = fullTextEntityManager.getSearchFactory().getAnalyzer("customAnalyzer");//.getAnalyzer(Documentation.class);
        if (tokenizeString(analyzer, text).isEmpty()) {
            return new ArrayList<>();
        }
        Query query = queryBuilder.keyword().onFields(Documentation_.CONTENT, Documentation_.NAME).matching(text).createQuery();
        FullTextQuery jpaQuery = fullTextEntityManager.createFullTextQuery(query, Documentation.class);
        @SuppressWarnings("unchecked")
        List<Documentation> documentationList = jpaQuery.getResultList();
        List<DocumentationSearchResult> documentationSearchResultList = new ArrayList<>();
        QueryScorer scorer = new QueryScorer(query);
        Highlighter highlighter = new Highlighter(
            new SimpleHTMLFormatter("<strong class=\"documentation__content_text_keyword\">", "</strong>"),
            scorer
        );
        highlighter.setTextFragmenter(new SimpleSpanFragmenter(scorer, 300));
        for (Documentation documentation: documentationList) {
            DocumentationSearchResult documentationSearchResult = new DocumentationSearchResult();
            documentationSearchResult.setId(documentation.getId());
            documentationSearchResult.setName(documentation.getName());
            if (documentation.getContent() != null) {
                documentationSearchResult.setHighlightedContent(
                    highlighter.getBestFragment(new RussianAnalyzer(), null, documentation.getContent())
                );
            }
            documentationSearchResultList.add(documentationSearchResult);
        }
        return documentationSearchResultList;
    }

    private static List<String> tokenizeString(Analyzer analyzer, String string) throws IOException {
        List<String> result = new ArrayList<>();
        TokenStream stream = analyzer.tokenStream(null, new StringReader(string));
        stream.reset();
        while (stream.incrementToken()) {
            result.add(stream.getAttribute(CharTermAttribute.class).toString());
        }
        stream.close();
        return result;
    }

    @Override
    public List<Documentation> recursiveGetAll(List<Documentation> documentations) {
        List<Documentation> result = new ArrayList<>();
        for (Documentation documentation : documentations) {
            result.add(documentation);
            List<Documentation> childList = documentation.getChildList();
            if (childList != null) {
                for (Documentation child : childList) {
                    result.add(child);
                    recursiveGetAll(childList);
                }
            }
        }
        return result;
    }

    @Override
    public Documentation getPrevious(List<Documentation> documentationList, Long id) {
        Documentation result = null;
        for (Documentation item : documentationList) {
            if (Objects.equals(item.getId(), id)) {
                int index = documentationList.indexOf(item);
                result = index == 0 ? null : documentationList.get(index - 1);
                break;
            }
        }
        return result;
    }

    @Override
    public Documentation getNext(List<Documentation> documentationList, Long id) {
        Documentation result = null;
        for (Documentation item : documentationList) {
            if (Objects.equals(item.getId(), id)) {
                int index = documentationList.indexOf(item);
                result = index == documentationList.size() - 1 ? null : documentationList.get(index + 1);
                break;
            }
        }
        return result;
    }

    @Override
    public List<Documentation> getListForBreadcrumbs(Long id) {
        List<Documentation> result = new ArrayList<>();
        Documentation documentation = read(id);
        if (documentation == null) {
            return result;
        }
        Documentation parent = documentation.getParent();
        while (parent != null) {
            result.add(parent);
            parent = parent.getParent();
        }
        Collections.reverse(result);
        return result;
    }
}
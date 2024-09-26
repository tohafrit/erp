package ru.korundm.schedule.importation.process;

import eco.dao.EcoDocumentService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.DocumentService;
import ru.korundm.entity.Document;
import ru.korundm.entity.User;
import ru.korundm.constant.BaseConstant;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Процесс переноса ECOPLAN.DOCUMENT
 * @author zhestkov_an
 * Date:   30.03.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class DocumentProcess {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private EcoDocumentService ecoDocumentService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void schedule() {
        em.createNativeQuery(BaseConstant.SQL_DISABLE_FOREIGN_KEY_CHECKS).executeUpdate();
        em.createQuery("DELETE FROM Document").executeUpdate();
        List<Document> documentList = ecoDocumentService.getAll().stream().map(ecoDocument -> {
            Document document = new Document();
            document.setId(ecoDocument.getId());
            document.setParentId(ecoDocument.getId());
            document.setDocName(ecoDocument.getDocName());
            document.setNote(ecoDocument.getDocNote());
            document.setFileName(ecoDocument.getFileName());
            document.setFileType(ecoDocument.getFileType());
            document.setOrderIndex(ecoDocument.getOrderIndex());
            document.setModifyDate(ecoDocument.getModifyDate());
            if (ecoDocument.getModifyUser() != null) {
                User user = new User();
                user.setId(ecoDocument.getModifyUser().getId());
                document.setModifyUser(user);
            }
            return document;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(documentList)) {
            documentService.saveAll(documentList);
        }
        em.createNativeQuery(BaseConstant.SQL_ENABLE_FOREIGN_KEY_CHECKS).executeUpdate();
    }
}
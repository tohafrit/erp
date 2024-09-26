package ru.korundm.schedule.importation.process;

import eco.dao.EcoBomAttributeService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.BomAttributeService;
import ru.korundm.entity.Bom;
import ru.korundm.entity.BomAttribute;
import ru.korundm.entity.Launch;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static ru.korundm.constant.BaseConstant.SQL_DISABLE_FOREIGN_KEY_CHECKS;
import static ru.korundm.constant.BaseConstant.SQL_ENABLE_FOREIGN_KEY_CHECKS;

/**
 * Процесс переноса ECOPLAN.BOM_ATTRIBUTE
 * @author zhestkov_an
 * Date:   12.04.2020
 */
@Component
@Scope(SCOPE_SINGLETON)
public class BomAttributeProcess {

    private static final List<Long> excludeIdList = List.of(576116L, 5076911L, 5985790L);

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private BomAttributeService bomAttributeService;

    @Autowired
    private EcoBomAttributeService ecoBomAttributeService;

    @Transactional(propagation = REQUIRES_NEW)
    public void schedule() {
        em.createNativeQuery(SQL_DISABLE_FOREIGN_KEY_CHECKS).executeUpdate();
        em.createQuery("DELETE FROM BomAttribute").executeUpdate();
        List<BomAttribute> bomAttributeList = ecoBomAttributeService.getAll().stream()
            .filter(ecoBomAttribute -> !excludeIdList.contains(ecoBomAttribute.getBom().getId())).map(ecoBomAttribute -> {
            BomAttribute bomAttribute = new BomAttribute();
            bomAttribute.setId(ecoBomAttribute.getId());
            if (ecoBomAttribute.getLaunch() != null) {
                bomAttribute.setLaunch(new Launch(ecoBomAttribute.getLaunch().getId()));
            }
            if (ecoBomAttribute.getBom() != null) {
                Bom bom = new Bom();
                bom.setId(ecoBomAttribute.getBom().getId());
                bomAttribute.setBom(bom);
            }
            bomAttribute.setApproveDate(ecoBomAttribute.getApproveDate());
            bomAttribute.setAcceptDate(ecoBomAttribute.getAcceptDate());
            /*if (ecoBomAttribute.getDocument() != null) {
                Document document = new Document();
                document.setId(ecoBomAttribute.getDocument().getId());
                bomAttribute.setDocument(document);
            }*/
            return bomAttribute;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(bomAttributeList)) {
            bomAttributeService.saveAll(bomAttributeList);
        }
        em.createNativeQuery(SQL_ENABLE_FOREIGN_KEY_CHECKS).executeUpdate();
    }
}
package ru.korundm.schedule.importation.process;

import eco.dao.EcoLabourService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.LabourService;
import ru.korundm.entity.Company;
import ru.korundm.entity.Labour;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static ru.korundm.constant.BaseConstant.SQL_DISABLE_FOREIGN_KEY_CHECKS;
import static ru.korundm.constant.BaseConstant.SQL_ENABLE_FOREIGN_KEY_CHECKS;

/**
 * Процесс переноса ECOPLAN.LABOUR
 * @author zhestkov_an
 * Date:   01.05.2020
 */
@Component
@Scope(SCOPE_SINGLETON)
public class LabourProcess {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private LabourService labourService;

    @Autowired
    private EcoLabourService ecoLabourService;

    @Transactional(propagation = REQUIRES_NEW)
    public void schedule() {
        em.createNativeQuery(SQL_DISABLE_FOREIGN_KEY_CHECKS).executeUpdate();
        em.createQuery("DELETE FROM Labour").executeUpdate();
        List<Labour> labourList = ecoLabourService.getAll().stream().map(ecoLabour -> {
            Labour labour = new Labour();
            labour.setId(ecoLabour.getId());
            labour.setName(ecoLabour.getLabourName());
            if (ecoLabour.getCompany() != null) {
                labour.setCompany(new Company(ecoLabour.getCompany().getId()));
            }
            labour.setSubtraction(ecoLabour.getSubtraction());
            return labour;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(labourList)) {
            labourService.saveAll(labourList);
        }
        em.createNativeQuery(SQL_ENABLE_FOREIGN_KEY_CHECKS).executeUpdate();
    }
}
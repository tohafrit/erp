package ru.korundm.schedule.importation.process;

import eco.dao.EcoIdentifierService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.GovernmentContractService;
import ru.korundm.entity.GovernmentContract;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static ru.korundm.constant.BaseConstant.SQL_DISABLE_FOREIGN_KEY_CHECKS;
import static ru.korundm.constant.BaseConstant.SQL_ENABLE_FOREIGN_KEY_CHECKS;

/**
 * Процесс переноса ECOPLAN.IDS
 * @author zhestkov_an
 * Date:   08.07.2021
 */
@Component
@Scope(SCOPE_SINGLETON)
public class GovernmentContractProcess {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private GovernmentContractService governmentContractService;

    @Autowired
    private EcoIdentifierService ecoIdentifierService;

    @Transactional(propagation = REQUIRES_NEW)
    public void schedule() {
        em.createNativeQuery(SQL_DISABLE_FOREIGN_KEY_CHECKS).executeUpdate();
        em.createQuery("DELETE FROM GovernmentContract").executeUpdate();
        List<GovernmentContract> governmentContractList = ecoIdentifierService.getAll().stream().map(ecoIdentifier -> {
            GovernmentContract govContract = new GovernmentContract();
            govContract.setId(ecoIdentifier.getId());
            govContract.setIdentifier(ecoIdentifier.getNumber());
            LocalDateTime contractDate = ecoIdentifier.getContractDate();
            if (contractDate != null) {
                govContract.setDate(contractDate.toLocalDate());
            }
            return govContract;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(governmentContractList)) {
            governmentContractService.saveAll(governmentContractList);
        }
        em.createNativeQuery(SQL_ENABLE_FOREIGN_KEY_CHECKS).executeUpdate();
    }
}

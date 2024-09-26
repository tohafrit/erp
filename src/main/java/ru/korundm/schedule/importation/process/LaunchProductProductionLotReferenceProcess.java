package ru.korundm.schedule.importation.process;

import eco.dao.EcoLaunchProductProductionLotReferenceService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.LaunchProductProductionLotReferenceService;
import ru.korundm.entity.LaunchProduct;
import ru.korundm.entity.LaunchProductProductionLotReference;
import ru.korundm.entity.ProductionLot;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static ru.korundm.constant.BaseConstant.SQL_DISABLE_FOREIGN_KEY_CHECKS;
import static ru.korundm.constant.BaseConstant.SQL_ENABLE_FOREIGN_KEY_CHECKS;

/**
 * Процесс переноса ECOPLAN.R_LP_PL_USAGE
 * @author zhestkov_an
 * Date:   14.04.2020
 */
@Component
@Scope(SCOPE_SINGLETON)
public class LaunchProductProductionLotReferenceProcess {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private LaunchProductProductionLotReferenceService launchProductProductionLotReferenceService;

    @Autowired
    private EcoLaunchProductProductionLotReferenceService ecoLaunchProductProductionLotReferenceService;

    @Transactional(propagation = REQUIRES_NEW)
    public void schedule() {
        em.createNativeQuery(SQL_DISABLE_FOREIGN_KEY_CHECKS).executeUpdate();
        em.createQuery("DELETE FROM LaunchProductProductionLotReference").executeUpdate();
        List<LaunchProductProductionLotReference> lpPlrList = ecoLaunchProductProductionLotReferenceService.getAll().stream().map(ecoLpPlr -> {
            LaunchProductProductionLotReference lpPlr = new LaunchProductProductionLotReference();
            lpPlr.setOrderIndex(ecoLpPlr.getOrderIndex());
            lpPlr.setAmount(ecoLpPlr.getAmount());
            if (ecoLpPlr.getProductionLot() != null) {
                ProductionLot productionLot = new ProductionLot();
                productionLot.setId(ecoLpPlr.getProductionLot().getId());
                lpPlr.setProductionLot(productionLot);
            }
            if (ecoLpPlr.getLaunchProduct() != null) {
                lpPlr.setLaunchProduct(new LaunchProduct(ecoLpPlr.getLaunchProduct().getId()));
            }
            return lpPlr;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(lpPlrList)) {
            launchProductProductionLotReferenceService.saveAll(lpPlrList);
        }
        em.createNativeQuery(SQL_ENABLE_FOREIGN_KEY_CHECKS).executeUpdate();
    }
}
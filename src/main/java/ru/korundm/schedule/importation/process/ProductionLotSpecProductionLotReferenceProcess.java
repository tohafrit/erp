package ru.korundm.schedule.importation.process;

import eco.dao.EcoProductionLotSpecProductionLotReferenceService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.ProductionLotSpecProductionLotReferenceService;
import ru.korundm.entity.ProductionLot;
import ru.korundm.entity.ProductionLotSpec;
import ru.korundm.entity.ProductionLotSpecProductionLotReference;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static ru.korundm.constant.BaseConstant.SQL_DISABLE_FOREIGN_KEY_CHECKS;
import static ru.korundm.constant.BaseConstant.SQL_ENABLE_FOREIGN_KEY_CHECKS;

/**
 * Процесс переноса ECOPLAN.R_PL_PL_USAGE
 * @author zhestkov_an
 * Date:   14.04.2020
 */
@Component
@Scope(SCOPE_SINGLETON)
public class ProductionLotSpecProductionLotReferenceProcess {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ProductionLotSpecProductionLotReferenceService productionLotSpecProductionLotReferenceService;

    @Autowired
    private EcoProductionLotSpecProductionLotReferenceService ecoProductionLotSpecProductionLotReferenceService;

    @Transactional(propagation = REQUIRES_NEW)
    public void schedule() {
        em.createNativeQuery(SQL_DISABLE_FOREIGN_KEY_CHECKS).executeUpdate();
        em.createQuery("DELETE FROM ProductionLotSpecProductionLotReference").executeUpdate();
        List<ProductionLotSpecProductionLotReference> plsPlrList = ecoProductionLotSpecProductionLotReferenceService.getAll().stream().map(ecoPlsPlr -> {
            ProductionLotSpecProductionLotReference plsPlr = new ProductionLotSpecProductionLotReference();
            plsPlr.setOrderIndex(ecoPlsPlr.getOrderIndex());
            plsPlr.setAmount(ecoPlsPlr.getAmount());
            if (ecoPlsPlr.getProductionLot() != null) {
                ProductionLot productionLot = new ProductionLot();
                productionLot.setId(ecoPlsPlr.getProductionLot().getId());
                plsPlr.setProductionLot(productionLot);
            }
            if (ecoPlsPlr.getProductionLotSpec() != null) {
                ProductionLotSpec productionLotSpec = new ProductionLotSpec();
                productionLotSpec.setId(ecoPlsPlr.getProductionLotSpec().getId());
                plsPlr.setProductionLotSpec(productionLotSpec);
            }
            return plsPlr;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(plsPlrList)) {
            productionLotSpecProductionLotReferenceService.saveAll(plsPlrList);
        }
        em.createNativeQuery(SQL_ENABLE_FOREIGN_KEY_CHECKS).executeUpdate();
    }
}
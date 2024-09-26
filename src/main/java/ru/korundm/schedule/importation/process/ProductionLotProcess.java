package ru.korundm.schedule.importation.process;

import eco.dao.EcoProductionLotService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.ProductionLotService;
import ru.korundm.entity.Bom;
import ru.korundm.entity.LaunchProduct;
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
 * Процесс переноса ECOPLAN.PRODUCTION_LOT
 * @author zhestkov_an
 * Date:   12.04.2020
 */
@Component
@Scope(SCOPE_SINGLETON)
public class ProductionLotProcess {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ProductionLotService productionLotService;

    @Autowired
    private EcoProductionLotService ecoProductionLotService;

    @Transactional(propagation = REQUIRES_NEW)
    public void schedule() {
        em.createNativeQuery(SQL_DISABLE_FOREIGN_KEY_CHECKS).executeUpdate();
        em.createQuery("DELETE FROM ProductionLot").executeUpdate();
        List<ProductionLot> productionLots = ecoProductionLotService.getAll().stream().map(ecoProductionLot -> {
            ProductionLot productionLot = new ProductionLot();
            productionLot.setId(ecoProductionLot.getId());
            productionLot.setAmount(ecoProductionLot.getAmount());
            productionLot.setNote(ecoProductionLot.getNote());
            productionLot.setOrderIndex(ecoProductionLot.getOrderIndex());
            productionLot.setMonthlyScheduled(ecoProductionLot.getMonthlyScheduled() == null ? 0 : ecoProductionLot.getMonthlyScheduled().intValue());
            if (ecoProductionLot.getLaunchProduct() != null) {
                LaunchProduct launchProduct = new LaunchProduct();
                launchProduct.setId(ecoProductionLot.getLaunchProduct().getId());
                productionLot.setLaunchProduct(launchProduct);
            }
            if (ecoProductionLot.getBom() != null) {
                Bom bom = new Bom();
                bom.setId(ecoProductionLot.getBom().getId());
                productionLot.setBom(bom);
            }
            return productionLot;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(productionLots)) {
            productionLotService.saveAll(productionLots);
        }
        em.createNativeQuery(SQL_ENABLE_FOREIGN_KEY_CHECKS).executeUpdate();
    }
}
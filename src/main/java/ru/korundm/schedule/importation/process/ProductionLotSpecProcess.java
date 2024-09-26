package ru.korundm.schedule.importation.process;

import eco.dao.EcoProductionLotSpecService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.ProductionLotSpecService;
import ru.korundm.entity.Product;
import ru.korundm.entity.ProductionLot;
import ru.korundm.entity.ProductionLotSpec;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static ru.korundm.constant.BaseConstant.SQL_DISABLE_FOREIGN_KEY_CHECKS;
import static ru.korundm.constant.BaseConstant.SQL_ENABLE_FOREIGN_KEY_CHECKS;

/**
 * Процесс переноса ECOPLAN.PRODUCTION_LOT_SPEC
 * @author zhestkov_an
 * Date:   12.04.2020
 */
@Component
@Scope(SCOPE_SINGLETON)
public class ProductionLotSpecProcess {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ProductionLotSpecService productionLotSpecService;

    @Autowired
    private EcoProductionLotSpecService ecoProductionLotSpecService;

    @Transactional(propagation = REQUIRES_NEW)
    public void schedule() {
        em.createNativeQuery(SQL_DISABLE_FOREIGN_KEY_CHECKS).executeUpdate();
        em.createQuery("DELETE FROM ProductionLotSpec").executeUpdate();
        List<ProductionLotSpec> productionLotSpecList = ecoProductionLotSpecService.getAll().stream().map(ecoProductionLotSpec -> {
            ProductionLotSpec productionLotSpec = new ProductionLotSpec();
            productionLotSpec.setId(ecoProductionLotSpec.getId());
            productionLotSpec.setSubProductAmount(ecoProductionLotSpec.getSubProductAmount());
            productionLotSpec.setOrderIndex(ecoProductionLotSpec.getOrderIndex());
            if (ecoProductionLotSpec.getProductionLot() != null) {
                ProductionLot productionLot = new ProductionLot();
                productionLot.setId(ecoProductionLotSpec.getProductionLot().getId());
                productionLotSpec.setProductionLot(productionLot);
            }
            if (ecoProductionLotSpec.getSubProduct() != null) {
                productionLotSpec.setSubProduct(new Product(ecoProductionLotSpec.getSubProduct().getId()));
            }
            return productionLotSpec;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(productionLotSpecList)) {
            productionLotSpecService.saveAll(productionLotSpecList);
        }
        em.createNativeQuery(SQL_ENABLE_FOREIGN_KEY_CHECKS).executeUpdate();
    }
}
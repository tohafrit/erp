package ru.korundm.schedule.importation.process;

import eco.dao.EcoBomSpecItemService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.BomSpecItemService;
import ru.korundm.entity.Bom;
import ru.korundm.entity.BomSpecItem;
import ru.korundm.entity.Product;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static ru.korundm.constant.BaseConstant.SQL_DISABLE_FOREIGN_KEY_CHECKS;
import static ru.korundm.constant.BaseConstant.SQL_ENABLE_FOREIGN_KEY_CHECKS;

/**
 * Процесс заполнения таблицы bom_spec_items
 * @author mazur_ea
 * Date:   18.05.2020
 */
@Component
@Scope(SCOPE_SINGLETON)
public class BomSpecItemProcess {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private BomSpecItemService bomSpecItemService;

    @Autowired
    private EcoBomSpecItemService ecoBomSpecItemService;

    @Transactional(propagation = REQUIRES_NEW)
    public void schedule() {
        em.createNativeQuery(SQL_DISABLE_FOREIGN_KEY_CHECKS).executeUpdate();
        em.createQuery("DELETE FROM BomSpecItem").executeUpdate();
        List<BomSpecItem> bomSpecItemList = ecoBomSpecItemService.getAll().stream().map(ecoItem -> {
            BomSpecItem item = new BomSpecItem();
            item.setId(ecoItem.getId());
            //
            Bom bom = new Bom();
            bom.setId(ecoItem.getBom().getId());
            item.setBom(bom);
            //
            Product product = new Product();
            product.setId(ecoItem.getProduct().getId());
            item.setProduct(product);
            //
            item.setQuantity(ecoItem.getSubProductCount().intValue());
            //item.setProducer(ecoItem.getContractor());
            return item;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(bomSpecItemList)) {
            bomSpecItemService.saveAll(bomSpecItemList);
        }
        em.createNativeQuery(SQL_ENABLE_FOREIGN_KEY_CHECKS).executeUpdate();
    }
}
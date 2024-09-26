package ru.korundm.schedule.importation.process;

import eco.dao.EcoSapsanProductService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.SapsanProductService;
import ru.korundm.entity.SapsanProduct;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static ru.korundm.constant.BaseConstant.SQL_DISABLE_FOREIGN_KEY_CHECKS;
import static ru.korundm.constant.BaseConstant.SQL_ENABLE_FOREIGN_KEY_CHECKS;

/**
 * Процесс переноса ECOPLAN.SAPSAN_PRODUCT
 *
 * @author berezin_mm
 * Date:   29.06.2020
 */
@Component
@Scope(SCOPE_SINGLETON)
public class SapsanProductProcess {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private SapsanProductService sapsanProductService;

    @Autowired
    private EcoSapsanProductService ecoSapsanProductService;

    @Transactional(propagation = REQUIRES_NEW)
    public void schedule() {
        em.createNativeQuery(SQL_DISABLE_FOREIGN_KEY_CHECKS).executeUpdate();
        em.createQuery("DELETE FROM SapsanProduct").executeUpdate();
        List<SapsanProduct> sapsanProductList = ecoSapsanProductService.getAll().stream().map(ecoSapsanProduct -> {
            SapsanProduct sapsanProduct = new SapsanProduct();
            sapsanProduct.setId(ecoSapsanProduct.getId());
            sapsanProduct.setName(ecoSapsanProduct.getName());
            sapsanProduct.setPrefix(ecoSapsanProduct.getPrefix());
            return sapsanProduct;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(sapsanProductList)) {
            sapsanProductService.saveAll(sapsanProductList);
        }
        em.createNativeQuery(SQL_ENABLE_FOREIGN_KEY_CHECKS).executeUpdate();
    }
}
package ru.korundm.schedule.importation.process;

import eco.dao.EcoSapsanProductBomService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.SapsanProductBomService;
import ru.korundm.entity.Bom;
import ru.korundm.entity.SapsanProduct;
import ru.korundm.entity.SapsanProductBom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static ru.korundm.constant.BaseConstant.SQL_DISABLE_FOREIGN_KEY_CHECKS;
import static ru.korundm.constant.BaseConstant.SQL_ENABLE_FOREIGN_KEY_CHECKS;

/**
 * Процесс переноса ECOPLAN.R_SAPSAN_PRODUCT_BOM
 * @author zhestkov_an
 * Date:   02.09.2021
 */
@Component
@Scope(SCOPE_SINGLETON)
public class SapsanProductBomProcess {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private SapsanProductBomService sapsanProductBomService;

    @Autowired
    private EcoSapsanProductBomService ecoSapsanProductBomService;

    @Transactional(propagation = REQUIRES_NEW)
    public void schedule() {
        em.createNativeQuery(SQL_DISABLE_FOREIGN_KEY_CHECKS).executeUpdate();
        em.createQuery("DELETE FROM SapsanProductBom").executeUpdate();
        List<SapsanProductBom> sapProductBomList = ecoSapsanProductBomService.getAll().stream().map(ecoSapProductBom -> {
            SapsanProductBom sapProductBom = new SapsanProductBom();
            sapProductBom.setId(ecoSapProductBom.getId());
            sapProductBom.setOrderIndex(ecoSapProductBom.getOrderIndex());
            sapProductBom.setPrime(ecoSapProductBom.isPrime());
            sapProductBom.setSapsanProduct(new SapsanProduct(ecoSapProductBom.getSapsanProduct().getId()));
            sapProductBom.setBom(new Bom(ecoSapProductBom.getBom().getId()));
            return sapProductBom;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(sapProductBomList)) {
            sapsanProductBomService.saveAll(sapProductBomList);
        }
        em.createNativeQuery(SQL_ENABLE_FOREIGN_KEY_CHECKS).executeUpdate();
    }
}

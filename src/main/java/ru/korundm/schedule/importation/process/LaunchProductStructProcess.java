package ru.korundm.schedule.importation.process;

import eco.dao.EcoLaunchProductService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static ru.korundm.constant.BaseConstant.SQL_DISABLE_FOREIGN_KEY_CHECKS;
import static ru.korundm.constant.BaseConstant.SQL_ENABLE_FOREIGN_KEY_CHECKS;

@Component
@Scope(SCOPE_SINGLETON)
public class LaunchProductStructProcess {

    @Setter @Getter
    public static class Item {
        private Long lpId;
        private Long productId;
        private Integer amount;
    }

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private EcoLaunchProductService ecoLaunchProductService;

    @Transactional(propagation = REQUIRES_NEW)
    public void schedule() {
        em.createNativeQuery(SQL_DISABLE_FOREIGN_KEY_CHECKS).executeUpdate();
        em.createQuery("DELETE FROM LaunchProductStruct").executeUpdate();
        ecoLaunchProductService.importStructData().forEach(it ->
            em.createNativeQuery("INSERT INTO launch_product_struct(id, ver, launch_product_id, product_id, amount) " +
                "VALUES (default, 0, :lpId, :productId, :amount)")
                .setParameter("lpId", it.getLpId())
                .setParameter("productId", it.getProductId())
                .setParameter("amount", it.getAmount())
                .executeUpdate()
        );
        em.createNativeQuery(SQL_ENABLE_FOREIGN_KEY_CHECKS).executeUpdate();
    }
}
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
import java.time.LocalDate;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static ru.korundm.constant.BaseConstant.SQL_DISABLE_FOREIGN_KEY_CHECKS;
import static ru.korundm.constant.BaseConstant.SQL_ENABLE_FOREIGN_KEY_CHECKS;

/**
 * Процесс переноса ECOPLAN.LAUNCH_PRODUCT
 * @author zhestkov_an
 * Date:   20.02.2020
 */
@Component
@Scope(SCOPE_SINGLETON)
public class LaunchProductProcess {

    @Setter @Getter
    public static class Item {
        private Long lpId;
        private Long launchId;
        private Long productId;
        private Long versionId;
        private LocalDate versionApproveDate;
    }

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private EcoLaunchProductService ecoLaunchProductService;

    @Transactional(propagation = REQUIRES_NEW)
    public void schedule() {
        em.createNativeQuery(SQL_DISABLE_FOREIGN_KEY_CHECKS).executeUpdate();
        em.createQuery("DELETE FROM LaunchProduct").executeUpdate();
        ecoLaunchProductService.importData().forEach(it ->
            em.createNativeQuery("INSERT INTO launch_product(id, ver, launch_id, product_id, version_id, version_approve_date, contract_amount, rf_contract_amount, rf_assembled_amount, ufrf_contract_amount, ufrf_assembled_amount, ufrf_contract_in_other_product_amount) " +
                "VALUES (:id, 0, :launchId, :productId, :versionId, :versionApproveDate, 0, 0, 0, 0, 0, 0)")
                .setParameter("id", it.getLpId())
                .setParameter("launchId", it.getLaunchId())
                .setParameter("productId", it.getProductId())
                .setParameter("versionId", it.getVersionId())
                .setParameter("versionApproveDate", it.getVersionApproveDate())
                .executeUpdate()
        );
        em.createNativeQuery(SQL_ENABLE_FOREIGN_KEY_CHECKS).executeUpdate();
    }
}
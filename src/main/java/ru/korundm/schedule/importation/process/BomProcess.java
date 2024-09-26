package ru.korundm.schedule.importation.process;

import eco.dao.EcoBomService;
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

/**
 * Процесс заполнения таблицы boms
 * @author zhestkov_an
 * Date:   12.04.2020
 */
@Component
@Scope(SCOPE_SINGLETON)
public class BomProcess {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private EcoBomService ecoBomService;

    @Transactional(propagation = REQUIRES_NEW)
    public void schedule() {
        em.createNativeQuery(SQL_DISABLE_FOREIGN_KEY_CHECKS).executeUpdate();
        em.createQuery("DELETE FROM Bom").executeUpdate();
        ecoBomService.getAll().forEach(it ->
            em.createNativeQuery("INSERT INTO boms(id, ver, major, minor, modification, stock, product_id, production_name, fix_date, create_date, descriptor) " +
                "VALUES (:id, 0, :major, :minor, :modification, :stock, :productId, :productionName, :fixDate, :createDate, :descriptor)")
                .setParameter("id", it.getId())
                .setParameter("major", it.getMajor())
                .setParameter("minor", it.getMinor())
                .setParameter("modification", it.getModification())
                .setParameter("stock", false)
                .setParameter("productId", it.getProduct().getId())
                .setParameter("productionName", it.getProductionName())
                .setParameter("fixDate", it.getFixDate())
                .setParameter("createDate", it.getCreated())
                .setParameter("descriptor", it.getDescriptor())
                .executeUpdate()
        );
        em.createNativeQuery(SQL_ENABLE_FOREIGN_KEY_CHECKS).executeUpdate();
    }
}
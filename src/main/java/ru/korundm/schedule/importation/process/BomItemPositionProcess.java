package ru.korundm.schedule.importation.process;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.BomItemPositionService;
import ru.korundm.entity.BomItem;
import ru.korundm.entity.BomItemPosition;
import ru.korundm.constant.BaseConstant;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Процесс заполнения таблицы bom_item_positions
 * @author mazur_ea
 * Date:   29.05.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class BomItemPositionProcess {

    @PersistenceContext
    private EntityManager em;

    @PersistenceContext(unitName = "ecoEntityManagerFactory")
    private EntityManager ecoEm;

    @Autowired
    private BomItemPositionService bomItemPositionService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void schedule() {
        em.createNativeQuery(BaseConstant.SQL_DISABLE_FOREIGN_KEY_CHECKS).executeUpdate();
        em.createQuery("DELETE FROM BomItemPosition").executeUpdate();
        @SuppressWarnings("unchecked")
        List<Object[]> ecoResultList = ecoEm.createNativeQuery(
            "SELECT\n" +
            "   MIN(BI.ID) OVER(PARTITION BY BI.BOM_ID, BIC.COMPONENT_ID) BI_ID,\n" +
            "   BI.SYMBOL\n" +
            "FROM\n" +
            "   BOM_ITEM BI\n" +
            "   JOIN\n" +
            "   BOM_ITEM_COMPONENT BIC\n" +
            "   ON\n" +
            "   BI.ID = BIC.BOM_ITEM_ID\n" +
            "WHERE\n" +
            "   BIC.KD = 1\n" +
            "   AND BI.BOM_ID NOT IN (576116, 5076911)" // кривые бомы (дубли по версиям)
        ).getResultList();
        List<BomItemPosition> list = ecoResultList.stream().map(ecoEntity -> {
            BomItemPosition entity = new BomItemPosition();
            entity.setDesignation(ecoEntity[1] == null ? null : (String) ecoEntity[1]);
            //
            BomItem bomItem = new BomItem();
            bomItem.setId(((BigDecimal) ecoEntity[0]).longValue());
            entity.setBomItem(bomItem);
            return entity;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(list)) {
            bomItemPositionService.saveAll(list);
        }
        em.createNativeQuery(BaseConstant.SQL_ENABLE_FOREIGN_KEY_CHECKS).executeUpdate();
    }
}
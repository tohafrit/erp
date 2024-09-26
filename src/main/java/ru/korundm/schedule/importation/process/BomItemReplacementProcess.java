package ru.korundm.schedule.importation.process;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.constant.BaseConstant;
import ru.korundm.dao.BomItemReplacementService;
import ru.korundm.entity.BomItem;
import ru.korundm.entity.BomItemReplacement;
import ru.korundm.enumeration.BomItemReplacementStatus;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static ru.korundm.constant.BaseConstant.SQL_DISABLE_FOREIGN_KEY_CHECKS;
import static ru.korundm.constant.BaseConstant.SQL_ENABLE_FOREIGN_KEY_CHECKS;

/**
 * Процесс заполнения таблицы bom_item_replacements
 * @author mazur_ea
 * Date:   29.05.2020
 */
@Component
@Scope(SCOPE_SINGLETON)
public class BomItemReplacementProcess {

    @PersistenceContext
    private EntityManager em;

    @PersistenceContext(unitName = "ecoEntityManagerFactory")
    private EntityManager ecoEm;

    @Autowired
    private BomItemReplacementService bomItemComponentService;

    @Transactional(propagation = REQUIRES_NEW)
    public void schedule() {
        em.createNativeQuery(SQL_DISABLE_FOREIGN_KEY_CHECKS).executeUpdate();
        em.createQuery("DELETE FROM BomItemReplacement").executeUpdate();
        @SuppressWarnings("unchecked")
        List<Object[]> ecoResultList = ecoEm.createNativeQuery(
            "SELECT\n" +
            "   BIC.ID,\n" +
            "   BIC.STATUS,\n" +
            "   BIC.BOM_ITEM_ID,\n" +
            "   BIC.COMPONENT_ID,\n" +
            "   BIC.PURCHASE,\n" +
            "   BIC.DATE_REPLACED,\n" +
            "   BIC.DATE_PROCESSED\n" +
            "FROM \n" +
            "   BOM_ITEM_COMPONENT BIC\n" +
            "   JOIN (\n" +
            "       SELECT\n" +
            "           BI.ID BI_ID,\n" +
            "           ROW_NUMBER() OVER(PARTITION BY BI.BOM_ID, BIC.COMPONENT_ID ORDER BY BI.ID) RN\n" +
            "       FROM\n" +
            "           BOM_ITEM BI\n" +
            "           JOIN\n" +
            "           BOM_ITEM_COMPONENT BIC\n" +
            "           ON\n" +
            "           BI.ID = BIC.BOM_ITEM_ID\n" +
            "       WHERE\n" +
            "           BIC.KD = 1\n" +
            "           AND BI.BOM_ID NOT IN (576116, 5076911)\n" + // кривые бомы (дубли по версиям)
            "   ) RS\n" +
            "   ON\n" +
            "   RS.BI_ID = BIC.BOM_ITEM_ID\n" +
            "   AND RS.RN = 1"
        ).getResultList();
        List<BomItemReplacement> list = ecoResultList.stream().map(ecoEntity -> {
            BomItemReplacement entity = new BomItemReplacement();
            entity.setId(((BigDecimal) ecoEntity[0]).longValue());
            entity.setStatus(BomItemReplacementStatus.getById(((BigDecimal) ecoEntity[1]).longValue() + 1));
            entity.setPurchase(((BigDecimal) ecoEntity[4]).intValue() > 0);
            entity.setReplacementDate(ecoEntity[5] == null ? null : ((Timestamp) ecoEntity[5]).toLocalDateTime().toLocalDate());
            entity.setStatusDate(ecoEntity[6] == null ? null : ((Timestamp) ecoEntity[6]).toLocalDateTime().toLocalDate());
            //
            BomItem bomItem = new BomItem();
            bomItem.setId(((BigDecimal) ecoEntity[2]).longValue());
            entity.setBomItem(bomItem);
            //
            ru.korundm.entity.Component component = new ru.korundm.entity.Component();
            component.setId(((BigDecimal) ecoEntity[3]).longValue());
            entity.setComponent(component);
            return entity;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(list)) {
            bomItemComponentService.saveAll(list);
        }
        em.createNativeQuery(SQL_ENABLE_FOREIGN_KEY_CHECKS).executeUpdate();
    }
}
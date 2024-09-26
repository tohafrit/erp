package ru.korundm.schedule.importation.process;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.constant.BaseConstant;
import ru.korundm.dao.BomItemService;
import ru.korundm.entity.Bom;
import ru.korundm.entity.BomItem;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static ru.korundm.constant.BaseConstant.SQL_DISABLE_FOREIGN_KEY_CHECKS;
import static ru.korundm.constant.BaseConstant.SQL_ENABLE_FOREIGN_KEY_CHECKS;

/**
 * Процесс заполнения таблицы bom_items
 * @author mazur_ea
 * Date:   29.05.2020
 */
@Component
@Scope(SCOPE_SINGLETON)
public class BomItemProcess {

    @PersistenceContext
    private EntityManager em;

    @PersistenceContext(unitName = "ecoEntityManagerFactory")
    private EntityManager ecoEm;

    @Autowired
    private BomItemService bomItemService;

    @Transactional(propagation = REQUIRES_NEW)
    public void schedule() {
        em.createNativeQuery(SQL_DISABLE_FOREIGN_KEY_CHECKS).executeUpdate();
        em.createQuery("DELETE FROM BomItem").executeUpdate();
        @SuppressWarnings("unchecked")
        List<Object[]> ecoResultList = ecoEm.createNativeQuery(
            "SELECT\n" +
            "   BI_ID,\n" +
            "   BOM_ID,\n" +
            "   COMPONENT_ID,\n" +
            "   QUANTITY\n" +
            "FROM (\n" +
            "   SELECT\n" +
            "       BI.ID BI_ID,\n" +
            "       BI.BOM_ID,\n" +
            "       BIC.COMPONENT_ID,\n" +
            "       BI.QUANTITY,\n" +
            "       ROW_NUMBER() OVER(PARTITION BY BI.BOM_ID, BIC.COMPONENT_ID ORDER BY BI.ID) RN\n" +
            "   FROM\n" +
            "       BOM_ITEM BI\n" +
            "       JOIN\n" +
            "       BOM_ITEM_COMPONENT BIC\n" +
            "       ON\n" +
            "       BI.ID = BIC.BOM_ITEM_ID\n" +
            "   WHERE\n" +
            "       BIC.KD = 1\n" +
            "       AND BI.BOM_ID NOT IN (576116, 5076911)\n" + // кривые бомы (дубли по версиям)
            ") RS\n" +
            "WHERE\n" +
            "   RS.RN = 1"
        ).getResultList();
        List<BomItem> list = ecoResultList.stream().map(ecoEntity -> {
            BomItem entity = new BomItem();
            entity.setId(((BigDecimal) ecoEntity[0]).longValue());
            //
            Bom bom = new Bom();
            bom.setId(((BigDecimal) ecoEntity[1]).longValue());
            entity.setBom(bom);
            //
            ru.korundm.entity.Component component = new ru.korundm.entity.Component();
            component.setId(((BigDecimal) ecoEntity[2]).longValue());
            entity.setComponent(component);
            //
            entity.setQuantity(((BigDecimal) ecoEntity[3]).doubleValue());
            return entity;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(list)) {
            bomItemService.saveAll(list);
        }
        em.createNativeQuery(SQL_ENABLE_FOREIGN_KEY_CHECKS).executeUpdate();
    }
}
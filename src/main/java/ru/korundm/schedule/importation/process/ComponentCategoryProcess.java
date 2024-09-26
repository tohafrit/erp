package ru.korundm.schedule.importation.process;

import eco.dao.EcoBomComponentCategoryService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.ComponentCategoryService;
import ru.korundm.entity.ComponentCategory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static ru.korundm.constant.BaseConstant.SQL_DISABLE_FOREIGN_KEY_CHECKS;
import static ru.korundm.constant.BaseConstant.SQL_ENABLE_FOREIGN_KEY_CHECKS;

/**
 * Процесс заполнения таблицы component_categories
 * @author pakhunov_an
 * Date:   07.05.2020
 */
@Component
@Scope(SCOPE_SINGLETON)
public class ComponentCategoryProcess {

    /** Список идентификаторов штучных категорий */
    public static final List<Long> unitCategoryIdList = List.of(
        2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 13L, 14L, 15L, 16L, 17L, 19L, 20L, 21L, 23L,
        24L, 25L, 26L, 27L, 28L, 29L, 30L, 31L, 32L, 33L, 34L, 35L, 36L, 52L, 53L
    );

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ComponentCategoryService componentCategoryService;

    @Autowired
    private EcoBomComponentCategoryService ecoBomComponentCategoryService;

    @Transactional(propagation = REQUIRES_NEW)
    public void schedule() {
        em.createNativeQuery(SQL_DISABLE_FOREIGN_KEY_CHECKS).executeUpdate();
        em.createQuery("DELETE FROM ComponentCategory").executeUpdate();
        List<ComponentCategory> componentCategoryList = ecoBomComponentCategoryService.getAll().stream().map(ecoEntity -> {
            ComponentCategory entity = new ComponentCategory();
            entity.setId(ecoEntity.getId());
            entity.setName(ecoEntity.getName());
            entity.setUnit(unitCategoryIdList.contains(ecoEntity.getId()));
            return entity;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(componentCategoryList)) {
            componentCategoryService.saveAll(componentCategoryList);
        }
        em.createNativeQuery(SQL_ENABLE_FOREIGN_KEY_CHECKS).executeUpdate();
    }
}
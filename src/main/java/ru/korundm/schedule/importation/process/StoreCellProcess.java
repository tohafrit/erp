package ru.korundm.schedule.importation.process;

import eco.dao.EcoStoreCellService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.StorageCellService;
import ru.korundm.entity.StorageCell;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static ru.korundm.constant.BaseConstant.SQL_DISABLE_FOREIGN_KEY_CHECKS;
import static ru.korundm.constant.BaseConstant.SQL_ENABLE_FOREIGN_KEY_CHECKS;

/**
 * Процесс переноса ECOPLAN.STORE_CELL
 * @author berezin_mm
 * Date:   10.04.2020
 */
@Component
@Scope(SCOPE_SINGLETON)
public class StoreCellProcess {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private StorageCellService storeCellService;

    @Autowired
    private EcoStoreCellService ecoStoreCellService;

    @Transactional(propagation = REQUIRES_NEW)
    public void schedule() {
        em.createNativeQuery(SQL_DISABLE_FOREIGN_KEY_CHECKS).executeUpdate();
        em.createQuery("DELETE FROM StorageCell").executeUpdate();
        List<StorageCell> storageCellList = ecoStoreCellService.getAll().stream().map(ecoStoreCell -> {
            StorageCell storageCell = new StorageCell();
            storageCell.setId(ecoStoreCell.getId());
            storageCell.setName(ecoStoreCell.getName());
            return storageCell;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(storageCellList)) {
            storeCellService.saveAll(storageCellList);
        }
        em.createNativeQuery(SQL_ENABLE_FOREIGN_KEY_CHECKS).executeUpdate();
    }
}
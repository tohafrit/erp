package ru.korundm.schedule.importation.process;

import eco.dao.EcoStoragePlaceService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.StoragePlaceService;
import ru.korundm.entity.StoragePlace;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static ru.korundm.constant.BaseConstant.SQL_DISABLE_FOREIGN_KEY_CHECKS;
import static ru.korundm.constant.BaseConstant.SQL_ENABLE_FOREIGN_KEY_CHECKS;

/**
 * Процесс переноса ECOPLAN.STORAGE_PLACE
 * @author berezin_mm
 * Date:   10.04.2020
 */
@Component
@Scope(SCOPE_SINGLETON)
public class StoragePlaceProcess {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private StoragePlaceService storagePlaceService;

    @Autowired
    private EcoStoragePlaceService ecoStoragePlaceService;

    @Transactional(propagation = REQUIRES_NEW)
    public void schedule() {
        em.createNativeQuery(SQL_DISABLE_FOREIGN_KEY_CHECKS).executeUpdate();
        em.createQuery("DELETE FROM StoragePlace").executeUpdate();
        List<StoragePlace> storagePlaceList = ecoStoragePlaceService.getAll().stream().map(ecoStoragePlace -> {
            StoragePlace storagePlace = new StoragePlace();
            storagePlace.setId(ecoStoragePlace.getId());
            storagePlace.setName(ecoStoragePlace.getPlace());
            return storagePlace;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(storagePlaceList)) {
            storagePlaceService.saveAll(storagePlaceList);
        }
        em.createNativeQuery(SQL_ENABLE_FOREIGN_KEY_CHECKS).executeUpdate();
    }
}
package ru.korundm.schedule.importation.task;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.korundm.schedule.importation.process.StoragePlaceProcess;

/**
 * Задача переноса ECOPLAN.STORAGE_PLACE
 * @author berezin_mm
 * Date:   10.04.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class StoragePlaceTask implements Runnable {

    private StoragePlaceProcess storagePlaceProcess;

    public StoragePlaceTask(StoragePlaceProcess storagePlaceProcess) {
        this.storagePlaceProcess = storagePlaceProcess;
    }

    @Override
    public synchronized void run() {
        storagePlaceProcess.schedule();
    }
}

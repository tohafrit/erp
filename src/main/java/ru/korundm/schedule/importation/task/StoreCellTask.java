package ru.korundm.schedule.importation.task;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.korundm.schedule.importation.process.StoreCellProcess;

/**
 * Задача переноса ECOPLAN.STORE_CELL
 * @author berezin_mm
 * Date:   10.04.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class StoreCellTask implements Runnable {

    private StoreCellProcess storeCellProcess;

    public StoreCellTask(StoreCellProcess storeCellProcess) {
        this.storeCellProcess = storeCellProcess;
    }

    @Override
    public synchronized void run() {
        storeCellProcess.schedule();
    }
}
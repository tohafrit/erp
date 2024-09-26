package ru.korundm.schedule.importation.task;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.korundm.schedule.importation.process.BomSpecItemProcess;

/**
 * Процесс заполнения таблицы bom_spec_items
 * @author mazur_ea
 * Date:   18.05.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class BomSpecItemTask implements Runnable {

    private BomSpecItemProcess bomSpecItemProcess;

    public BomSpecItemTask(BomSpecItemProcess bomSpecItemProcess) {
        this.bomSpecItemProcess = bomSpecItemProcess;
    }

    @Override
    public synchronized void run() {
        bomSpecItemProcess.schedule();
    }
}
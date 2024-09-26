package ru.korundm.schedule.importation.task;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.korundm.schedule.importation.process.LotGroupProcess;

/**
 * Задача переноса ECOPLAN.LOT_GROUP
 * @author zhestkov_an
 * Date:   27.03.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class LotGroupTask implements Runnable {

    private LotGroupProcess lotGroupProcess;

    public LotGroupTask(LotGroupProcess lotGroupProcess) {
        this.lotGroupProcess = lotGroupProcess;
    }

    @Override
    public synchronized void run() {
        lotGroupProcess.schedule();
    }
}

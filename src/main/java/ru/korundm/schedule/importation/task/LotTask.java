package ru.korundm.schedule.importation.task;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.korundm.schedule.importation.process.LotProcess;

/**
 * Задача переноса ECOPLAN.LOT
 * @author zhestkov_an
 * Date:   27.03.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class LotTask implements Runnable {

    private LotProcess lotProcess;

    public LotTask(LotProcess lotProcess) {
        this.lotProcess = lotProcess;
    }

    @Override
    public synchronized void run() {
        lotProcess.schedule();
    }
}

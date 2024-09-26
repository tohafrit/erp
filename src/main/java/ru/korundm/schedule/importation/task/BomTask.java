package ru.korundm.schedule.importation.task;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.korundm.schedule.importation.process.BomProcess;

/**
 * Задача заполнения таблицы boms
 * @author zhestkov_an
 * Date:   12.04.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class BomTask implements Runnable {

    private BomProcess bomProcess;

    public BomTask(BomProcess bomProcess) {
        this.bomProcess = bomProcess;
    }

    @Override
    public synchronized void run() {
        bomProcess.schedule();
    }
}
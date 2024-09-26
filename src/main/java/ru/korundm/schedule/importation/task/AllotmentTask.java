package ru.korundm.schedule.importation.task;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.korundm.schedule.importation.process.AllotmentProcess;

/**
 * Задача переноса ECOPLAN.ALLOTMENT
 * @author zhestkov_an
 * Date:   26.03.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AllotmentTask implements Runnable {

    private AllotmentProcess allotmentProcess;

    public AllotmentTask(AllotmentProcess allotmentProcess) {
        this.allotmentProcess = allotmentProcess;
    }

    @Override
    public synchronized void run() {
        allotmentProcess.schedule();
    }
}

package ru.korundm.schedule.importation.task;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.korundm.schedule.importation.process.PresentLogRecordProcess;

/**
 * Задача переноса ECOPLAN.PRESENT_LOG_RECORD
 * @author berezin_mm
 * Date:   03.04.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class PresentLogRecordTask implements Runnable {

    private PresentLogRecordProcess presentLogRecordProcess;

    public PresentLogRecordTask(PresentLogRecordProcess presentLogRecordProcess) {
        this.presentLogRecordProcess = presentLogRecordProcess;
    }

    @Override
    public synchronized void run() {
        presentLogRecordProcess.schedule();
    }
}
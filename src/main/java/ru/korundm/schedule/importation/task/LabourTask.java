package ru.korundm.schedule.importation.task;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.korundm.schedule.importation.process.LabourProcess;

/**
 * Задача переноса ECOPLAN.LABOUR
 * @author zhestkov_an
 * Date:   01.05.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class LabourTask implements Runnable {

    private LabourProcess labourProcess;

    public LabourTask(LabourProcess labourProcess) {
        this.labourProcess = labourProcess;
    }

    @Override
    public synchronized void run() {
        labourProcess.schedule();
    }
}
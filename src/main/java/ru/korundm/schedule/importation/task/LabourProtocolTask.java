package ru.korundm.schedule.importation.task;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.korundm.schedule.importation.process.LabourProtocolProcess;

/**
 * Задача переноса ECOPLAN.LABOUR_PROTOCOL
 * @author zhestkov_an
 * Date:   01.05.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class LabourProtocolTask implements Runnable {

    private LabourProtocolProcess labourProtocolProcess;

    public LabourProtocolTask(LabourProtocolProcess labourProtocolProcess) {
        this.labourProtocolProcess = labourProtocolProcess;
    }

    @Override
    public synchronized void run() {
        labourProtocolProcess.schedule();
    }
}
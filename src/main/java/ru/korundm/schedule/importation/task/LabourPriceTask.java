package ru.korundm.schedule.importation.task;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.korundm.schedule.importation.process.LabourPriceProcess;

/**
 * Задача переноса ECOPLAN.LABOUR_PRICE
 * @author zhestkov_an
 * Date:   01.05.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class LabourPriceTask implements Runnable {

    private LabourPriceProcess labourPriceProcess;

    public LabourPriceTask(LabourPriceProcess labourPriceProcess) {
        this.labourPriceProcess = labourPriceProcess;
    }

    @Override
    public synchronized void run() {
        labourPriceProcess.schedule();
    }
}
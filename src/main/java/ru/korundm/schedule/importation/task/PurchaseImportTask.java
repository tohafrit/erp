package ru.korundm.schedule.importation.task;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.korundm.schedule.importation.process.PurchaseProcess;

/**
 * Задача переноса PROC_PARAM
 * @author pakhunov_an
 * Date:   07.02.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class PurchaseImportTask implements Runnable {

    private PurchaseProcess purchaseProcess;

    public PurchaseImportTask(PurchaseProcess purchaseProcess) {
        this.purchaseProcess = purchaseProcess;
    }

    @Override
    public void run() {
        purchaseProcess.schedule();
    }
}
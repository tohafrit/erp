package ru.korundm.schedule.importation.task;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.korundm.schedule.importation.process.InvoiceProcess;

/**
 * Задача переноса ECOPLAN.INVOICE
 * @author zhestkov_an
 * Date:   24.04.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class InvoiceTask implements Runnable {

    private InvoiceProcess invoiceProcess;

    public InvoiceTask(InvoiceProcess invoiceProcess) {
        this.invoiceProcess = invoiceProcess;
    }

    @Override
    public synchronized void run() {
        invoiceProcess.schedule();
    }
}
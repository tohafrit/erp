package ru.korundm.schedule.importation.task;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.korundm.schedule.importation.process.BankProcess;

/**
 * Задача переноса ECOPLAN.BANK
 * @author zhestkov_an
 * Date:   30.03.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class BankTask implements Runnable {

    private BankProcess bankProcess;

    public BankTask(BankProcess bankProcess) {
        this.bankProcess = bankProcess;
    }

    @Override
    public synchronized void run() {
        bankProcess.schedule();
    }
}

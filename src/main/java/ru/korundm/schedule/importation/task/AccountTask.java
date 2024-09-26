package ru.korundm.schedule.importation.task;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.korundm.schedule.importation.process.AccountProcess;

/**
 * Задача переноса ECOPLAN.ACCOUNT
 * @author zhestkov_an
 * Date:   23.04.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AccountTask implements Runnable {

    private AccountProcess accountProcess;

    public AccountTask(AccountProcess accountProcess) {
        this.accountProcess = accountProcess;
    }

    @Override
    public synchronized void run() {
        accountProcess.schedule();
    }
}
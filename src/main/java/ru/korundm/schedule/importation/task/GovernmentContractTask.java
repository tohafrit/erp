package ru.korundm.schedule.importation.task;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.korundm.schedule.importation.process.GovernmentContractProcess;

/**
 * Задача переноса ECOPLAN.IDS
 * @author zhestkov_an
 * Date:   08.07.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class GovernmentContractTask implements Runnable {

    private final GovernmentContractProcess governmentContractProcess;

    public GovernmentContractTask(GovernmentContractProcess governmentContractProcess) {
        this.governmentContractProcess = governmentContractProcess;
    }

    @Override
    public synchronized void run() {
        governmentContractProcess.schedule();
    }
}

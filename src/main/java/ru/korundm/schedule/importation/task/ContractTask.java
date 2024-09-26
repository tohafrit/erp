package ru.korundm.schedule.importation.task;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.korundm.schedule.importation.process.ContractProcess;

/**
 * Задача переноса ECOPLAN.CONTRACT
 * @author zhestkov_an
 * Date:   28.03.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ContractTask implements Runnable {

    private ContractProcess contractProcess;

    public ContractTask(ContractProcess contractProcess) {
        this.contractProcess = contractProcess;
    }

    @Override
    public synchronized void run() {
        contractProcess.schedule();
    }
}

package ru.korundm.schedule.importation.task;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.korundm.schedule.importation.process.ContractSectionProcess;

/**
 * Задача переноса ECOPLAN.CONTRACT_SECTION
 * @author zhestkov_an
 * Date:   27.03.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ContractSectionTask implements Runnable {

    private final ContractSectionProcess contractSectionProcess;

    public ContractSectionTask(ContractSectionProcess contractSectionProcess) {
        this.contractSectionProcess = contractSectionProcess;
    }

    @Override
    public synchronized void run() {
        contractSectionProcess.schedule();
    }
}
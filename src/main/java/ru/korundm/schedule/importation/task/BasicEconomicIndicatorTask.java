package ru.korundm.schedule.importation.task;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.korundm.schedule.importation.process.BasicEconomicIndicatorProcess;

/**
 * Задача переноса ECOPLAN.COMPANY_CONST_PROTOCOL
 * @author pakhunov_an
 * Date:   13.05.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class BasicEconomicIndicatorTask implements Runnable {

    private final BasicEconomicIndicatorProcess basicEconomicIndicatorProcess;

    public BasicEconomicIndicatorTask(BasicEconomicIndicatorProcess basicEconomicIndicatorProcess) {
        this.basicEconomicIndicatorProcess = basicEconomicIndicatorProcess;
    }

    @Override
    public synchronized void run() {
        basicEconomicIndicatorProcess.schedule();
    }
}
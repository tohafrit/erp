package ru.korundm.schedule.importation.task;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.korundm.schedule.importation.process.LaunchProductProductionLotReferenceProcess;

/**
 * Задача переноса ECOPLAN.R_LP_PL_USAGE
 * @author zhestkov_an
 * Date:   14.04.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class LaunchProductProductionLotReferenceTask implements Runnable {

    private LaunchProductProductionLotReferenceProcess launchProductProductionLotReferenceProcess;

    public LaunchProductProductionLotReferenceTask(LaunchProductProductionLotReferenceProcess launchProductProductionLotReferenceProcess) {
        this.launchProductProductionLotReferenceProcess = launchProductProductionLotReferenceProcess;
    }

    @Override
    public synchronized void run() {
        launchProductProductionLotReferenceProcess.schedule();
    }
}
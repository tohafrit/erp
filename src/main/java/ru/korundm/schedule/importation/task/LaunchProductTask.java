package ru.korundm.schedule.importation.task;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.korundm.schedule.importation.process.LaunchProductProcess;

/**
 * Задача переноса ECOPLAN.LAUNCH_PRODUCT
 * @author zhestkov_an
 * Date:   20.02.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class LaunchProductTask implements Runnable {

    private LaunchProductProcess launchProductProcess;

    public LaunchProductTask(LaunchProductProcess launchProductProcess) {
        this.launchProductProcess = launchProductProcess;
    }

    @Override
    public synchronized void run() {
        launchProductProcess.schedule();
    }
}

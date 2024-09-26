package ru.korundm.schedule.importation.task;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.korundm.schedule.importation.process.LaunchProcess;

/**
 * Задача переноса ECOPLAN.LAUNCH
 * @author zhestkov_an
 * Date:   20.02.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class LaunchTask implements Runnable {

    private LaunchProcess launchProcess;

    public LaunchTask(LaunchProcess launchProcess) {
        this.launchProcess = launchProcess;
    }

    @Override
    public synchronized void run() {
        launchProcess.schedule();
    }
}

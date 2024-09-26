package ru.korundm.schedule.importation.task;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.korundm.schedule.importation.process.LaunchProductStructProcess;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class LaunchProductStructTask implements Runnable {

    private final LaunchProductStructProcess launchProductStructProcess;

    public LaunchProductStructTask(LaunchProductStructProcess launchProductStructProcess) {
        this.launchProductStructProcess = launchProductStructProcess;
    }

    @Override
    public synchronized void run() {
        launchProductStructProcess.schedule();
    }
}
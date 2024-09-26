package ru.korundm.schedule.importation.task;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.korundm.schedule.importation.process.MainProcess;

/**
 * Задача переноса некоторых данных
 * @author mazur_ea
 * Date:   29.03.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class MainTask implements Runnable {

    private MainProcess mainProcess;

    public MainTask(MainProcess mainProcess) {
        this.mainProcess = mainProcess;
    }

    @Override
    public synchronized void run() { mainProcess.schedule(); }
}
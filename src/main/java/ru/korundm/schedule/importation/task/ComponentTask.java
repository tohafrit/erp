package ru.korundm.schedule.importation.task;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.korundm.schedule.importation.process.ComponentProcess;

/**
 * Задача заполнения таблицы components
 * @author mazur_ea
 * Date:   29.05.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ComponentTask implements Runnable {

    private ComponentProcess componentProcess;

    public ComponentTask(ComponentProcess componentProcess) {
        this.componentProcess = componentProcess;
    }

    @Override
    public synchronized void run() {
        componentProcess.schedule();
    }
}
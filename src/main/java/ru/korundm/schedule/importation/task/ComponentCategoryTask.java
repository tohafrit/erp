package ru.korundm.schedule.importation.task;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.korundm.schedule.importation.process.ComponentCategoryProcess;

/**
 * Задача заполнения таблицы component_categories
 * @author pakhunov_an
 * Date:   07.05.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ComponentCategoryTask implements Runnable {

    private ComponentCategoryProcess componentCategoryProcess;

    public ComponentCategoryTask(ComponentCategoryProcess componentCategoryProcess) {
        this.componentCategoryProcess = componentCategoryProcess;
    }

    @Override
    public synchronized void run() {
        componentCategoryProcess.schedule();
    }
}
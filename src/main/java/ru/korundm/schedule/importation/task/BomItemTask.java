package ru.korundm.schedule.importation.task;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.korundm.schedule.importation.process.BomItemProcess;

/**
 * Задача заполнения таблицы bom_items
 * @author mazur_ea
 * Date:   29.05.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class BomItemTask implements Runnable {

    private BomItemProcess bomItemProcess;

    public BomItemTask(BomItemProcess bomItemProcess) {
        this.bomItemProcess = bomItemProcess;
    }

    @Override
    public synchronized void run() {
        bomItemProcess.schedule();
    }
}
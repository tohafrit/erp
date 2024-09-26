package ru.korundm.schedule.importation.task;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.korundm.schedule.importation.process.BomItemPositionProcess;

/**
 * Задача заполнения таблицы bom_item_positions
 * @author mazur_ea
 * Date:   29.05.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class BomItemPositionTask implements Runnable {

    private BomItemPositionProcess bomItemPositionProcess;

    public BomItemPositionTask(BomItemPositionProcess bomItemPositionProcess) {
        this.bomItemPositionProcess = bomItemPositionProcess;
    }

    @Override
    public synchronized void run() {
        bomItemPositionProcess.schedule();
    }
}
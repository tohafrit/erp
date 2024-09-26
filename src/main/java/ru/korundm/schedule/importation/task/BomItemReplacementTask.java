package ru.korundm.schedule.importation.task;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.korundm.schedule.importation.process.BomItemReplacementProcess;

/**
 * Задача заполнения таблицы bom_item_replacements
 * @author mazur_ea
 * Date:   29.05.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class BomItemReplacementTask implements Runnable {

    private BomItemReplacementProcess bomItemReplacementProcess;

    public BomItemReplacementTask(BomItemReplacementProcess bomItemReplacementProcess) {
        this.bomItemReplacementProcess = bomItemReplacementProcess;
    }

    @Override
    public synchronized void run() {
        bomItemReplacementProcess.schedule();
    }
}
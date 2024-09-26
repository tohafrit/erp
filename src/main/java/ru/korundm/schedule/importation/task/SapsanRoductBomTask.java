package ru.korundm.schedule.importation.task;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.korundm.schedule.importation.process.SapsanProductBomProcess;

/**
 * Задача переноса ECOPLAN.R_SAPSAN_PRODUCT_BOM
 * @author zhestkov_an
 * Date:   02.09.2021
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SapsanRoductBomTask implements Runnable {

    private SapsanProductBomProcess sapsanProductBomProcess;

    public SapsanRoductBomTask(SapsanProductBomProcess sapsanProductBomProcess) {
        this.sapsanProductBomProcess = sapsanProductBomProcess;
    }

    @Override
    public synchronized void run() {
        sapsanProductBomProcess.schedule();
    }
}

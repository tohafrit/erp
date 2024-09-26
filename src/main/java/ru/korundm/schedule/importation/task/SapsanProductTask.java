package ru.korundm.schedule.importation.task;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.korundm.schedule.importation.process.SapsanProductProcess;

/**
 * Задача переноса ECOPLAN.SAPSAN_PRODUCT
 * @author berezin_mm
 * Date:   29.06.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SapsanProductTask implements Runnable {

    private SapsanProductProcess sapsanProductProcess;

    public SapsanProductTask(SapsanProductProcess sapsanProductProcess) {
        this.sapsanProductProcess = sapsanProductProcess;
    }

    @Override
    public synchronized void run() {
        sapsanProductProcess.schedule();
    }
}
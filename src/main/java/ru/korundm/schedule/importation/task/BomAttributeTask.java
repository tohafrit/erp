package ru.korundm.schedule.importation.task;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.korundm.schedule.importation.process.BomAttributeProcess;

/**
 * Задача переноса ECOPLAN.BOM_ATTRIBUTE
 * @author zhestkov_an
 * Date:   12.04.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class BomAttributeTask implements Runnable {

    private BomAttributeProcess bomAttributeProcess;

    public BomAttributeTask(BomAttributeProcess bomAttributeProcess) {
        this.bomAttributeProcess = bomAttributeProcess;
    }

    @Override
    public synchronized void run() {
        bomAttributeProcess.schedule();
    }
}
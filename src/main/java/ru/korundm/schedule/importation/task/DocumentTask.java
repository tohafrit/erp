package ru.korundm.schedule.importation.task;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.korundm.schedule.importation.process.DocumentProcess;

/**
 * Задача переноса ECOPLAN.DOCUMENT
 * @author zhestkov_an
 * Date:   30.03.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class DocumentTask implements Runnable {

    private DocumentProcess documentProcess;

    public DocumentTask(DocumentProcess documentProcess) {
        this.documentProcess = documentProcess;
    }

    @Override
    public synchronized void run() {
        documentProcess.schedule();
    }
}

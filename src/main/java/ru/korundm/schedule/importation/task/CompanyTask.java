package ru.korundm.schedule.importation.task;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.korundm.schedule.importation.process.CompanyProcess;

/**
 * Задача заполнения таблицы companies
 * @author pakhunov_an
 * Date:   01.08.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CompanyTask implements Runnable {

    private CompanyProcess companyProcess;

    public CompanyTask(CompanyProcess companyProcess) {
        this.companyProcess = companyProcess;
    }

    @Override
    public synchronized void run() {
        companyProcess.schedule();
    }
}
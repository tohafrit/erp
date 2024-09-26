package ru.korundm.schedule.importation.task;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.korundm.schedule.importation.process.User1CProcess;

/**
 * Задача переноса ECOPLAN.T_1C_USER
 * @author berezin_mm
 * Date:   14.04.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class User1CTask implements Runnable {

    private User1CProcess user1CProcess;

    public User1CTask(User1CProcess user1CProcess) {
        this.user1CProcess = user1CProcess;
    }

    @Override
    public synchronized void run() {
        user1CProcess.schedule();
    }
}
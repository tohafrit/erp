package ru.korundm.schedule.importation.task;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.korundm.schedule.importation.process.PaymentProcess;

/**
 * Задача переноса ECOPLAN.PAYMENT
 * @author zhestkov_an
 * Date:   24.04.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class PaymentTask implements Runnable {

    private PaymentProcess paymentProcess;

    public PaymentTask(PaymentProcess paymentProcess) {
        this.paymentProcess = paymentProcess;
    }

    @Override
    public synchronized void run() {
        paymentProcess.schedule();
    }
}
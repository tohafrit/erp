package ru.korundm.schedule.importation.task;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.korundm.schedule.importation.process.ProductProcess;

/**
 * Задача переноса ECOPLAN.PRODUCT
 * @author mazur_ea
 * Date:   28.02.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ProductTask implements Runnable {

    private ProductProcess productProcess;

    public ProductTask(ProductProcess productProcess) {
        this.productProcess = productProcess;
    }

    @Override
    public synchronized void run() {
        productProcess.schedule();
    }
}
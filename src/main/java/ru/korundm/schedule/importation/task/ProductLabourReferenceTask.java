package ru.korundm.schedule.importation.task;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.korundm.schedule.importation.process.ProductLabourReferenceProcess;

/**
 * Задача переноса ECOPLAN.PRODUCT_LABOUR
 * @author zhestkov_an
 * Date:   01.05.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ProductLabourReferenceTask implements Runnable {

    private ProductLabourReferenceProcess productLabourReferenceProcess;

    public ProductLabourReferenceTask(ProductLabourReferenceProcess productLabourReferenceProcess) {
        this.productLabourReferenceProcess = productLabourReferenceProcess;
    }

    @Override
    public synchronized void run() {
        productLabourReferenceProcess.schedule();
    }
}
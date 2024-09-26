package ru.korundm.schedule.importation.task;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.korundm.schedule.importation.process.ProductChargesProtocolProcess;

/**
 * Задача переноса ECOPLAN.PRODUCT_CHARGES_PROTOCOL
 * @author zhestkov_an
 * Date:   13.04.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ProductChargesProtocolTask implements Runnable {

    private ProductChargesProtocolProcess productChargesProtocolProcess;

    public ProductChargesProtocolTask(ProductChargesProtocolProcess productChargesProtocolProcess) {
        this.productChargesProtocolProcess = productChargesProtocolProcess;
    }

    @Override
    public synchronized void run() {
        productChargesProtocolProcess.schedule();
    }
}
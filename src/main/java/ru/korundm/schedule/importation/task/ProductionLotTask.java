package ru.korundm.schedule.importation.task;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.korundm.schedule.importation.process.ProductionLotProcess;

/**
 * Задача переноса ECOPLAN.PRODUCTION_LOT
 * @author zhestkov_an
 * Date:   12.04.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ProductionLotTask implements Runnable {

    private ProductionLotProcess productionLotProcess;

    public ProductionLotTask(ProductionLotProcess productionLotProcess) {
        this.productionLotProcess = productionLotProcess;
    }

    @Override
    public synchronized void run() {
        productionLotProcess.schedule();
    }
}
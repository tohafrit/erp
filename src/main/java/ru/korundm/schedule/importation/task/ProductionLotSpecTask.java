package ru.korundm.schedule.importation.task;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.korundm.schedule.importation.process.ProductionLotSpecProcess;

/**
 * Задача переноса ECOPLAN.PRODUCTION_LOT_SPEC
 * @author zhestkov_an
 * Date:   12.04.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ProductionLotSpecTask implements Runnable {

    private ProductionLotSpecProcess productionLotSpecProcess;

    public ProductionLotSpecTask(ProductionLotSpecProcess productionLotSpecProcess) {
        this.productionLotSpecProcess = productionLotSpecProcess;
    }

    @Override
    public synchronized void run() {
        productionLotSpecProcess.schedule();
    }
}
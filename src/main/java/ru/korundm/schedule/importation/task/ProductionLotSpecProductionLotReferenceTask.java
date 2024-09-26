package ru.korundm.schedule.importation.task;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.korundm.schedule.importation.process.ProductionLotSpecProductionLotReferenceProcess;

/**
 * Задача переноса ECOPLAN.R_PL_PL_USAGE
 * @author zhestkov_an
 * Date:   14.04.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ProductionLotSpecProductionLotReferenceTask implements Runnable {

    private ProductionLotSpecProductionLotReferenceProcess productionLotSpecProductionLotReferenceProcess;

    public ProductionLotSpecProductionLotReferenceTask(ProductionLotSpecProductionLotReferenceProcess productionLotSpecProductionLotReferenceProcess) {
        this.productionLotSpecProductionLotReferenceProcess = productionLotSpecProductionLotReferenceProcess;
    }

    @Override
    public synchronized void run() {
        productionLotSpecProductionLotReferenceProcess.schedule();
    }
}
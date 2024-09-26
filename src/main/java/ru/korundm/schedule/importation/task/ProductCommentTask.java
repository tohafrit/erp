package ru.korundm.schedule.importation.task;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.korundm.schedule.importation.process.ProductCommentProcess;

/**
 * Задача переноса ECOPLAN.COMMENT
 * @author zhestkov_an
 * Date:   06.10.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ProductCommentTask implements Runnable {

    private ProductCommentProcess productCommentProcess;

    public ProductCommentTask(ProductCommentProcess productCommentProcess) {
        this.productCommentProcess = productCommentProcess;
    }

    @Override
    public synchronized void run() {
        productCommentProcess.schedule();
    }
}
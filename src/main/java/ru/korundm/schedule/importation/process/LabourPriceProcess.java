package ru.korundm.schedule.importation.process;

import eco.dao.EcoLabourPriceService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.LabourPriceService;
import ru.korundm.entity.Labour;
import ru.korundm.entity.LabourPrice;
import ru.korundm.entity.LabourProtocol;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static ru.korundm.constant.BaseConstant.SQL_DISABLE_FOREIGN_KEY_CHECKS;
import static ru.korundm.constant.BaseConstant.SQL_ENABLE_FOREIGN_KEY_CHECKS;

/**
 * Процесс переноса ECOPLAN.LABOUR_PRICE
 * @author zhestkov_an
 * Date:   01.05.2020
 */
@Component
@Scope(SCOPE_SINGLETON)
public class LabourPriceProcess {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private LabourPriceService labourPriceService;

    @Autowired
    private EcoLabourPriceService ecoLabourPriceService;

    @Transactional(propagation = REQUIRES_NEW)
    public void schedule() {
        em.createNativeQuery(SQL_DISABLE_FOREIGN_KEY_CHECKS).executeUpdate();
        em.createQuery("DELETE FROM LabourPrice").executeUpdate();
        List<LabourPrice> labourPriceList = ecoLabourPriceService.getAll().stream().map(ecoLabourPrice -> {
            LabourPrice labourPrice = new LabourPrice();
            labourPrice.setId(ecoLabourPrice.getId());
            if (ecoLabourPrice.getLabour() != null) {
                Labour labour = new Labour();
                labour.setId(ecoLabourPrice.getLabour().getId());
                labourPrice.setLabour(labour);
            }
            labourPrice.setHourlyPay(ecoLabourPrice.getHourlyPay());
            if (ecoLabourPrice.getProtocol() != null) {
                LabourProtocol labourProtocol = new LabourProtocol();
                labourProtocol.setId(ecoLabourPrice.getProtocol().getId());
                labourPrice.setLabourProtocol(labourProtocol);
            }
            labourPrice.setOrderIndex(ecoLabourPrice.getOrderIndex());
            return labourPrice;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(labourPriceList)) {
            labourPriceService.saveAll(labourPriceList);
        }
        em.createNativeQuery(SQL_ENABLE_FOREIGN_KEY_CHECKS).executeUpdate();
    }
}
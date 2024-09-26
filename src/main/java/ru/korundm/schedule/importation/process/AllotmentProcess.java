package ru.korundm.schedule.importation.process;

import eco.dao.EcoAllotmentService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.AllotmentService;
import ru.korundm.entity.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static ru.korundm.constant.BaseConstant.SQL_DISABLE_FOREIGN_KEY_CHECKS;
import static ru.korundm.constant.BaseConstant.SQL_ENABLE_FOREIGN_KEY_CHECKS;

/**
 * Процесс переноса ECOPLAN.ALLOTMENT
 * @author zhestkov_an
 * Date:   26.03.2020
 */
@Component
@Scope(SCOPE_SINGLETON)
public class AllotmentProcess {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private AllotmentService allotmentService;

    @Autowired
    private EcoAllotmentService ecoAllotmentService;

    @Transactional(propagation = REQUIRES_NEW)
    public void schedule() {
        em.createNativeQuery(SQL_DISABLE_FOREIGN_KEY_CHECKS).executeUpdate();
        em.createQuery("DELETE FROM Allotment").executeUpdate();
        List<Allotment> allotmentList = ecoAllotmentService.getAll().stream().map(ecoAllotment -> {
            Allotment allotment = new Allotment();
            allotment.setId(ecoAllotment.getId());
            if (ecoAllotment.getLot() != null) {
                allotment.setLot(new Lot(ecoAllotment.getLot().getId()));
            }
            if (ecoAllotment.getLaunchProduct() != null) {
                allotment.setLaunchProduct(new LaunchProduct(ecoAllotment.getLaunchProduct().getId()));
            }
            allotment.setAmount(ecoAllotment.getAmount());
            allotment.setPrice(ecoAllotment.getPrice());
            allotment.setPriceKind(ecoAllotment.getPriceKind());
            if (ecoAllotment.getPriceProtocol() != null) {
                allotment.setProtocol(new ProductChargesProtocol(ecoAllotment.getPriceProtocol()));
            }
            allotment.setPaid(ecoAllotment.getPaid());
            allotment.setShipmentDate(ecoAllotment.getShipmentDate());
            allotment.setNote(ecoAllotment.getNote());
            allotment.setOrderIndex(ecoAllotment.getOrderIndex());
            allotment.setFinalPrice(ecoAllotment.getFinalPrice());
            allotment.setShipmentPermitDate(ecoAllotment.getShipmentPermitDate());
            allotment.setIntendedShipmentDate(ecoAllotment.getIntendedShipmentDate());
            allotment.setRequestId(ecoAllotment.getRequestId());
            allotment.setTransferForWrappingDate(ecoAllotment.getTransferForWrappingDate());
            allotment.setReadyForShipmentDate(ecoAllotment.getReadyForShipmentDate());
            allotment.setShipment(ecoAllotment.getShipment());
            allotment.setAdvancedStudyDate(ecoAllotment.getAdvancedStudyDate());
            return allotment;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(allotmentList)) {
            allotmentService.saveAll(allotmentList);
        }
        em.createNativeQuery(SQL_ENABLE_FOREIGN_KEY_CHECKS).executeUpdate();
    }
}
package ru.korundm.schedule.importation.process;

import eco.dao.EcoLotService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.LotService;
import ru.korundm.dao.ValueAddedTaxService;
import ru.korundm.entity.Lot;
import ru.korundm.entity.LotGroup;
import ru.korundm.entity.ProductChargesProtocol;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static ru.korundm.constant.BaseConstant.SQL_DISABLE_FOREIGN_KEY_CHECKS;
import static ru.korundm.constant.BaseConstant.SQL_ENABLE_FOREIGN_KEY_CHECKS;

/**
 * Процесс переноса ECOPLAN.LOT
 * @author zhestkov_an
 * Date:   27.03.2020
 */
@Component
@Scope(SCOPE_SINGLETON)
public class LotProcess {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private LotService lotService;

    @Autowired
    private EcoLotService ecoLotService;

    @Autowired
    private ValueAddedTaxService valueAddedTaxService;

    @Transactional(propagation = REQUIRES_NEW)
    public void schedule() {
        em.createNativeQuery(SQL_DISABLE_FOREIGN_KEY_CHECKS).executeUpdate();
        em.createQuery("DELETE FROM Lot").executeUpdate();
        List<Lot> lotList = ecoLotService.getAll().stream().map(ecoLot -> {
            Lot lot = new Lot();
            lot.setId(ecoLot.getId());
            if (ecoLot.getLotGroup() != null) {
                lot.setLotGroup(new LotGroup(ecoLot.getLotGroup().getId()));
            }
            lot.setAmount(ecoLot.getAmount());
            lot.setDeliveryDate(ecoLot.getDeliveryDate().toLocalDate());
            lot.setPrice(ecoLot.getPrice());
            lot.setPriceKind(ecoLot.getPriceKind());
            if (ecoLot.getPriceProtocol() != null) lot.setProtocol(new ProductChargesProtocol(ecoLot.getPriceProtocol()));
            lot.setAcceptType(ecoLot.getAcceptType());
            lot.setSpecialTestType(ecoLot.getSpecialTestType());
            lot.setReturnDate(ecoLot.getReturnDate());
            lot.setContractStageID(ecoLot.getContractStageID());
            lot.setStageName(ecoLot.getStageName());
            var vat = valueAddedTaxService.findByDateFrom(lot.getDeliveryDate());
            if (vat == null) vat = valueAddedTaxService.getDateFromLast();
            lot.setVat(vat);
            return lot;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(lotList)) lotService.saveAll(lotList);
        em.createNativeQuery(SQL_ENABLE_FOREIGN_KEY_CHECKS).executeUpdate();
    }
}
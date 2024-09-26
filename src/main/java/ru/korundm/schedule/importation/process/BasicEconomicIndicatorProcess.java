package ru.korundm.schedule.importation.process;

import eco.dao.EcoCompanyConstProtocolService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.BasicEconomicIndicatorService;
import ru.korundm.entity.BasicEconomicIndicator;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static ru.korundm.constant.BaseConstant.SQL_DISABLE_FOREIGN_KEY_CHECKS;
import static ru.korundm.constant.BaseConstant.SQL_ENABLE_FOREIGN_KEY_CHECKS;

/**
 * Процесс переноса ECOPLAN.COMPANY_CONST_PROTOCOL
 * @author pakhunov_an
 * Date:   13.05.2020
 */
@Component
@Scope(SCOPE_SINGLETON)
public class BasicEconomicIndicatorProcess {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private BasicEconomicIndicatorService basicEconomicIndicatorService;

    @Autowired
    private EcoCompanyConstProtocolService ecoCompanyConstProtocolService;

    @Transactional(propagation = REQUIRES_NEW)
    public void schedule() {
        em.createNativeQuery(SQL_DISABLE_FOREIGN_KEY_CHECKS).executeUpdate();
        em.createQuery("DELETE FROM BasicEconomicIndicator").executeUpdate();
        List<BasicEconomicIndicator> basicEconomicIndicatorList = ecoCompanyConstProtocolService.getAll().stream().map(ecoCompanyConstProtocol -> {
            BasicEconomicIndicator basicEconomicIndicator = new BasicEconomicIndicator();
            /*basicEconomicIndicator.setId(ecoCompanyConstProtocol.getId());
            basicEconomicIndicator.setProtocolNumber(ecoCompanyConstProtocol.getProtocolNumber());
            basicEconomicIndicator.setProtocolDate(ecoCompanyConstProtocol.getProtocolDate());
            basicEconomicIndicator.setProtocolNote(ecoCompanyConstProtocol.getProtocolNote());
            *//*if (ecoCompanyConstProtocol.getDocumentId() != null) {
                basicEconomicIndicator.setDocument(ecoCompanyConstProtocol.getDocumentId());
            }*//*
            basicEconomicIndicator.setCompany(new Company(ecoCompanyConstProtocol.getCompany().getId()));
            basicEconomicIndicator.setAdditionalWagesRate(String.valueOf(ecoCompanyConstProtocol.getAdditionalWagesRate()));
            basicEconomicIndicator.setSocialInsuranceRate(String.valueOf(ecoCompanyConstProtocol.getSocialInsuranceRate()));
            basicEconomicIndicator.setProfitRate(String.valueOf(ecoCompanyConstProtocol.getProfitRate()));
            basicEconomicIndicator.setManufacturingChargesRate(String.valueOf(ecoCompanyConstProtocol.getManufacturingChargesRate()));
            basicEconomicIndicator.setWorkshopChargesRate(String.valueOf(ecoCompanyConstProtocol.getWorkshopChargesRate()));
            basicEconomicIndicator.setAverageMonthlyPay(String.valueOf(ecoCompanyConstProtocol.getAverageMonthlyPay()));*/
            return basicEconomicIndicator;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(basicEconomicIndicatorList)) {
            basicEconomicIndicatorService.saveAll(basicEconomicIndicatorList);
        }
        em.createNativeQuery(SQL_ENABLE_FOREIGN_KEY_CHECKS).executeUpdate();
    }
}
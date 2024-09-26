package ru.korundm.schedule.importation.process;

import eco.dao.EcoContractService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.ContractService;
import ru.korundm.entity.Company;
import ru.korundm.entity.Contract;
import ru.korundm.enumeration.ContractType;
import ru.korundm.enumeration.Performer;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static ru.korundm.constant.BaseConstant.SQL_DISABLE_FOREIGN_KEY_CHECKS;
import static ru.korundm.constant.BaseConstant.SQL_ENABLE_FOREIGN_KEY_CHECKS;

/**
 * Процесс переноса ECOPLAN.CONTRACT
 * @author zhestkov_an
 * Date:   28.03.2020
 */
@Component
@Scope(SCOPE_SINGLETON)
public class ContractProcess {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ContractService contractService;

    @Autowired
    private EcoContractService ecoContractService;

    @Transactional(propagation = REQUIRES_NEW)
    public void schedule() {
        em.createNativeQuery(SQL_DISABLE_FOREIGN_KEY_CHECKS).executeUpdate();
        em.createQuery("DELETE FROM Contract").executeUpdate();
        List<Contract> contractList = ecoContractService.getAll().stream().map(ecoContract -> {
            Contract contract = new Contract();
            contract.setId(ecoContract.getId());
            contract.setType(ContractType.Companion.getById(ecoContract.getContractType()));
            contract.setNumber(ecoContract.getContractNumber().intValue());
            if (ecoContract.getCustomer() != null) {
                Company company = new Company();
                company.setId(ecoContract.getCustomer().getId());
                contract.setCustomer(company);
            }
            contract.setComment(ecoContract.getNote());
            contract.setPerformer(Performer.Companion.getById(ecoContract.getPerformer()));
            return contract;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(contractList)) {
            contractService.saveAll(contractList);
        }
        em.createNativeQuery(SQL_ENABLE_FOREIGN_KEY_CHECKS).executeUpdate();
    }
}
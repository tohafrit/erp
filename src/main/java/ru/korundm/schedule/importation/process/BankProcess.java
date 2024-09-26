package ru.korundm.schedule.importation.process;

import eco.dao.EcoBankService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.BankService;
import ru.korundm.entity.Bank;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static ru.korundm.constant.BaseConstant.SQL_DISABLE_FOREIGN_KEY_CHECKS;
import static ru.korundm.constant.BaseConstant.SQL_ENABLE_FOREIGN_KEY_CHECKS;

/**
 * Процесс переноса ECOPLAN.BANK
 * @author zhestkov_an
 * Date:   30.03.2020
 */
@Component
@Scope(SCOPE_SINGLETON)
public class BankProcess {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private BankService bankService;

    @Autowired
    private EcoBankService ecoBankService;

    @Transactional(propagation = REQUIRES_NEW)
    public void schedule() {
        em.createNativeQuery(SQL_DISABLE_FOREIGN_KEY_CHECKS).executeUpdate();
        em.createQuery("DELETE FROM Bank").executeUpdate();
        List<Bank> bankList = ecoBankService.getAll().stream().map(ecoBank -> {
            Bank bank = new Bank();
            bank.setId(ecoBank.getId());
            bank.setCode1C(ecoBank.getCode1C());
            bank.setName(ecoBank.getName());
            bank.setLocation(ecoBank.getLocation());
            bank.setBik(ecoBank.getBik());
            bank.setCorrespondentAccount(ecoBank.getCorrespondentAccount());
            bank.setAddress(ecoBank.getAddress());
            bank.setPhone(ecoBank.getPhone());
            return bank;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(bankList)) {
            bankService.saveAll(bankList);
        }
        em.createNativeQuery(SQL_ENABLE_FOREIGN_KEY_CHECKS).executeUpdate();
    }
}
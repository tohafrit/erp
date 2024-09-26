package ru.korundm.schedule.importation.process;

import eco.dao.EcoAccountService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.AccountService;
import ru.korundm.entity.Account;
import ru.korundm.entity.Bank;
import ru.korundm.entity.Company;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static ru.korundm.constant.BaseConstant.SQL_DISABLE_FOREIGN_KEY_CHECKS;
import static ru.korundm.constant.BaseConstant.SQL_ENABLE_FOREIGN_KEY_CHECKS;

/**
 * Процесс переноса ECOPLAN.ACCOUNT
 * @author zhestkov_an
 * Date:   23.04.2020
 */
@Component
@Scope(SCOPE_SINGLETON)
public class AccountProcess {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private AccountService accountService;

    @Autowired
    private EcoAccountService ecoAccountService;

    @Transactional(propagation = REQUIRES_NEW)
    public void schedule() {
        em.createNativeQuery(SQL_DISABLE_FOREIGN_KEY_CHECKS).executeUpdate();
        em.createQuery("DELETE FROM Account").executeUpdate();
        List<Account> accountList = ecoAccountService.getAll().stream().map(ecoAccount -> {
            Account account = new Account();
            account.setId(ecoAccount.getId());
            account.setCode1C(ecoAccount.getCode1C());
            account.setCompany(new Company(ecoAccount.getCompany().getId()));
            account.setBank(new Bank(ecoAccount.getBank().getId()));
            account.setAccount(ecoAccount.getAccount());
            account.setNote(ecoAccount.getNote());
            return account;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(accountList)) {
            accountService.saveAll(accountList);
        }
        em.createNativeQuery(SQL_ENABLE_FOREIGN_KEY_CHECKS).executeUpdate();
    }
}
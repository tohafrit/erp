package ru.korundm.schedule.importation.process;

import eco.dao.EcoContractSectionService;
import eco.entity.EcoIdentifier;
import eco.entity.EcoUserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.constant.BaseConstant;
import ru.korundm.dao.AccountService;
import ru.korundm.dao.GovernmentContractService;
import ru.korundm.dao.UserService;
import ru.korundm.entity.Account;
import ru.korundm.entity.GovernmentContract;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static ru.korundm.constant.BaseConstant.SQL_DISABLE_FOREIGN_KEY_CHECKS;
import static ru.korundm.constant.BaseConstant.SQL_ENABLE_FOREIGN_KEY_CHECKS;

/**
 * Процесс переноса ECOPLAN.CONTRACT_SECTION
 * @author zhestkov_an
 * Date:   27.03.2020
 */
@Component
@Scope(SCOPE_SINGLETON)
public class ContractSectionProcess {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private EcoContractSectionService ecoContractSectionService;

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private GovernmentContractService governmentContractService;

    @Transactional(propagation = REQUIRES_NEW)
    public void schedule() {
        em.createNativeQuery(SQL_DISABLE_FOREIGN_KEY_CHECKS).executeUpdate();
        em.createQuery("DELETE FROM ContractSection").executeUpdate();
        ecoContractSectionService.getAll().forEach(it -> {
            String comment = it.getNote();
            EcoIdentifier ecoIdentifier = it.getIdentifier();
            Long separateAccountId = null;
            if (StringUtils.isNotBlank(comment)) {
                String[] commentArray = comment.split("//");
                if (commentArray.length > 0 && commentArray[0].matches(BaseConstant.ONLY_DIGITAL_PATTERN)) {
                    String obs = commentArray[0];
                    Account accountOBS = accountService.getByAccount(obs);
                    if (accountOBS != null) {
                        separateAccountId = accountOBS.getId();
                        if (ecoIdentifier != null) {
                            GovernmentContract govContract = governmentContractService.getGovernmentContractByIdentifier(ecoIdentifier.getNumber());
                            accountOBS.setGovernmentContract(govContract);
                            accountService.save(accountOBS);
                        }
                    }
                }
            }
            String identifierId = null;
            Long managerId = null;
            if (ecoIdentifier != null) identifierId = ecoIdentifier.getNumber();
            EcoUserInfo manager = it.getContract().getManager();
            if (manager != null && userService.existsById(manager.getId())) managerId = manager.getId();
            em.createNativeQuery("INSERT INTO contract_sections(id, contract_id, number, year, create_date, comment, archive_date, external_number, send_to_client_date, identifier, manager_id, separate_account_id) " +
                "VALUES (:id, :contractId, :number, :year, :createDate, :comment, :archiveDate, :externalNumber, :sendToClientDate, :identifier, :managerId, :separateAccountId)")
                .setParameter("id", it.getId())
                .setParameter("contractId", it.getContract().getId())
                .setParameter("number", it.getNumber().intValue())
                .setParameter("year", it.getDate().getYear())
                .setParameter("createDate", it.getDate())
                .setParameter("comment", it.getNote())
                .setParameter("archiveDate", it.getArchiveDate())
                .setParameter("externalNumber", it.getExternalName())
                .setParameter("sendToClientDate", it.getPzCopyDate())
                .setParameter("identifier", identifierId)
                .setParameter("managerId", managerId)
                .setParameter("separateAccountId", separateAccountId)
                .executeUpdate();
        });
        em.createNativeQuery(SQL_ENABLE_FOREIGN_KEY_CHECKS).executeUpdate();
    }
}
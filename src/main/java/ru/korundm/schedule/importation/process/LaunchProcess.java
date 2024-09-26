package ru.korundm.schedule.importation.process;

import eco.dao.EcoLaunchService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.LaunchService;
import ru.korundm.dao.UserService;
import ru.korundm.entity.Launch;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static ru.korundm.constant.BaseConstant.SQL_DISABLE_FOREIGN_KEY_CHECKS;
import static ru.korundm.constant.BaseConstant.SQL_ENABLE_FOREIGN_KEY_CHECKS;

/**
 * Процесс переноса ECOPLAN.LAUNCH
 * @author zhestkov_an
 * Date:   20.02.2020
 */
@Component
@Scope(SCOPE_SINGLETON)
public class LaunchProcess {

    /** Логин пользвателя: Малиновская Ю.А. */
    private static final String MALINOVSKAYA_UA = "malinovskaya_ua";

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private LaunchService launchService;

    @Autowired
    private EcoLaunchService ecoLaunchService;

    @Autowired
    private UserService userService;

    @Transactional(propagation = REQUIRES_NEW)
    public void schedule() {
        em.createNativeQuery(SQL_DISABLE_FOREIGN_KEY_CHECKS).executeUpdate();
        em.createQuery("DELETE FROM Launch").executeUpdate();
        List<Launch> launchList = ecoLaunchService.getAll().stream().map(ecoLaunch -> {
            Launch launch = new Launch();
            launch.setId(ecoLaunch.getId());
            launch.setYear(ecoLaunch.getYear().getYear());
            launch.setNumber(ecoLaunch.getNumberInYear());
            launch.setApprovalDate(ecoLaunch.getConfirmDate());
            launch.setComment(ecoLaunch.getNote());
            if (ecoLaunch.getConfirmDate() != null) {
                launch.setApprovedBy(userService.findByUserName(MALINOVSKAYA_UA));
            }
            return launch;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(launchList)) {
            launchService.saveAll(launchList);
        }
        em.createNativeQuery(SQL_ENABLE_FOREIGN_KEY_CHECKS).executeUpdate();
    }
}
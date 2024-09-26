package ru.korundm.schedule.importation.process;

import eco.dao.Eco1CUserService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.User1CService;
import ru.korundm.entity.User1C;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static ru.korundm.constant.BaseConstant.SQL_DISABLE_FOREIGN_KEY_CHECKS;
import static ru.korundm.constant.BaseConstant.SQL_ENABLE_FOREIGN_KEY_CHECKS;

/**
 * Процесс переноса ECOPLAN.T_1C_USER
 *
 * @author berezin_mm
 * Date:   14.04.2020
 */
@Component
@Scope(SCOPE_SINGLETON)
public class User1CProcess {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private User1CService user1CService;

    @Autowired
    private Eco1CUserService eco1CUserService;

    @Transactional(propagation = REQUIRES_NEW)
    public void schedule() {
        em.createNativeQuery(SQL_DISABLE_FOREIGN_KEY_CHECKS).executeUpdate();
        em.createQuery("DELETE FROM User1C").executeUpdate();
        List<User1C> user1CList = eco1CUserService.getAll().stream().map(eco1CUser -> {
            User1C user1C = new User1C();
            user1C.setId(eco1CUser.getId());
            user1C.setName(eco1CUser.getName());
            return user1C;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(user1CList)) {
            user1CService.saveAll(user1CList);
        }
        em.createNativeQuery(SQL_ENABLE_FOREIGN_KEY_CHECKS).executeUpdate();
    }
}
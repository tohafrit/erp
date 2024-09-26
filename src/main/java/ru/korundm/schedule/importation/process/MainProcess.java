package ru.korundm.schedule.importation.process;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.nio.charset.StandardCharsets;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static ru.korundm.constant.BaseConstant.SQL_DISABLE_FOREIGN_KEY_CHECKS;
import static ru.korundm.constant.BaseConstant.SQL_ENABLE_FOREIGN_KEY_CHECKS;

/**
 * Процесс переноса некоторых данных
 * @author mazur_ea
 * Date:   29.03.2020
 */
@Component
@Scope(SCOPE_SINGLETON)
public class MainProcess {

    @Autowired
    private ResourceLoader resourceLoader;

    @PersistenceContext
    private EntityManager em;

    @SneakyThrows
    @Transactional(propagation = REQUIRES_NEW)
    public void schedule() {
        em.createNativeQuery(SQL_DISABLE_FOREIGN_KEY_CHECKS).executeUpdate();
        em.createNativeQuery("DROP PROCEDURE IF EXISTS eco_import").executeUpdate();
        em.createNativeQuery(FileUtils.readFileToString(
            resourceLoader.getResource("classpath:sql/eco.sql").getFile(), StandardCharsets.UTF_8.name())).executeUpdate();
        em.createNativeQuery("CALL eco_import()").executeUpdate();
        em.createNativeQuery("DROP PROCEDURE IF EXISTS eco_import").executeUpdate();
        em.createNativeQuery(SQL_ENABLE_FOREIGN_KEY_CHECKS).executeUpdate();
    }
}
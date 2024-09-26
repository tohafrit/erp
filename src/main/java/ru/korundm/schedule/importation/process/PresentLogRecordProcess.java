package ru.korundm.schedule.importation.process;

import eco.dao.EcoPresentLogRecordService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.PresentLogRecordService;
import ru.korundm.entity.PresentLogRecord;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static ru.korundm.constant.BaseConstant.SQL_DISABLE_FOREIGN_KEY_CHECKS;
import static ru.korundm.constant.BaseConstant.SQL_ENABLE_FOREIGN_KEY_CHECKS;

/**
 * Процесс переноса ECOPLAN.PRESENT_LOG_RECORD
 *
 * @author berezin_mm
 * Date:   03.04.2020
 */
@Component
@Scope(SCOPE_SINGLETON)
public class PresentLogRecordProcess {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private PresentLogRecordService presentLogRecordService;

    @Autowired
    private EcoPresentLogRecordService ecoPresentLogRecordService;

    @Transactional(propagation = REQUIRES_NEW)
    public void schedule() {
        em.createNativeQuery(SQL_DISABLE_FOREIGN_KEY_CHECKS).executeUpdate();
        em.createQuery("DELETE FROM PresentLogRecord").executeUpdate();
        List<PresentLogRecord> presentLogRecordList = ecoPresentLogRecordService.getAll().stream().map(ecoPresentLogRecord -> {
            PresentLogRecord presentLogRecord = new PresentLogRecord();
            presentLogRecord.setId(ecoPresentLogRecord.getId());
            presentLogRecord.setNumber(ecoPresentLogRecord.getNumber());
            presentLogRecord.setRegistrationDate(ecoPresentLogRecord.getRegistrationDate());
            presentLogRecord.setYear(ecoPresentLogRecord.getRegistrationDate().getYear());
            return presentLogRecord;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(presentLogRecordList)) {
            presentLogRecordService.saveAll(presentLogRecordList);
        }
        em.createNativeQuery(SQL_ENABLE_FOREIGN_KEY_CHECKS).executeUpdate();
    }
}
package eco.dao;

import eco.entity.EcoPresentLogRecord;
import eco.repository.EcoPresentLogRecordRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class EcoPresentLogRecordService {

    @PersistenceContext(unitName = "ecoEntityManagerFactory")
    private EntityManager em;

    private final EcoPresentLogRecordRepository presentLogRecordRepository;

    public EcoPresentLogRecordService(EcoPresentLogRecordRepository presentLogRecordRepository) {
        this.presentLogRecordRepository = presentLogRecordRepository;
    }

    public List<EcoPresentLogRecord> getAll() {
        return presentLogRecordRepository.findAll();
    }

    public EcoPresentLogRecord getLast() {
        return presentLogRecordRepository.findTopByOrderByRegistrationDateDesc();
    }

    public EcoPresentLogRecord read(long id) {
        return presentLogRecordRepository.getOne(id);
    }
}
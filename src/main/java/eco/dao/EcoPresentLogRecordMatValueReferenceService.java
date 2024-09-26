package eco.dao;

import eco.entity.EcoPresentLogRecordMatValueReference;
import eco.repository.EcoPresentLogRecordMatValueReferenceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EcoPresentLogRecordMatValueReferenceService {
    private final EcoPresentLogRecordMatValueReferenceRepository presentLogRecordMatValueReferenceRepository;

    public EcoPresentLogRecordMatValueReferenceService(EcoPresentLogRecordMatValueReferenceRepository presentLogRecordMatValueReferenceRepository) {
        this.presentLogRecordMatValueReferenceRepository = presentLogRecordMatValueReferenceRepository;
    }

    public List<EcoPresentLogRecordMatValueReference> getAll() {
        return presentLogRecordMatValueReferenceRepository.findAll();
    }
}
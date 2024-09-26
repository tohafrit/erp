package eco.dao;

import eco.entity.EcoMvInOutDocReference;
import eco.repository.EcoMvInOutDocReferenceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EcoMvInOutDocReferenceService {

    private final EcoMvInOutDocReferenceRepository mvInOutDocReferenceRepository;

    public EcoMvInOutDocReferenceService(EcoMvInOutDocReferenceRepository mvInOutDocReferenceRepository) {
        this.mvInOutDocReferenceRepository = mvInOutDocReferenceRepository;
    }

    public EcoMvInOutDocReference read(long id) {
        return mvInOutDocReferenceRepository.getOne(id);
    }

    public List<EcoMvInOutDocReference> getAll() {
        return mvInOutDocReferenceRepository.findAll();
    }
}
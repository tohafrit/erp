package eco.dao;

import eco.entity.EcoProductLabourId;
import eco.entity.EcoProductLabourReference;
import eco.repository.EcoProductLabourReferenceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EcoProductLabourReferenceService {

    private final EcoProductLabourReferenceRepository ecoProductLabourReferenceRepository;

    public EcoProductLabourReferenceService(EcoProductLabourReferenceRepository ecoProductLabourReferenceRepository) {
        this.ecoProductLabourReferenceRepository = ecoProductLabourReferenceRepository;
    }

    public EcoProductLabourReference read(EcoProductLabourId id) {
        return ecoProductLabourReferenceRepository.getOne(id);
    }

    public List<EcoProductLabourReference> getAll() {
        return ecoProductLabourReferenceRepository.findAll();
    }
}
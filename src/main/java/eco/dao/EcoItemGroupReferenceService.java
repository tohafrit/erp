package eco.dao;

import eco.entity.EcoItemGroupReference;
import eco.repository.EcoItemGroupReferenceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EcoItemGroupReferenceService {

    private final EcoItemGroupReferenceRepository ecoItemGroupReferenceRepository;

    public EcoItemGroupReferenceService(EcoItemGroupReferenceRepository ecoItemGroupReferenceRepository) {
        this.ecoItemGroupReferenceRepository = ecoItemGroupReferenceRepository;
    }

    public List<EcoItemGroupReference> getAll() {
        return ecoItemGroupReferenceRepository.findAll();
    }
}
package eco.dao;

import eco.entity.EcoContractSectionReference;
import eco.repository.EcoContractSectionReferenceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EcoContractSectionReferenceService {

    private final EcoContractSectionReferenceRepository ecoContractSectionReferenceRepository;

    public EcoContractSectionReferenceService(EcoContractSectionReferenceRepository ecoContractSectionReferenceRepository) {
        this.ecoContractSectionReferenceRepository = ecoContractSectionReferenceRepository;
    }

    public List<EcoContractSectionReference> getAll() {
        return ecoContractSectionReferenceRepository.findAll();
    }
}
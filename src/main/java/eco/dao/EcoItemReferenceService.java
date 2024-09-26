package eco.dao;

import eco.entity.EcoItemReference;
import eco.repository.EcoItemReferenceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EcoItemReferenceService {

    private final EcoItemReferenceRepository ecoItemReferenceRepository;

    public EcoItemReferenceService(EcoItemReferenceRepository ecoItemReferenceRepository) {
        this.ecoItemReferenceRepository = ecoItemReferenceRepository;
    }

    public List<EcoItemReference> getAllByProductionShipmentLetter(Long letterId) {
        return ecoItemReferenceRepository.findByItemGroupReference_ContractSectionReference_ProductShipmentLetter_Id(letterId);
    }

    public List<EcoItemReference> getAll() {
        return ecoItemReferenceRepository.findAll();
    }
}
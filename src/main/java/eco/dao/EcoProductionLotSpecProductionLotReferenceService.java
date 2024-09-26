package eco.dao;

import eco.entity.EcoProductionLotSpecProductionLotId;
import eco.entity.EcoProductionLotSpecProductionLotReference;
import eco.repository.EcoProductionLotSpecProductionLotReferenceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EcoProductionLotSpecProductionLotReferenceService {

    private final EcoProductionLotSpecProductionLotReferenceRepository ecoProductionLotSpecProductionLotReferenceRepository;

    public EcoProductionLotSpecProductionLotReferenceService(EcoProductionLotSpecProductionLotReferenceRepository ecoProductionLotSpecProductionLotReferenceRepository) {
        this.ecoProductionLotSpecProductionLotReferenceRepository = ecoProductionLotSpecProductionLotReferenceRepository;
    }

    public EcoProductionLotSpecProductionLotReference read(EcoProductionLotSpecProductionLotId id) {
        return ecoProductionLotSpecProductionLotReferenceRepository.getOne(id);
    }

    public List<EcoProductionLotSpecProductionLotReference> getAll() {
        return ecoProductionLotSpecProductionLotReferenceRepository.findAll();
    }
}
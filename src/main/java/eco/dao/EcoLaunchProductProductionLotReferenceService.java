package eco.dao;

import eco.entity.EcoLaunchProductProductionLotId;
import eco.entity.EcoLaunchProductProductionLotReference;
import eco.repository.EcoLaunchProductProductionLotReferenceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EcoLaunchProductProductionLotReferenceService {

    private final EcoLaunchProductProductionLotReferenceRepository ecoLaunchProductProductionLotReferenceRepository;

    public EcoLaunchProductProductionLotReferenceService(EcoLaunchProductProductionLotReferenceRepository ecoLaunchProductProductionLotReferenceRepository) {
        this.ecoLaunchProductProductionLotReferenceRepository = ecoLaunchProductProductionLotReferenceRepository;
    }

    public EcoLaunchProductProductionLotReference read(EcoLaunchProductProductionLotId id) {
        return ecoLaunchProductProductionLotReferenceRepository.getOne(id);
    }

    public List<EcoLaunchProductProductionLotReference> getAll() {
        return ecoLaunchProductProductionLotReferenceRepository.findAll();
    }
}
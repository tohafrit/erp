package eco.dao;

import eco.entity.EcoLot;
import eco.repository.EcoLotRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EcoLotService {

    private final EcoLotRepository ecoLotRepository;

    public EcoLotService(EcoLotRepository ecoLotRepository) {
        this.ecoLotRepository = ecoLotRepository;
    }

    public EcoLot read(Long id) {
        return ecoLotRepository.getOne(id);
    }

    public List<EcoLot> getAllByProductionShipmentLetter(Long letterId) {
        return ecoLotRepository.findDistinctByAllotmentList_ProductionShipmentLetterId(letterId);
    }

    public List<EcoLot> getAll() {
        return ecoLotRepository.findAll();
    }
}
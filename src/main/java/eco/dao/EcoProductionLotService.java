package eco.dao;


import eco.entity.EcoProductionLot;
import eco.repository.EcoProductionLotRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EcoProductionLotService {

    private final EcoProductionLotRepository productionLotRepository;

    public EcoProductionLotService(EcoProductionLotRepository productionLotRepository) {
        this.productionLotRepository = productionLotRepository;
    }

    public EcoProductionLot read(long id) {
        return productionLotRepository.getOne(id);
    }

    public List<EcoProductionLot> getAll() {
        return productionLotRepository.findAll();
    }
}
package eco.dao;

import eco.entity.EcoProductionLotSpec;
import eco.repository.EcoProductionLotSpecRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EcoProductionLotSpecService {

    private final EcoProductionLotSpecRepository productionLotSpecRepository;

    public EcoProductionLotSpecService(EcoProductionLotSpecRepository productionLotSpecRepository) {
        this.productionLotSpecRepository = productionLotSpecRepository;
    }

    public EcoProductionLotSpec read(long id) {
        return productionLotSpecRepository.getOne(id);
    }

    public List<EcoProductionLotSpec> getAll() {
        return productionLotSpecRepository.findAll();
    }
}
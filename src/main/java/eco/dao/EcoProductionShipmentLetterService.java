package eco.dao;

import eco.entity.EcoProductionShipmentLetter;
import eco.repository.EcoProductionShipmentLetterRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EcoProductionShipmentLetterService {

    private final EcoProductionShipmentLetterRepository productionShipmentLetterRepository;

    public EcoProductionShipmentLetterService(EcoProductionShipmentLetterRepository productionShipmentLetterRepository) {
        this.productionShipmentLetterRepository = productionShipmentLetterRepository;
    }

    public EcoProductionShipmentLetter read(long id) {
        return productionShipmentLetterRepository.getOne(id);
    }

    public List<EcoProductionShipmentLetter> getAll() {
        return productionShipmentLetterRepository.findAll();
    }

    public void deleteById(long id) {
        productionShipmentLetterRepository.deleteById(id);
    }
}
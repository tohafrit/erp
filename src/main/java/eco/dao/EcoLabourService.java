package eco.dao;

import eco.entity.EcoLabour;
import eco.repository.EcoLabourRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EcoLabourService {

    private final EcoLabourRepository ecoLabourRepository;

    public EcoLabourService(EcoLabourRepository ecoLabourRepository) {
        this.ecoLabourRepository = ecoLabourRepository;
    }

    public List<EcoLabour> getAll() {
        return ecoLabourRepository.findAll();
    }
}
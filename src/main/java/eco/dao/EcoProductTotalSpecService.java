package eco.dao;

import eco.entity.EcoProductTotalSpec;
import eco.entity.EcoProductTotalSpecId;
import eco.repository.EcoProductTotalSpecRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EcoProductTotalSpecService {

    private final EcoProductTotalSpecRepository ecoProductTotalSpecRepository;

    public EcoProductTotalSpecService(EcoProductTotalSpecRepository ecoProductTotalSpecRepository) {
        this.ecoProductTotalSpecRepository = ecoProductTotalSpecRepository;
    }

    public EcoProductTotalSpec read(EcoProductTotalSpecId id) {
        return ecoProductTotalSpecRepository.getOne(id);
    }

    public List<EcoProductTotalSpec> getAll() {
        return ecoProductTotalSpecRepository.findAll();
    }
}
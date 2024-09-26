package eco.dao;

import eco.entity.EcoTBicStatus;
import eco.repository.EcoTBicStatusRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EcoTBicStatusService {

    private final EcoTBicStatusRepository ecoTBicStatusRepository;

    public EcoTBicStatusService(EcoTBicStatusRepository ecoTBicStatusRepository) {
        this.ecoTBicStatusRepository = ecoTBicStatusRepository;
    }

    public List<EcoTBicStatus> getAll() {
        return ecoTBicStatusRepository.findAll();
    }
}
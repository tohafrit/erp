package eco.dao;

import eco.entity.EcoStoragePlace;
import eco.repository.EcoStoragePlaceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EcoStoragePlaceService {

    private final EcoStoragePlaceRepository ecoStoragePlaceRepository;

    public EcoStoragePlaceService(EcoStoragePlaceRepository ecoStoragePlaceRepository) {
        this.ecoStoragePlaceRepository = ecoStoragePlaceRepository;
    }

    public EcoStoragePlace read(long id) {
        return ecoStoragePlaceRepository.getOne(id);
    }

    public List<EcoStoragePlace> getAll() {
        return ecoStoragePlaceRepository.findAll();
    }
}

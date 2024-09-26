package asu.dao;

import asu.entity.AsuPlant;
import asu.repository.AsuPlantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AsuPlantService {

    private final AsuPlantRepository asuPlantRepository;

    public AsuPlantService(AsuPlantRepository asuPlantRepository) {
        this.asuPlantRepository = asuPlantRepository;
    }

    public AsuPlant read(long id) {
        return asuPlantRepository.getOne(id);
    }

    public List<AsuPlant> getAll() {
        return asuPlantRepository.findAll();
    }
}
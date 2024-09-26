package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.PlantService;
import ru.korundm.entity.Plant;
import ru.korundm.repository.PlantRepository;

import java.util.List;

@Service
@Transactional
public class PlantServiceImpl implements PlantService {

    private final PlantRepository plantRepository;

    public PlantServiceImpl(PlantRepository plantRepository) {
        this.plantRepository = plantRepository;
    }

    @Override
    public List<Plant> getAll() {
        return plantRepository.findAll();
    }

    @Override
    public List<Plant> getAllById(List<Long> idList) { return plantRepository.findAllById(idList); }

    @Override
    public Plant save(Plant object) {
        return plantRepository.save(object);
    }

    @Override
    public List<Plant> saveAll(List<Plant> objectList) { return plantRepository.saveAll(objectList); }

    @Override
    public Plant read(long id) {
        return plantRepository.getOne(id);
    }

    @Override
    public void delete(Plant object) {
        plantRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        plantRepository.deleteById(id);
    }
}

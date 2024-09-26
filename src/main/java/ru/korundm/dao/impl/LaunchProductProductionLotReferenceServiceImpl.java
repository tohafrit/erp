package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.LaunchProductProductionLotReferenceService;
import ru.korundm.entity.LaunchProductProductionLotReference;
import ru.korundm.repository.LaunchProductProductionLotReferenceRepository;

import java.util.List;

@Service
@Transactional
public class LaunchProductProductionLotReferenceServiceImpl implements LaunchProductProductionLotReferenceService {

    private final LaunchProductProductionLotReferenceRepository launchProductProductionLotReferenceRepository;

    public LaunchProductProductionLotReferenceServiceImpl(LaunchProductProductionLotReferenceRepository launchProductProductionLotReferenceRepository) {
        this.launchProductProductionLotReferenceRepository = launchProductProductionLotReferenceRepository;
    }

    @Override
    public List<LaunchProductProductionLotReference> getAll() {
        return launchProductProductionLotReferenceRepository.findAll();
    }

    @Override
    public List<LaunchProductProductionLotReference> getAllById(List<Long> idList) {
        return launchProductProductionLotReferenceRepository.findAllById(idList);
    }

    @Override
    public LaunchProductProductionLotReference save(LaunchProductProductionLotReference object) {
        return launchProductProductionLotReferenceRepository.save(object);
    }

    @Override
    public List<LaunchProductProductionLotReference> saveAll(List<LaunchProductProductionLotReference> objectList) {
        return launchProductProductionLotReferenceRepository.saveAll(objectList);
    }

    @Override
    public LaunchProductProductionLotReference read(long id) {
        return launchProductProductionLotReferenceRepository.getOne(id);
    }

    @Override
    public void delete(LaunchProductProductionLotReference object) {
        launchProductProductionLotReferenceRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        launchProductProductionLotReferenceRepository.deleteById(id);
    }
}
package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.ComponentInstallationTypeService;
import ru.korundm.entity.ComponentInstallationType;
import ru.korundm.repository.ComponentInstallationTypeRepository;

import java.util.List;

@Service
@Transactional
public class ComponentInstallationTypeServiceImpl implements ComponentInstallationTypeService {

    private final ComponentInstallationTypeRepository componentInstallationTypeRepository;

    public ComponentInstallationTypeServiceImpl(ComponentInstallationTypeRepository componentInstallationTypeRepository) {
        this.componentInstallationTypeRepository = componentInstallationTypeRepository;
    }

    @Override
    public List<ComponentInstallationType> getAll() {
        return componentInstallationTypeRepository.findAll();
    }

    @Override
    public List<ComponentInstallationType> getAllById(List<Long> idList) {
        return componentInstallationTypeRepository.findAllById(idList);
    }

    @Override
    public ComponentInstallationType save(ComponentInstallationType object) {
        return componentInstallationTypeRepository.save(object);
    }

    @Override
    public List<ComponentInstallationType> saveAll(List<ComponentInstallationType> objectList) {
        return componentInstallationTypeRepository.saveAll(objectList);
    }

    @Override
    public ComponentInstallationType read(long id) {
        return componentInstallationTypeRepository.getOne(id);
    }

    @Override
    public void delete(ComponentInstallationType object) {
        componentInstallationTypeRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        componentInstallationTypeRepository.deleteById(id);
    }
}
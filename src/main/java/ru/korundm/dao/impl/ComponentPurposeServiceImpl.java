package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.ComponentPurposeService;
import ru.korundm.entity.ComponentPurpose;
import ru.korundm.repository.ComponentPurposeRepository;

import java.util.List;

@Service
@Transactional
public class ComponentPurposeServiceImpl implements ComponentPurposeService {

    private final ComponentPurposeRepository componentPurposeRepository;

    public ComponentPurposeServiceImpl(ComponentPurposeRepository componentPurposeRepository) {
        this.componentPurposeRepository = componentPurposeRepository;
    }

    @Override
    public List<ComponentPurpose> getAll() {
        return componentPurposeRepository.findAll();
    }

    @Override
    public List<ComponentPurpose> getAllById(List<Long> idList) {
        return componentPurposeRepository.findAllById(idList);
    }

    @Override
    public ComponentPurpose save(ComponentPurpose object) {
        return componentPurposeRepository.save(object);
    }

    @Override
    public List<ComponentPurpose> saveAll(List<ComponentPurpose> objectList) {
        return componentPurposeRepository.saveAll(objectList);
    }

    @Override
    public ComponentPurpose read(long id) {
        return componentPurposeRepository.getOne(id);
    }

    @Override
    public void delete(ComponentPurpose object) {
        componentPurposeRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        componentPurposeRepository.deleteById(id);
    }
}
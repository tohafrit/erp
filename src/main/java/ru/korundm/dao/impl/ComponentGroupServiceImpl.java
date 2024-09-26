package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.ComponentGroupService;
import ru.korundm.entity.ComponentGroup;
import ru.korundm.repository.ComponentGroupRepository;

import java.util.List;

@Service
@Transactional
public class ComponentGroupServiceImpl implements ComponentGroupService {

    private final ComponentGroupRepository componentGroupRepository;

    public ComponentGroupServiceImpl(ComponentGroupRepository componentGroupRepository) {
        this.componentGroupRepository = componentGroupRepository;
    }

    @Override
    public List<ComponentGroup> getAll() {
        return componentGroupRepository.findAll();
    }

    @Override
    public List<ComponentGroup> getAllById(List<Long> idList) {
        return componentGroupRepository.findAllById(idList);
    }

    @Override
    public ComponentGroup save(ComponentGroup object) {
        return componentGroupRepository.save(object);
    }

    @Override
    public List<ComponentGroup> saveAll(List<ComponentGroup> objectList) {
        return componentGroupRepository.saveAll(objectList);
    }

    @Override
    public ComponentGroup read(long id) {
        return componentGroupRepository.getOne(id);
    }

    @Override
    public void delete(ComponentGroup object) {
        componentGroupRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        componentGroupRepository.deleteById(id);
    }
}
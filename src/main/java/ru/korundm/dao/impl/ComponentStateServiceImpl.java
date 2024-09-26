package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.ComponentStateService;
import ru.korundm.entity.ComponentState;
import ru.korundm.repository.ComponentStateRepository;

import java.util.List;

@Service
@Transactional
public class ComponentStateServiceImpl implements ComponentStateService {

    private final ComponentStateRepository componentStateRepository;

    public  ComponentStateServiceImpl (ComponentStateRepository componentStateRepository){
        this.componentStateRepository = componentStateRepository;
    }
    @Override
    public List<ComponentState> getAll() {
        return componentStateRepository.findAll();
    }

    @Override
    public List<ComponentState> getAllById(List<Long> idList) {
        return componentStateRepository.findAllById(idList);
    }

    @Override
    public ComponentState save(ComponentState object) {
        return componentStateRepository.save(object);
    }

    @Override
    public List<ComponentState> saveAll(List<ComponentState> objectList) {
        return componentStateRepository.saveAll(objectList);
    }

    @Override
    public ComponentState read(long id) {
        return componentStateRepository.getOne(id);
    }

    @Override
    public void delete(ComponentState object) {
        componentStateRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        componentStateRepository.deleteById(id);
    }

    @Override
    public ComponentState getByCode(String code) {
        return componentStateRepository.getFirstByCode(code);
    }
}

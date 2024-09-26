package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.ComponentKindService;
import ru.korundm.entity.ComponentKind;
import ru.korundm.repository.ComponentKindRepository;

import java.util.List;

@Service
@Transactional
public class ComponentKindServiceImpl implements ComponentKindService {

    private final ComponentKindRepository componentKindRepository;

    public ComponentKindServiceImpl(ComponentKindRepository componentKindRepository) {
        this.componentKindRepository = componentKindRepository;
    }

    @Override
    public List<ComponentKind> getAll() {
        return componentKindRepository.findAll();
    }

    @Override
    public List<ComponentKind> getAllById(List<Long> idList) {
        return componentKindRepository.findAllById(idList);
    }

    @Override
    public ComponentKind save(ComponentKind object) {
        return componentKindRepository.save(object);
    }

    @Override
    public List<ComponentKind> saveAll(List<ComponentKind> objectList) {
        return componentKindRepository.saveAll(objectList);
    }

    @Override
    public ComponentKind read(long id) {
        return componentKindRepository.getOne(id);
    }

    @Override
    public void delete(ComponentKind object) {
        componentKindRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        componentKindRepository.deleteById(id);
    }
}
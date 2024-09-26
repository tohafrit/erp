package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.ComponentMarkTypeService;
import ru.korundm.entity.ComponentMarkType;
import ru.korundm.repository.ComponentMarkTypeRepository;

import java.util.List;

@Service
@Transactional
public class ComponentMarkTypeServiceImpl implements ComponentMarkTypeService {

    private final ComponentMarkTypeRepository componentMarkTypeRepository;

    public ComponentMarkTypeServiceImpl(ComponentMarkTypeRepository componentMarkTypeRepository) {
        this.componentMarkTypeRepository = componentMarkTypeRepository;
    }

    @Override
    public List<ComponentMarkType> getAll() {
        return componentMarkTypeRepository.findAll();
    }

    @Override
    public List<ComponentMarkType> getAllById(List<Long> idList) {
        return componentMarkTypeRepository.findAllById(idList);
    }

    @Override
    public ComponentMarkType save(ComponentMarkType object) {
        return componentMarkTypeRepository.save(object);
    }

    @Override
    public List<ComponentMarkType> saveAll(List<ComponentMarkType> objectList) {
        return componentMarkTypeRepository.saveAll(objectList);
    }

    @Override
    public ComponentMarkType read(long id) {
        return componentMarkTypeRepository.getOne(id);
    }

    @Override
    public void delete(ComponentMarkType object) {
        componentMarkTypeRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        componentMarkTypeRepository.deleteById(id);
    }
}
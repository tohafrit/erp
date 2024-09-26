package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.ComponentAttributeValueService;
import ru.korundm.entity.ComponentAttributeValue;
import ru.korundm.repository.ComponentAttributeValueRepository;

import java.util.List;

@Service
@Transactional
public class ComponentAttributeValueServiceImpl implements ComponentAttributeValueService {

    private final ComponentAttributeValueRepository componentAttributeValueRepository;

    public ComponentAttributeValueServiceImpl(ComponentAttributeValueRepository componentAttributeValueRepository) {
        this.componentAttributeValueRepository = componentAttributeValueRepository;
    }

    @Override
    public List<ComponentAttributeValue> getAll() {
        return componentAttributeValueRepository.findAll();
    }

    @Override
    public List<ComponentAttributeValue> getAllById(List<Long> idList) {
        return componentAttributeValueRepository.findAllById(idList);
    }

    @Override
    public ComponentAttributeValue save(ComponentAttributeValue object) {
        return componentAttributeValueRepository.save(object);
    }

    @Override
    public List<ComponentAttributeValue> saveAll(List<ComponentAttributeValue> objectList) {
        return componentAttributeValueRepository.saveAll(objectList);
    }

    @Override
    public ComponentAttributeValue read(long id) {
        return componentAttributeValueRepository.findById(id).orElse(null);
    }

    @Override
    public void delete(ComponentAttributeValue object) {
        componentAttributeValueRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        componentAttributeValueRepository.deleteById(id);
    }

    @Override
    public List<ComponentAttributeValue> getAllByAttributeIdIn(List<Long> idList) {
        return componentAttributeValueRepository.findAllByAttributeIdIn(idList);
    }
}
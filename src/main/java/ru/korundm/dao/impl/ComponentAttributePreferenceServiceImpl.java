package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.ComponentAttributePreferenceService;
import ru.korundm.entity.ComponentAttributePreference;
import ru.korundm.repository.ComponentAttributePreferenceRepository;

import java.util.List;

@Service
@Transactional
public class ComponentAttributePreferenceServiceImpl implements ComponentAttributePreferenceService {

    private final ComponentAttributePreferenceRepository componentAttributePreferenceRepository;

    public ComponentAttributePreferenceServiceImpl(ComponentAttributePreferenceRepository componentAttributePreferenceRepository) {
        this.componentAttributePreferenceRepository = componentAttributePreferenceRepository;
    }

    @Override
    public List<ComponentAttributePreference> getAll() {
        return componentAttributePreferenceRepository.findAll();
    }

    @Override
    public List<ComponentAttributePreference> getAllById(List<Long> idList) {
        return componentAttributePreferenceRepository.findAllById(idList);
    }

    @Override
    public ComponentAttributePreference save(ComponentAttributePreference object) {
        return componentAttributePreferenceRepository.save(object);
    }

    @Override
    public List<ComponentAttributePreference> saveAll(List<ComponentAttributePreference> objectList) {
        return componentAttributePreferenceRepository.saveAll(objectList);
    }

    @Override
    public ComponentAttributePreference read(long id) {
        return componentAttributePreferenceRepository.findById(id).orElse(null);
    }

    @Override
    public void delete(ComponentAttributePreference object) {
        componentAttributePreferenceRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        componentAttributePreferenceRepository.deleteById(id);
    }

    @Override
    public List<ComponentAttributePreference> getAllByAttributeId(Long attributeId) {
        return componentAttributePreferenceRepository.findAllByAttributeId(attributeId);
    }

    @Override
    public List<ComponentAttributePreference> getAllByAttributeIdIn(List<Long> idList) {
        return componentAttributePreferenceRepository.findAllByAttributeIdIn(idList);
    }
}
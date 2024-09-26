package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.ComponentAttributeService;
import ru.korundm.entity.ComponentAttribute;
import ru.korundm.repository.ComponentAttributeRepository;

import java.util.List;

@Service
@Transactional
public class ComponentAttributeServiceImpl implements ComponentAttributeService {

    private final ComponentAttributeRepository componentAttributeRepository;

    public ComponentAttributeServiceImpl(ComponentAttributeRepository componentAttributeRepository) {
        this.componentAttributeRepository = componentAttributeRepository;
    }

    @Override
    public List<ComponentAttribute> getAll() {
        return componentAttributeRepository.findAll();
    }

    @Override
    public List<ComponentAttribute> getAllById(List<Long> idList) {
        return componentAttributeRepository.findAllById(idList);
    }

    @Override
    public ComponentAttribute save(ComponentAttribute object) {
        return componentAttributeRepository.save(object);
    }

    @Override
    public List<ComponentAttribute> saveAll(List<ComponentAttribute> objectList) {
        return componentAttributeRepository.saveAll(objectList);
    }

    @Override
    public ComponentAttribute read(long id) {
        return componentAttributeRepository.findById(id).orElse(null);
    }

    @Override
    public void delete(ComponentAttribute object) {
        componentAttributeRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        componentAttributeRepository.deleteById(id);
    }

    @Override
    public List<ComponentAttribute> getAllByCategoryId(Long categoryId) {
        return componentAttributeRepository.findAllByCategoryId(categoryId);
    }

    @Override
    public boolean existsByIdNotAndCategoryIdAndName(Long id, Long categoryId, String name) {
        return componentAttributeRepository.existsByIdNotAndCategoryIdAndName(id, categoryId, name);
    }

    @Override
    public boolean existsByCategoryIdAndName(Long categoryId, String name) {
        return componentAttributeRepository.existsByCategoryIdAndName(categoryId, name);
    }
}
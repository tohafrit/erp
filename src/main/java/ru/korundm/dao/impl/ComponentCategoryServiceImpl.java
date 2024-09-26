package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.ComponentCategoryService;
import ru.korundm.entity.ComponentCategory;
import ru.korundm.repository.ComponentCategoryRepository;

import java.util.List;

@Service
@Transactional
public class ComponentCategoryServiceImpl implements ComponentCategoryService {

    private final ComponentCategoryRepository componentCategoryRepository;

    public ComponentCategoryServiceImpl(ComponentCategoryRepository componentCategoryRepository) {
        this.componentCategoryRepository = componentCategoryRepository;
    }

    @Override
    public List<ComponentCategory> getAll() {
        return componentCategoryRepository.findAll();
    }

    @Override
    public List<ComponentCategory> getAllById(List<Long> idList) {
        return componentCategoryRepository.findAllById(idList);
    }

    @Override
    public ComponentCategory save(ComponentCategory object) {
        return componentCategoryRepository.save(object);
    }

    @Override
    public List<ComponentCategory> saveAll(List<ComponentCategory> objectList) {
        return componentCategoryRepository.saveAll(objectList);
    }

    @Override
    public ComponentCategory read(long id) {
        return componentCategoryRepository.findById(id).orElse(null);
    }

    @Override
    public void delete(ComponentCategory object) {
        componentCategoryRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        componentCategoryRepository.deleteById(id);
    }

    @Override
    public List<ComponentCategory> getAllByIdIsNotIn(List<Long> idList) {
        return componentCategoryRepository.findAllByIdIsNotIn(idList);
    }

    @Override
    public List<ComponentCategory> getAllByParentIsNull() {
        return componentCategoryRepository.findAllByParentIsNull();
    }

    @Override
    public List<Long> getAllSiblingsIdByParentId(Long parentId) {
        return componentCategoryRepository.findAllSiblingsIdByParentId(parentId);
    }
}
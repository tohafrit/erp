package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.CorporateDocumentCategoryService;
import ru.korundm.entity.CorporateDocumentCategory;
import ru.korundm.repository.CorporateDocumentCategoryRepository;

import java.util.List;

@Service
@Transactional
public class CorporateDocumentCategoryServiceImpl implements CorporateDocumentCategoryService {

    private final CorporateDocumentCategoryRepository corporateDocumentCategoryRepository;

    public CorporateDocumentCategoryServiceImpl(CorporateDocumentCategoryRepository corporateDocumentCategoryRepository) {
        this.corporateDocumentCategoryRepository = corporateDocumentCategoryRepository;
    }

    @Override
    public List<CorporateDocumentCategory> getAll() {
        return corporateDocumentCategoryRepository.findAll();
    }

    @Override
    public List<CorporateDocumentCategory> getAllById(List<Long> idList) {
        return corporateDocumentCategoryRepository.findAllById(idList);
    }

    @Override
    public CorporateDocumentCategory save(CorporateDocumentCategory object) {
        return corporateDocumentCategoryRepository.save(object);
    }

    @Override
    public List<CorporateDocumentCategory> saveAll(List<CorporateDocumentCategory> objectList) {
        return corporateDocumentCategoryRepository.saveAll(objectList);
    }

    @Override
    public CorporateDocumentCategory read(long id) {
        return corporateDocumentCategoryRepository.findById(id).orElse(null);
    }

    @Override
    public void delete(CorporateDocumentCategory object) {
        corporateDocumentCategoryRepository.delete(object);
    }

    @Override
    public void deleteById(long id) { corporateDocumentCategoryRepository.deleteById(id); }

    @Override
    public List<CorporateDocumentCategory> getAllByParentIsNull() {
        return corporateDocumentCategoryRepository.findAllByParentIsNull();
    }

    @Override
    public List<Long> getAllSiblingsIdByParentId(Long parentId) {
        return corporateDocumentCategoryRepository.findAllSiblingsIdByParentId(parentId);
    }
}
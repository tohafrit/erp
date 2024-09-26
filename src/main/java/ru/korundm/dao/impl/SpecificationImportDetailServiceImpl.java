package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.SpecificationImportDetailService;
import ru.korundm.entity.SpecificationImportDetail;
import ru.korundm.repository.SpecificationImportDetailRepository;

import java.util.List;

@Service
@Transactional
public class SpecificationImportDetailServiceImpl implements SpecificationImportDetailService {

    private final SpecificationImportDetailRepository specificationImportDetailRepository;

    public SpecificationImportDetailServiceImpl(SpecificationImportDetailRepository specificationImportDetailRepository) {
        this.specificationImportDetailRepository = specificationImportDetailRepository;
    }

    @Override
    public List<SpecificationImportDetail> getAll() {
        return specificationImportDetailRepository.findAll();
    }

    @Override
    public List<SpecificationImportDetail> getAllById(List<Long> idList) {
        return specificationImportDetailRepository.findAllById(idList);
    }

    @Override
    public SpecificationImportDetail save(SpecificationImportDetail object) {
        return specificationImportDetailRepository.save(object);
    }

    @Override
    public List<SpecificationImportDetail> saveAll(List<SpecificationImportDetail> objectList) {
        return specificationImportDetailRepository.saveAll(objectList);
    }

    @Override
    public SpecificationImportDetail read(long id) {
        return specificationImportDetailRepository.getOne(id);
    }

    @Override
    public void delete(SpecificationImportDetail object) {
        specificationImportDetailRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        specificationImportDetailRepository.deleteById(id);
    }

    @Override
    public List<SpecificationImportDetail> getAllByBomId(Long bomId) {
        return specificationImportDetailRepository.findAllByBomId(bomId);
    }

    @Override
    public boolean existsByBomId(Long bomId) {
        return specificationImportDetailRepository.existsByBomId(bomId);
    }

    @Override
    public void deleteAllByBomId(Long bomId) {
        specificationImportDetailRepository.deleteAllByBomId(bomId);
    }
}
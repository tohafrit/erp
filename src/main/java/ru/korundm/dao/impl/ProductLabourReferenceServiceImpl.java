package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.ProductLabourReferenceService;
import ru.korundm.entity.ProductLabourReference;
import ru.korundm.repository.ProductLabourReferenceRepository;

import java.util.List;

@Service
@Transactional
public class ProductLabourReferenceServiceImpl implements ProductLabourReferenceService {

    private final ProductLabourReferenceRepository productLabourReferenceRepository;

    public ProductLabourReferenceServiceImpl(ProductLabourReferenceRepository productLabourReferenceRepository) {
        this.productLabourReferenceRepository = productLabourReferenceRepository;
    }

    @Override
    public List<ProductLabourReference> getAll() {
        return productLabourReferenceRepository.findAll();
    }

    @Override
    public List<ProductLabourReference> getAllById(List<Long> idList) {
        return productLabourReferenceRepository.findAllById(idList);
    }

    @Override
    public ProductLabourReference save(ProductLabourReference object) {
        return productLabourReferenceRepository.save(object);
    }

    @Override
    public List<ProductLabourReference> saveAll(List<ProductLabourReference> objectList) {
        return productLabourReferenceRepository.saveAll(objectList);
    }

    @Override
    public ProductLabourReference read(long id) {
        return productLabourReferenceRepository.getOne(id);
    }

    @Override
    public void delete(ProductLabourReference object) {
        productLabourReferenceRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        productLabourReferenceRepository.deleteById(id);
    }
}
package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.ProductUnitService;
import ru.korundm.entity.ProductUnit;
import ru.korundm.repository.ProductUnitRepository;

import java.util.List;

@Service
@Transactional
public class ProductUnitServiceImpl implements ProductUnitService {

    private final ProductUnitRepository productUnitRepository;

    public ProductUnitServiceImpl(ProductUnitRepository productUnitRepository) {
        this.productUnitRepository = productUnitRepository;
    }

    @Override
    public List<ProductUnit> getAll() {
        return productUnitRepository.findAll();
    }

    @Override
    public List<ProductUnit> getAllById(List<Long> idList) {
        return productUnitRepository.findAllById(idList);
    }

    @Override
    public ProductUnit save(ProductUnit object) {
        return productUnitRepository.save(object);
    }

    @Override
    public List<ProductUnit> saveAll(List<ProductUnit> objectList) {
        return productUnitRepository.saveAll(objectList);
    }

    @Override
    public ProductUnit read(long id) {
        return productUnitRepository.getOne(id);
    }

    @Override
    public void delete(ProductUnit object) {
        productUnitRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        productUnitRepository.deleteById(id);
    }
}
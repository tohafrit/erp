package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.ProductionLotSpecService;
import ru.korundm.entity.ProductionLotSpec;
import ru.korundm.repository.ProductionLotSpecRepository;

import java.util.List;

@Service
@Transactional
public class ProductionLotSpecServiceImpl implements ProductionLotSpecService {

    private final ProductionLotSpecRepository productionLotSpecRepository;

    public ProductionLotSpecServiceImpl(ProductionLotSpecRepository productionLotSpecRepository) {
        this.productionLotSpecRepository = productionLotSpecRepository;
    }

    @Override
    public List<ProductionLotSpec> getAll() {
        return productionLotSpecRepository.findAll();
    }

    @Override
    public List<ProductionLotSpec> getAllById(List<Long> idList) {
        return productionLotSpecRepository.findAllById(idList);
    }

    @Override
    public ProductionLotSpec save(ProductionLotSpec object) {
        return productionLotSpecRepository.save(object);
    }

    @Override
    public List<ProductionLotSpec> saveAll(List<ProductionLotSpec> objectList) {
        return productionLotSpecRepository.saveAll(objectList);
    }

    @Override
    public ProductionLotSpec read(long id) {
        return productionLotSpecRepository.getOne(id);
    }

    @Override
    public void delete(ProductionLotSpec object) {
        productionLotSpecRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        productionLotSpecRepository.deleteById(id);
    }
}
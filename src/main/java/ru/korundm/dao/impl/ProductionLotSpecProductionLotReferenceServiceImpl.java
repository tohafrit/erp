package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.ProductionLotSpecProductionLotReferenceService;
import ru.korundm.entity.ProductionLotSpecProductionLotReference;
import ru.korundm.repository.ProductionLotSpecProductionLotReferenceRepository;

import java.util.List;

@Service
@Transactional
public class ProductionLotSpecProductionLotReferenceServiceImpl implements ProductionLotSpecProductionLotReferenceService {

    private final ProductionLotSpecProductionLotReferenceRepository productionLotSpecProductionLotReferenceRepository;

    public ProductionLotSpecProductionLotReferenceServiceImpl(ProductionLotSpecProductionLotReferenceRepository productionLotSpecProductionLotReferenceRepository) {
        this.productionLotSpecProductionLotReferenceRepository = productionLotSpecProductionLotReferenceRepository;
    }

    @Override
    public List<ProductionLotSpecProductionLotReference> getAll() {
        return productionLotSpecProductionLotReferenceRepository.findAll();
    }

    @Override
    public List<ProductionLotSpecProductionLotReference> getAllById(List<Long> idList) {
        return productionLotSpecProductionLotReferenceRepository.findAllById(idList);
    }

    @Override
    public ProductionLotSpecProductionLotReference save(ProductionLotSpecProductionLotReference object) {
        return productionLotSpecProductionLotReferenceRepository.save(object);
    }

    @Override
    public List<ProductionLotSpecProductionLotReference> saveAll(List<ProductionLotSpecProductionLotReference> objectList) {
        return productionLotSpecProductionLotReferenceRepository.saveAll(objectList);
    }

    @Override
    public ProductionLotSpecProductionLotReference read(long id) {
        return productionLotSpecProductionLotReferenceRepository.getOne(id);
    }

    @Override
    public void delete(ProductionLotSpecProductionLotReference object) {
        productionLotSpecProductionLotReferenceRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        productionLotSpecProductionLotReferenceRepository.deleteById(id);
    }

    @Override
    public void deleteAll(List<ProductionLotSpecProductionLotReference> objectList) {
        productionLotSpecProductionLotReferenceRepository.deleteAll(objectList);
    }
}
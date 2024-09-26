package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.ProductionDefectService;
import ru.korundm.entity.ProductionDefect;
import ru.korundm.repository.ProductionDefectRepository;

import java.util.List;

@Service
@Transactional
public class ProductionDefectServiceImpl implements ProductionDefectService {

    private final ProductionDefectRepository  productionDefectRepository;

    public ProductionDefectServiceImpl(ProductionDefectRepository productionDefectRepository) {
        this.productionDefectRepository = productionDefectRepository;
    }

    @Override
    public List<ProductionDefect> getAll() {
        return productionDefectRepository.findAll();
    }

    @Override
    public List<ProductionDefect> getAllById(List<Long> idList) { return productionDefectRepository.findAllById(idList); }

    @Override
    public ProductionDefect save(ProductionDefect object) {
        return productionDefectRepository.save(object);
    }

    @Override
    public List<ProductionDefect> saveAll(List<ProductionDefect> objectList) { return productionDefectRepository.saveAll(objectList); }

    @Override
    public ProductionDefect read(long id) {
        return productionDefectRepository.getOne(id);
    }

    @Override
    public void delete(ProductionDefect object) {
        productionDefectRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        productionDefectRepository.deleteById(id);
    }
}
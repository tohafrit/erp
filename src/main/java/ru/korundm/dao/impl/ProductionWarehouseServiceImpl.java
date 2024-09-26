package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.ProductionWarehouseService;
import ru.korundm.entity.ProductionWarehouse;
import ru.korundm.repository.ProductionWarehouseRepository;

import java.util.List;

@Service
@Transactional
public class ProductionWarehouseServiceImpl implements ProductionWarehouseService {

    private final ProductionWarehouseRepository repository;

    public ProductionWarehouseServiceImpl(ProductionWarehouseRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ProductionWarehouse> getAll() {
        return repository.findAll();
    }

    @Override
    public List<ProductionWarehouse> getAllById(List<Long> idList) { return repository.findAllById(idList); }

    @Override
    public ProductionWarehouse save(ProductionWarehouse object) {
        return repository.save(object);
    }

    @Override
    public List<ProductionWarehouse> saveAll(List<ProductionWarehouse> objectList) { return repository.saveAll(objectList); }

    @Override
    public ProductionWarehouse read(long id) {
        return repository.getOne(id);
    }

    @Override
    public void delete(ProductionWarehouse object) {
        repository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsByCodeAndIdNot(Integer code, Long id) {
        return id == null ? repository.existsByCode(code) : repository.existsByCodeAndIdNot(code, id);
    }
}
package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.ProductKindService;
import ru.korundm.entity.ProductKind;
import ru.korundm.repository.ProductKindRepository;

import java.util.List;

@Service
@Transactional
public class ProductKindServiceImpl implements ProductKindService {

    private final ProductKindRepository productKindRepository;

    public ProductKindServiceImpl(ProductKindRepository productKindRepository) {
        this.productKindRepository = productKindRepository;
    }

    @Override
    public List<ProductKind> getAll() {
        return productKindRepository.findAll();
    }

    @Override
    public List<ProductKind> getAllById(List<Long> idList) {
        return productKindRepository.findAllById(idList);
    }

    @Override
    public ProductKind save(ProductKind object) {
        return productKindRepository.save(object);
    }

    @Override
    public List<ProductKind> saveAll(List<ProductKind> objectList) {
        return productKindRepository.saveAll(objectList);
    }

    @Override
    public ProductKind read(long id) {
        return productKindRepository.getOne(id);
    }

    @Override
    public void delete(ProductKind object) {
        productKindRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        productKindRepository.deleteById(id);
    }

    @Override
    public Long getMaxId() {
        return productKindRepository.getMaxId();
    }
}
package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.ProductTechnicalProcessService;
import ru.korundm.entity.Justification;
import ru.korundm.entity.ProductTechnicalProcess;
import ru.korundm.repository.ProductTechnicalProcessRepository;

import java.util.List;

@Service
@Transactional
public class ProductTechnicalProcessServiceImpl implements ProductTechnicalProcessService {

    private final ProductTechnicalProcessRepository productTechnicalProcessRepository;

    public ProductTechnicalProcessServiceImpl(ProductTechnicalProcessRepository productTechnicalProcessRepository) {
        this.productTechnicalProcessRepository = productTechnicalProcessRepository;
    }

    @Override
    public List<ProductTechnicalProcess> getAll() {
        return productTechnicalProcessRepository.findAll();
    }

    @Override
    public List<ProductTechnicalProcess> getAllById(List<Long> idList) {
        return productTechnicalProcessRepository.findAllById(idList);
    }

    @Override
    public ProductTechnicalProcess save(ProductTechnicalProcess object) {
        return productTechnicalProcessRepository.save(object);
    }

    @Override
    public List<ProductTechnicalProcess> saveAll(List<ProductTechnicalProcess> objectList) {
        return productTechnicalProcessRepository.saveAll(objectList);
    }

    @Override
    public ProductTechnicalProcess read(long id) {
        return productTechnicalProcessRepository.getOne(id);
    }

    @Override
    public void delete(ProductTechnicalProcess object) {
        productTechnicalProcessRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        productTechnicalProcessRepository.deleteById(id);
    }

    @Override
    public List<ProductTechnicalProcess> getAllByJustification(Justification justification) {
        return productTechnicalProcessRepository.findAllByJustification(justification);
    }

    @Override
    public void setApprovedById(Boolean approved, List<Long> ids) {
        productTechnicalProcessRepository.setApprovedById(approved, ids);
    }

    @Override
    public List<ProductTechnicalProcess> getAllByParams(Justification justification, Long id) {
        return productTechnicalProcessRepository.findAllByJustificationAndIdNot(justification, id);
    }
}
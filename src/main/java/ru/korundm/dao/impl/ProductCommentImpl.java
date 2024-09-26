package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.ProductCommentService;
import ru.korundm.entity.ProductComment;
import ru.korundm.repository.ProductCommentRepository;

import java.util.List;

@Service
@Transactional
public class ProductCommentImpl implements ProductCommentService {

    private final ProductCommentRepository productCommentRepository;

    public ProductCommentImpl(ProductCommentRepository productCommentRepository) {
        this.productCommentRepository = productCommentRepository;
    }

    @Override
    public List<ProductComment> getAll() {
        return productCommentRepository.findAll();
    }

    @Override
    public List<ProductComment> getAllById(List<Long> idList) {
        return productCommentRepository.findAllById(idList);
    }

    @Override
    public ProductComment save(ProductComment object) {
        return productCommentRepository.save(object);
    }

    @Override
    public List<ProductComment> saveAll(List<ProductComment> objectList) {
        return productCommentRepository.saveAll(objectList);
    }

    @Override
    public ProductComment read(long id) {
        return productCommentRepository.findById(id).orElse(null);
    }

    @Override
    public void delete(ProductComment object) {
        productCommentRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        productCommentRepository.deleteById(id);
    }

    @Override
    public List<String> getDistinctUser() {
        return productCommentRepository.findDistinctUser();
    }

    @Override
    public List<ProductComment> getAllByProduct(Long letterId) {
        return productCommentRepository.findByProduct_Id(letterId);
    }
}
package ru.korundm.dao;

import ru.korundm.entity.ProductComment;

import java.util.List;

public interface ProductCommentService extends CommonService<ProductComment> {

    List<String> getDistinctUser();

    List<ProductComment> getAllByProduct(Long productId);
}
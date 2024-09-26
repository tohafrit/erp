package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.korundm.entity.ProductComment;

import java.util.List;

public interface ProductCommentRepository extends JpaRepository<ProductComment, Long> {

    @Query("select distinct user.lastName from ProductComment ")
    List<String> findDistinctUser();

    List<ProductComment> findByProduct_Id(Long productId);
}
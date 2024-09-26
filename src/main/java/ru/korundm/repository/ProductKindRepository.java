package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.korundm.entity.ProductKind;

public interface ProductKindRepository extends JpaRepository<ProductKind, Long> {

    @Query("SELECT MAX(id) FROM ProductKind")
    Long getMaxId();
}
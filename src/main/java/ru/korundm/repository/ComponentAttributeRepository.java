package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.ComponentAttribute;

import java.util.List;

public interface ComponentAttributeRepository extends JpaRepository<ComponentAttribute, Long> {

    List<ComponentAttribute> findAllByCategoryId(Long categoryId);

    boolean existsByIdNotAndCategoryIdAndName(Long id, Long categoryId, String name);

    boolean existsByCategoryIdAndName(Long categoryId, String name);
}
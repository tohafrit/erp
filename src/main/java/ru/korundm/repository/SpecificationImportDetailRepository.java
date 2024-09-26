package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.SpecificationImportDetail;

import java.util.List;

public interface SpecificationImportDetailRepository extends JpaRepository<SpecificationImportDetail, Long> {

    List<SpecificationImportDetail> findAllByBomId(Long bomId);

    boolean existsByBomId(Long bomId);

    void deleteAllByBomId(Long bomId);
}
package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.korundm.entity.Justification;
import ru.korundm.entity.ProductTechnicalProcess;

import java.util.List;

public interface ProductTechnicalProcessRepository extends JpaRepository<ProductTechnicalProcess, Long> {

    List<ProductTechnicalProcess> findAllByJustification(Justification justification);

    @Modifying
    @Query("update ProductTechnicalProcess p set p.approved = ?1 where p.id in (?2)")
    void setApprovedById(Boolean approved, List<Long> ids);

    List<ProductTechnicalProcess> findAllByJustificationAndIdNot(Justification justification, Long id);
}
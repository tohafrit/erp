package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.LaboriousnessCalculation;

import java.util.List;

public interface LaboriousnessCalculationRepository extends JpaRepository<LaboriousnessCalculation, Long> {

    LaboriousnessCalculation findFirstByProductTechnicalProcess_IdAndParentIsNull(long id);

    List<LaboriousnessCalculation> findAllByIdIsNotIn(List<Long> idList);
}
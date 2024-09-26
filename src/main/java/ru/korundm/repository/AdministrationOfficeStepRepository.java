package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.AdministrationOfficeStep;

public interface AdministrationOfficeStepRepository extends JpaRepository<AdministrationOfficeStep, Long> {

    AdministrationOfficeStep findTopByDemand_IdOrderByIdDesc(Long administrationOfficeDemandId);
}
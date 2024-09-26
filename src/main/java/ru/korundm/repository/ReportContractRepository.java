package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.ContractSection;
import ru.korundm.entity.ReportContract;

public interface ReportContractRepository extends JpaRepository<ReportContract, Long> {

    ReportContract findFirstByContractSection(ContractSection contractSection);
}
package ru.korundm.dao;

import ru.korundm.entity.ContractSection;
import ru.korundm.entity.ReportContract;

public interface ReportContractService extends CommonService<ReportContract>  {

    ReportContract getByContractSection(ContractSection contractSection);
}
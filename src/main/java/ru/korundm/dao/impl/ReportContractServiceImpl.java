package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.ReportContractService;
import ru.korundm.entity.ContractSection;
import ru.korundm.entity.ReportContract;
import ru.korundm.repository.ReportContractRepository;

import java.util.List;

@Service
@Transactional
public class ReportContractServiceImpl implements ReportContractService {

    private final ReportContractRepository reportContractRepository;

    public ReportContractServiceImpl(ReportContractRepository reportContractRepository) {
        this.reportContractRepository = reportContractRepository;
    }

    @Override
    public List<ReportContract> getAll() {
        return reportContractRepository.findAll();
    }

    @Override
    public List<ReportContract> getAllById(List<Long> idList) {
        return reportContractRepository.findAllById(idList);
    }

    @Override
    public ReportContract save(ReportContract object) {
        return reportContractRepository.save(object);
    }

    @Override
    public List<ReportContract> saveAll(List<ReportContract> objectList) {
        return reportContractRepository.saveAll(objectList);
    }

    @Override
    public ReportContract read(long id) {
        return reportContractRepository.getOne(id);
    }

    @Override
    public void delete(ReportContract object) {
        reportContractRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        reportContractRepository.deleteById(id);
    }

    @Override
    public ReportContract getByContractSection(ContractSection contractSection) {
        return reportContractRepository.findFirstByContractSection(contractSection);
    }
}
package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.LaboriousnessCalculationService;
import ru.korundm.entity.LaboriousnessCalculation;
import ru.korundm.repository.LaboriousnessCalculationRepository;

import java.util.List;

@Service
@Transactional
public class LaboriousnessCalculationServiceImpl implements LaboriousnessCalculationService {

    private final LaboriousnessCalculationRepository laboriousnessCalculationRepository;

    public LaboriousnessCalculationServiceImpl(LaboriousnessCalculationRepository laboriousnessCalculationRepository) {
        this.laboriousnessCalculationRepository = laboriousnessCalculationRepository;
    }

    @Override
    public List<LaboriousnessCalculation> getAll() {
        return laboriousnessCalculationRepository.findAll();
    }

    @Override
    public List<LaboriousnessCalculation> getAllById(List<Long> idList) {
        return laboriousnessCalculationRepository.findAllById(idList);
    }

    @Override
    public LaboriousnessCalculation save(LaboriousnessCalculation object) {
        return laboriousnessCalculationRepository.save(object);
    }

    @Override
    public List<LaboriousnessCalculation> saveAll(List<LaboriousnessCalculation> objectList) {
        return laboriousnessCalculationRepository.saveAll(objectList);
    }

    @Override
    public LaboriousnessCalculation read(long id) {
        return laboriousnessCalculationRepository.getOne(id);
    }

    @Override
    public void delete(LaboriousnessCalculation object) {
        laboriousnessCalculationRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        laboriousnessCalculationRepository.deleteById(id);
    }

    @Override
    public LaboriousnessCalculation getByProductTechnicalProcessId(long id) {
        return laboriousnessCalculationRepository.findFirstByProductTechnicalProcess_IdAndParentIsNull(id);
    }

    @Override
    public List<LaboriousnessCalculation> getAllByIdIsNotIn(List<Long> idList) {
        return laboriousnessCalculationRepository.findAllByIdIsNotIn(idList);
    }
}
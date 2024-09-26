package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.AdministrationOfficeStepService;
import ru.korundm.entity.AdministrationOfficeStep;
import ru.korundm.repository.AdministrationOfficeStepRepository;

import java.util.List;

@Service
@Transactional
public class AdministrationOfficeStepServiceImpl implements AdministrationOfficeStepService {

    private final AdministrationOfficeStepRepository administrationOfficeStepRepository;

    public AdministrationOfficeStepServiceImpl(AdministrationOfficeStepRepository administrationOfficeStepRepository) {
        this.administrationOfficeStepRepository = administrationOfficeStepRepository;
    }

    @Override
    public List<AdministrationOfficeStep> getAll() {
        return administrationOfficeStepRepository.findAll();
    }

    @Override
    public List<AdministrationOfficeStep> getAllById(List<Long> idList) {
        return administrationOfficeStepRepository.findAllById(idList);
    }

    @Override
    public AdministrationOfficeStep save(AdministrationOfficeStep object) {
        return administrationOfficeStepRepository.save(object);
    }

    @Override
    public AdministrationOfficeStep read(long id) {
        return administrationOfficeStepRepository.getOne(id);
    }

    @Override
    public void delete(AdministrationOfficeStep object) {
        administrationOfficeStepRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        administrationOfficeStepRepository.deleteById(id);
    }

    @Override
    public List<AdministrationOfficeStep> saveAll(List<AdministrationOfficeStep> list) {
        return administrationOfficeStepRepository.saveAll(list);
    }

    @Override
    public AdministrationOfficeStep getLastByDemand(Long demandId) {
        return administrationOfficeStepRepository.findTopByDemand_IdOrderByIdDesc(demandId);
    }
}
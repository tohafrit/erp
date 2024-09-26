package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.LabourService;
import ru.korundm.entity.Labour;
import ru.korundm.repository.LabourRepository;

import java.util.List;

@Service
@Transactional
public class LabourServiceImpl implements LabourService {

    private final LabourRepository labourRepository;

    public LabourServiceImpl(LabourRepository labourRepository) {
        this.labourRepository = labourRepository;
    }

    @Override
    public List<Labour> getAll() {
        return labourRepository.findAll();
    }

    @Override
    public List<Labour> getAllById(List<Long> idList) {
        return labourRepository.findAllById(idList);
    }

    @Override
    public Labour save(Labour object) {
        return labourRepository.save(object);
    }

    @Override
    public List<Labour> saveAll(List<Labour> objectList) {
        return labourRepository.saveAll(objectList);
    }

    @Override
    public Labour read(long id) {
        return labourRepository.getOne(id);
    }

    @Override
    public void delete(Labour object) {
        labourRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        labourRepository.deleteById(id);
    }
}
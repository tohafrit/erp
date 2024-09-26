package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.LaboriousnessService;
import ru.korundm.entity.Laboriousness;
import ru.korundm.repository.LaboriousnessRepository;

import java.util.List;

@Service
@Transactional
public class LaboriousnessServiceImpl implements LaboriousnessService {

    private final LaboriousnessRepository laboriousnessRepository;

    public LaboriousnessServiceImpl(LaboriousnessRepository laboriousnessRepository) {
        this.laboriousnessRepository = laboriousnessRepository;
    }

    @Override
    public List<Laboriousness> getAll() {
        return laboriousnessRepository.findAll();
    }

    @Override
    public List<Laboriousness> getAllById(List<Long> idList) {
        return laboriousnessRepository.findAllById(idList);
    }

    @Override
    public Laboriousness save(Laboriousness object) {
        return laboriousnessRepository.save(object);
    }

    @Override
    public List<Laboriousness> saveAll(List<Laboriousness> objectList) {
        return laboriousnessRepository.saveAll(objectList);
    }

    @Override
    public Laboriousness read(long id) {
        return laboriousnessRepository.getOne(id);
    }

    @Override
    public void delete(Laboriousness object) {
        laboriousnessRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        laboriousnessRepository.deleteById(id);
    }
}
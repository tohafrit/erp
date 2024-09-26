package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.LabourProtocolService;
import ru.korundm.entity.LabourProtocol;
import ru.korundm.repository.LabourProtocolRepository;

import java.util.List;

@Service
@Transactional
public class LabourProtocolServiceImpl implements LabourProtocolService {

    private final LabourProtocolRepository labourProtocolRepository;

    public LabourProtocolServiceImpl(LabourProtocolRepository labourProtocolRepository) {
        this.labourProtocolRepository = labourProtocolRepository;
    }

    @Override
    public List<LabourProtocol> getAll() {
        return labourProtocolRepository.findAll();
    }

    @Override
    public List<LabourProtocol> getAllById(List<Long> idList) {
        return labourProtocolRepository.findAllById(idList);
    }

    @Override
    public LabourProtocol save(LabourProtocol object) {
        return labourProtocolRepository.save(object);
    }

    @Override
    public List<LabourProtocol> saveAll(List<LabourProtocol> objectList) {
        return labourProtocolRepository.saveAll(objectList);
    }

    @Override
    public LabourProtocol read(long id) {
        return labourProtocolRepository.getOne(id);
    }

    @Override
    public void delete(LabourProtocol object) {
        labourProtocolRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        labourProtocolRepository.deleteById(id);
    }
}
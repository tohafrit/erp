package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.SnapshotParameterService;
import ru.korundm.entity.SnapshotParameter;
import ru.korundm.repository.SnapshotParameterRepository;

import java.util.List;

@Service
@Transactional
public class SnapshotParameterServiceImpl implements SnapshotParameterService {

    private final SnapshotParameterRepository snapshotParameterRepository;

    public SnapshotParameterServiceImpl(SnapshotParameterRepository snapshotParameterRepository) {
        this.snapshotParameterRepository = snapshotParameterRepository;
    }

    @Override
    public void deleteAll() {
        snapshotParameterRepository.deleteAll();
    }

    @Override
    public List<SnapshotParameter> getAll() {
        return snapshotParameterRepository.findAll();
    }

    @Override
    public List<SnapshotParameter> getAllById(List<Long> idList) {
        return snapshotParameterRepository.findAllById(idList);
    }

    @Override
    public SnapshotParameter save(SnapshotParameter object) {
        return snapshotParameterRepository.save(object);
    }

    @Override
    public List<SnapshotParameter> saveAll(List<SnapshotParameter> objectList) {
        return snapshotParameterRepository.saveAll(objectList);
    }

    @Override
    public SnapshotParameter read(long id) {
        return snapshotParameterRepository.getOne(id);
    }

    @Override
    public void delete(SnapshotParameter object) {
        snapshotParameterRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        snapshotParameterRepository.deleteById(id);
    }
}